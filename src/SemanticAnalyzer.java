import nodes.conditionals.CondNode;
import nodes.expresions.ExprNode;
import nodes.expresions.RefNode;
import nodes.instructions.InstrNode;
import nodes.loops.LoopNode;
import simbols.*;
import nodes.*;
import simbols.descripcio.*;
import java.util.*;
import errors.*;

public class SemanticAnalyzer {
    private SymbolTable currentScope;

    private void openScope() {
        currentScope = new SymbolTable(currentScope);
    }

    private void closeScope() {
        if (currentScope.getParent() != null) {
            SymbolTable.registerClosedScope(currentScope);
            currentScope = currentScope.getParent();
        } else {
            ErrorManager.add(new CompilerError(CompilerError.TYPE.SEMANTIC,
                    "Error intern: intent de tancar l'àmbit global"));
        }
    }

    public SemanticAnalyzer() {
        this.currentScope = new SymbolTable(null);
        currentScope.inicialitzaValorsEstandard();
    }

    public void analyze(ProgramNode p) {
        gest_program(p);
    }

    // ─────────────────────────────────────────────
    // Nivell 1: programa
    // ─────────────────────────────────────────────
    public void gest_program(ProgramNode p) {
        gest_decls(p.getDecls());
        gest_methods(p.getMethods());
        gest_instrs(p.getInstrs());
        SymbolTable.registerClosedScope(currentScope);
    }

    // ─────────────────────────────────────────────
    // Declaracions globals
    // ─────────────────────────────────────────────
    public void gest_decls(List<DeclNode> decls) {
        if (decls == null) return;
        for (DeclNode decl : decls) gest_decl(decl);
    }

    public void gest_decl(DeclNode decl) {
        if (decl == null) return;

        if (decl.isConst()) {
            gest_decl_const(decl);
            return;
        }

        if (decl.getType().getKind() == TypeNode.Kind.TUPLA) {
            novaTupla(decl);
            return;
        }

        DescripcioTipus tipusDeclarat = cercaTipus(decl.getType().getKind(), decl.getType().getCustomTypeName());

        ExprNode assig = decl.getExpr();

        if (decl.getType().getKind() == TypeNode.Kind.USER && assig != null) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "Les variables de tipus '" + decl.getType().getCustomTypeName() +
                            "' (tuples o tipus definits) no poden inicialitzar-se directament."
            ));
            decl.setHasError(true);
            assig = null; // ignora l'expressió
        }

        if (assig != null) {
            gest_expr(assig);
            if (assig.hasError()) decl.setHasError(true);
        }

        if (!decl.hasError() && assig != null) {
            DescripcioTipus tipusExpr = assig.getDescripcioTipus();
            if (!TipusUtils.sonCompatibles(tipusDeclarat, tipusExpr)) {
                ErrorManager.add(new CompilerError(
                        assig.line, assig.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus incompatible: s'espera " + tipusDeclarat.getNomTipus() +
                                " però s'ha trobat " + tipusExpr.getNomTipus()));
                decl.setHasError(true);
            }
        }

        DescripcioVar desc = new DescripcioVar(decl.hasError() ? cercaTipus(TypeNode.Kind.UNKNOWN) : tipusDeclarat);
        if (assig != null) desc.setInitialized(true);

        if (!currentScope.add(new Simbol(decl.getId(), desc, currentScope.getScopeId()))) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "La variable '" + decl.getId() + "' ja ha estat declarada."
            ));
            decl.setHasError(true);
        }
    }

    private void novaTupla(DeclNode decl) {
        TypeNode tipusNode = decl.getType();
        String nomTupla = decl.getId();

        if (decl.getExpr() != null) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "Les definicions de tipus 'tupla' no poden tenir una assignació."
            ));
            decl.setHasError(true);
            return;
        }

        // crear descripció base
        DescripcioTipus descTupla = new DescripcioTipus(nomTupla, TypeNode.Kind.TUPLA, 0);

        // construir camps
        List<DescripcioTipus.CampRecord> camps = new ArrayList<>();
        Set<String> nomsCamps = new HashSet<>();
        int offset = 0;

        for (ArgNode camp : tipusNode.getFields()) {
            if (!nomsCamps.add(camp.getName())) {
                ErrorManager.add(new CompilerError(
                        camp.line, camp.column, CompilerError.TYPE.SEMANTIC,
                        "Camp duplicat '" + camp.getName() + "' dins la tupla '" + nomTupla + "'."
                ));
                decl.setHasError(true);
                continue;
            }

            DescripcioTipus descCamp = cercaTipus(camp.getType().getKind(), camp.getType().getCustomTypeName());
            if (descCamp == null) {
                ErrorManager.add(new CompilerError(
                        camp.line, camp.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus del camp '" + camp.getName() + "' no declarat."
                ));
                decl.setHasError(true);
                continue;
            }

            camps.add(new DescripcioTipus.CampRecord(camp.getName(), descCamp, offset));
            offset += descCamp.getOcupacio();
        }

        if (decl.hasError()) return;

        descTupla.setCamps(camps);
        descTupla.setOcupacio(offset > 0 ? offset : 1);

        Simbol s = new Simbol(nomTupla, descTupla, currentScope.getScopeId());
        if (!currentScope.add(s)) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "La tupla '" + decl.getId() + "' ja ha estat declarada."
            ));
            decl.setHasError(true);
        }
    }

    // DECL -> CONST TIPUS ID := E
    public void gest_decl_const(DeclNode decl) {
        if (decl == null) return;

        Simbol s = currentScope.lookUp(decl.getType().getLookupName());

        // Cercar el tipus declarat
        if (s == null) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus '" + decl.getType().getLookupName() + "' no està declarat."
            ));
            decl.setHasError(true);
            return;
        }

        // Comprovar que el tipus és una descripció de tipus
        if(!(s.getDescripcio() instanceof DescripcioTipus dt)) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "El símbol '" + decl.getType().getLookupName() + "' no és un tipus vàlid."
            ));
            decl.setHasError(true);
            return;
        }

        TypeNode.Kind tsb = dt.getTipusBase();

        // Comprovar que el tipus és l'adequat
        if (tsb != TypeNode.Kind.ENTER && tsb != TypeNode.Kind.CADENA && tsb != TypeNode.Kind.CARACTER
                && tsb != TypeNode.Kind.LOGIC) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus '" + tsb + "' no pot emprar-se per declarar una constant."
            ));
            decl.setHasError(true);
            return;
        }

        // Analitzar l'expressió associada
        ExprNode assig = decl.getExpr();
        gest_expr(assig);

        // Comprovar compatibilitat de tipus (valor i id)
        if (!TipusUtils.sonCompatibles(dt, assig.getDescripcioTipus())) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus de l'expressió no és compatible amb el tipus de la constant '" + decl.getId() + "'."
            ));
            decl.setHasError(true);
        }


        Object valor;
        if (assig instanceof ExprNode.LiteralNode lit) {
            valor = lit.getValue();

            int v = (Integer) valor;
            if (v < dt.getLimitInf() || v > dt.getLimitSup()) {
                ErrorManager.add(new CompilerError(
                        decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                        "El valor " + v + " està fora del rang permès per al tipus '" + dt.getNomTipus() + "'."
                ));
                decl.setHasError(true);
            } else if ((Integer) valor > dt.getLimitSup()) {
                ErrorManager.add(new CompilerError(decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                        " "));
            }
        } else if (assig instanceof RefNode r) {
            Simbol sr = currentScope.lookUp(r.getId());

            if(!(sr.getDescripcio() instanceof DescripcioConst)) {
                ErrorManager.add(new CompilerError(decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                        " "));
                decl.setHasError(true);
            } else {

            }
        }


        if (!decl.hasError()) {
            // Afegir la declaració si tot és correcte
            DescripcioConst dc = new DescripcioConst(dt, valor);
            if (!currentScope.add(new Simbol(decl.getId(), dc, currentScope.getScopeId()))) {
                ErrorManager.add(new CompilerError(
                        decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                        "La constant '" + decl.getId() + "' ja ha estat declarada."
                ));
                decl.setHasError(true);
            }
        }
    }


    public void gest_methods(List<MethodNode> methods) {
        if (methods == null) return;
        for (MethodNode method : methods) gest_method(method);
    }

    public void gest_method(MethodNode m) {
        if (m == null) return;

        DescripcioTipus tipusRetorn = null;
        if (m.getType() != null) {
            tipusRetorn = cercaTipus(m.getType().getKind());
            if (tipusRetorn == null) {
                ErrorManager.add(new CompilerError(
                        m.line, m.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus de retorn inexistent: " + m.getType().getLookupName()
                ));
                tipusRetorn = cercaTipus(TypeNode.Kind.UNKNOWN);
                m.setHasError(true);
            }
        }

        DescripcioProc descProc = new DescripcioProc(
                tipusRetorn,
                m.isFunction(),
                m.getParams() != null ? m.getParams() : new ArrayList<>()
        );

        Simbol sProc = new Simbol(m.getName(), descProc, currentScope.getScopeId());
        if (!currentScope.add(sProc)) {
            ErrorManager.add(new CompilerError(
                    m.line, m.column, CompilerError.TYPE.SEMANTIC,
                    "Nom de subprograma ja utilitzat: " + m.getName()
            ));
            m.setHasError(true);
        }

        openScope();

        if (m.getParams() != null) {
            for (ArgNode arg : m.getParams()) {
                String name = arg.getName();
                DescripcioTipus tipusArg = null;

                if (arg.getType() != null)
                    tipusArg = cercaTipus(arg.getType().getKind());

                if (tipusArg == null) {
                    ErrorManager.add(new CompilerError(
                            arg.line, arg.column, CompilerError.TYPE.SEMANTIC,
                            "Tipus d'argument inexistent: " +
                                    (arg.getType() != null ? arg.getType().getLookupName() : "<desconegut>")
                    ));
                    tipusArg = cercaTipus(TypeNode.Kind.UNKNOWN);
                    m.setHasError(true);
                }

                DescripcioArg descArg = new DescripcioArg(name, tipusArg);
                Simbol sArg = new Simbol(name, descArg, currentScope.getScopeId());

                if (!currentScope.add(sArg)) {
                    ErrorManager.add(new CompilerError(
                            arg.line, arg.column, CompilerError.TYPE.SEMANTIC,
                            "Argument ja declarat: " + name
                    ));
                    m.setHasError(true);
                }
            }
        }

        gest_instrs(m.getInstrs());

        if (m.isFunction()) {
            ExprNode retExpr = m.getReturnExpr();

            if (retExpr == null) {
                ErrorManager.add(new CompilerError(
                        m.line, m.column, CompilerError.TYPE.SEMANTIC,
                        "Falta l'expressió de retorn a la funció " + m.getName()
                ));
                m.setHasError(true);
            } else {
                gest_expr(retExpr);
                if (retExpr.hasError()) {
                    m.setHasError(true);
                } else if (!TipusUtils.sonCompatibles(tipusRetorn, retExpr.getDescripcioTipus())) {
                    ErrorManager.add(new CompilerError(
                            retExpr.line, retExpr.column, CompilerError.TYPE.SEMANTIC,
                            "Tipus de retorn incompatible: s'espera " + tipusRetorn.getNomTipus() +
                                    " però s'ha trobat " + retExpr.getDescripcioTipus().getNomTipus()
                    ));
                    m.setHasError(true);
                }
            }
        }

        closeScope();
    }


    public void gest_instrs(List<InstrNode> instrs) {
        if (instrs == null) return;
        for (InstrNode instr : instrs) gest_instr(instr);
    }

    public void gest_instr(InstrNode i) {
        if (i instanceof InstrNode.AssignNode assign) {
            gest_assign(assign);
        } else if (i instanceof InstrNode.CallNode call) {
            gest_call(call);
        } else if (i instanceof InstrNode.InputNode input) {
            gest_input(input);
        } else if (i instanceof InstrNode.OutputNode output) {
            gest_output(output);
        } else if (i instanceof CondNode cond) {
            gest_cond(cond);
        } else if (i instanceof LoopNode loop) {
            gest_loop(loop);
        } else if (i instanceof InstrNode.InstrDeclNode decl) {
            gest_decl(decl.getDecl());
        }
    }


    public void gest_assign(InstrNode.AssignNode a) {
        if (a == null) return;

        RefNode ref = a.getRef();
        ExprNode expr = a.getExpr();

        gest_ref(ref);

        if (ref.hasError()) {
            a.setHasError(true);
            return;
        }

        switch (ref.getModeRef()) {
            case CONST -> {
                ErrorManager.add(new CompilerError(
                        a.line, a.column, CompilerError.TYPE.SEMANTIC,
                        "No es pot assignar a una constant: " + ref.getId()
                ));
                a.setHasError(true);
            }
            case PROCF, PROCC -> {
                ErrorManager.add(new CompilerError(
                        a.line, a.column, CompilerError.TYPE.SEMANTIC,
                        "No es pot assignar a un subprograma: " + ref.getId()
                ));
                a.setHasError(true);
            }
            default -> {}
        }

        gest_expr(expr);
        if (expr.hasError()) {
            a.setHasError(true);
            return;
        }

        DescripcioTipus tipusRef = ref.getDescripcioTipus();
        DescripcioTipus tipusExpr = expr.getDescripcioTipus();

        if (tipusRef.getTipusBase() == TypeNode.Kind.UNKNOWN ||
                tipusExpr.getTipusBase() == TypeNode.Kind.UNKNOWN)
        {
            ErrorManager.add(new CompilerError(
                    a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "Tipus desconegut en l'assignació a '" + ref.getId() + "'"
            ));
            a.setHasError(true);
        }

        if (!a.hasError() && !TipusUtils.sonCompatibles(tipusRef, tipusExpr)) {
            ErrorManager.add(new CompilerError(
                    a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "Tipus incompatible: s'intenta assignar una expressió de tipus " +
                            tipusExpr.getNomTipus() + " a '" + ref.getId() + "' de tipus " +
                            tipusRef.getNomTipus()
            ));
            a.setHasError(true);
        }

        TypeNode.Kind tsb = tipusRef.getTipusBase();
        if (!(tsb == TypeNode.Kind.INTEGER || tsb == TypeNode.Kind.DOUBLE ||
                tsb == TypeNode.Kind.ENTER   || tsb == TypeNode.Kind.LOGIC   ||
                tsb == TypeNode.Kind.CARACTER|| tsb == TypeNode.Kind.CADENA))
        {
            ErrorManager.add(new CompilerError(
                    a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "No es pot assignar a un element de tipus no escalar (com tuples o arrays)."
            ));
            a.setHasError(true);
        }

        Simbol s = currentScope.lookUp(ref.getId());
        if (s != null) {
            Descripcio d = s.getDescripcio();
            if (d instanceof DescripcioVar dvar) dvar.setInitialized(true);
            else if (d instanceof DescripcioArg darg) darg.setInitialized(true);
        }
    }


    public void gest_call(InstrNode.CallNode c) {
        if (c == null) return;

        String nom = c.getFunctionName();
        Simbol s = currentScope.lookUp(nom);

        if (s == null) {
            ErrorManager.add(new CompilerError(
                    c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "Crida a subprograma no declarat: " + nom
            ));
            c.setRetornTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            c.setHasError(true);
            return;
        }

        Descripcio desc = s.getDescripcio();
        if (!(desc instanceof DescripcioProc dproc)) {
            ErrorManager.add(new CompilerError(
                    c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "'" + nom + "' no és una funció ni un procediment."
            ));
            c.setRetornTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            c.setHasError(true);
            return;
        }

        List<ArgNode> params = dproc.getArgs();
        List<ExprNode> args = c.getExpr();
        if (params == null) params = List.of();
        if (args == null) args = List.of();

        if (params.size() != args.size()) {
            ErrorManager.add(new CompilerError(
                    c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "Nombre d'arguments incorrecte a la crida de " + nom +
                            ": s'esperaven " + params.size() + ", s'han passat " + args.size()
            ));
            c.setRetornTipus(
                    dproc.getType() != null ? dproc.getType() : cercaTipus(TypeNode.Kind.UNKNOWN)
            );
            c.setHasError(true);
            return;
        }

        for (int i = 0; i < params.size(); i++) {
            ArgNode param = params.get(i);
            ExprNode arg = args.get(i);

            gest_expr(arg);
            if (arg.hasError()) c.setHasError(true);

            DescripcioTipus tipusFormal = cercaTipus(param.getType().getKind());
            if (tipusFormal == null) {
                tipusFormal = cercaTipus(TypeNode.Kind.UNKNOWN);
                c.setHasError(true);
            }

            DescripcioTipus tipusReal = arg.getDescripcioTipus();

            if (!TipusUtils.sonCompatibles(tipusFormal, tipusReal)) {
                ErrorManager.add(new CompilerError(
                        arg.line, arg.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus incorrecte a l’argument " + (i + 1) + " de la crida a " + nom +
                                ": s'esperava " + tipusFormal.getNomTipus() +
                                " i s'ha rebut " + tipusReal.getNomTipus()
                ));
                c.setHasError(true);
            }
        }

        DescripcioTipus tipusRetorn = dproc.getType();
        if (tipusRetorn == null) tipusRetorn = cercaTipus(TypeNode.Kind.UNKNOWN);
        c.setRetornTipus(tipusRetorn);
    }


    public void gest_input(InstrNode.InputNode in) {
        if (in == null) return;

        RefNode ref = in.getRef();
        gest_ref(ref);

        if (ref.hasError()) {
            in.setHasError(true);
            return;
        }

        if (ref.getModeRef() != RefNode.ModeRef.VAR) {
            ErrorManager.add(new CompilerError(
                    in.line, in.column, CompilerError.TYPE.SEMANTIC,
                    "Només es poden fer operacions d'input sobre variables assignables: " + ref.getId()
            ));
            in.setHasError(true);
            return;
        }

        Simbol s = currentScope.lookUp(ref.getId());
        if (s != null) {
            Descripcio d = s.getDescripcio();
            if (d instanceof DescripcioVar dvar) dvar.setInitialized(true);
            else if (d instanceof DescripcioArg darg) darg.setInitialized(true);
        }
    }


    public void gest_output(InstrNode.OutputNode o) {
        if (o == null) return;

        gest_expr(o.getExpr());

        if (o.getExpr().hasError()) {
            o.setHasError(true);
        }
    }


    public void gest_cond(CondNode c) {
        if (c == null) return;

        if (c instanceof CondNode.IfNode ifNode) {
            gest_expr(ifNode.getCondition());
            DescripcioTipus tipusCond = ifNode.getCondition().getDescripcioTipus();

            if (notBoolean(tipusCond)) {
                ErrorManager.add(new CompilerError(
                        ifNode.line, ifNode.column, CompilerError.TYPE.SEMANTIC,
                        "La condició del 'if' ha de ser de tipus booleà."
                ));
                ifNode.setHasError(true);
            }

            openScope();
            gest_instrs(ifNode.getThenInstrs());
            closeScope();

            if (ifNode.getElseInstrs() != null) {
                openScope();
                gest_instrs(ifNode.getElseInstrs());
                closeScope();
            }
        }

        else if (c instanceof CondNode.SwitchNode switchNode) {
            ExprNode exprSwitch = switchNode.getExpr();
            gest_expr(exprSwitch);
            DescripcioTipus tipusSwitch = exprSwitch.getDescripcioTipus();

            TypeNode.Kind tsb = tipusSwitch.getTipusBase();
            if (!(tsb == TypeNode.Kind.INTEGER || tsb == TypeNode.Kind.CARACTER ||
                    tsb == TypeNode.Kind.LOGIC   || tsb == TypeNode.Kind.CADENA   ||
                    tsb == TypeNode.Kind.ENTER))
            {
                ErrorManager.add(new CompilerError(
                        switchNode.line, switchNode.column, CompilerError.TYPE.SEMANTIC,
                        "L'expressió del 'switch' ha de ser d'un tipus escalar (enter, caracter, lògic o cadena)."
                ));
                switchNode.setHasError(true);
            }

            List<CondNode.SwitchNode.CaseNode> cases = switchNode.getCases();
            if (cases != null) {
                for (CondNode.SwitchNode.CaseNode caseNode : cases) {
                    ExprNode caseValue = caseNode.getValue();
                    gest_expr(caseValue);
                    DescripcioTipus tipusCase = caseValue.getDescripcioTipus();

                    // Comprovar compatibilitat entre el tipus principal i el valor del case
                    if (!TipusUtils.sonCompatibles(tipusSwitch, tipusCase)) {
                        ErrorManager.add(new CompilerError(
                                caseNode.line, caseNode.column, CompilerError.TYPE.SEMANTIC,
                                "Tipus incompatible al 'case': s'esperava " + tipusSwitch.getNomTipus() +
                                        " però s'ha trobat " + tipusCase.getNomTipus()
                        ));
                        caseNode.setHasError(true);
                    }

                    openScope();
                    gest_instrs(caseNode.getInstrs());
                    closeScope();
                }
            }

            if (switchNode.getDefaultInstrs() != null) {
                openScope();
                gest_instrs(switchNode.getDefaultInstrs());
                closeScope();
            }
        }
    }


    public void gest_loop(LoopNode l) {
        if (l == null) return;

        if (l instanceof LoopNode.WhileNode w) {
            gest_expr(w.getCondition());

            DescripcioTipus tipusCond = w.getCondition().getDescripcioTipus();
            if (notBoolean(tipusCond)) {
                ErrorManager.add(new CompilerError(
                        w.line, w.column, CompilerError.TYPE.SEMANTIC,
                        "La condició del 'while' ha de ser de tipus booleà."
                ));
                w.setHasError(true);
            }

            openScope();
            gest_instrs(w.getBody());
            closeScope();
        }

        else if (l instanceof LoopNode.DoWhileNode dw) {
            openScope();
            gest_instrs(dw.getBody());
            closeScope();

            gest_expr(dw.getCondition());
            DescripcioTipus tipusCond = dw.getCondition().getDescripcioTipus();

            if (notBoolean(tipusCond)) {
                ErrorManager.add(new CompilerError(
                        dw.line, dw.column, CompilerError.TYPE.SEMANTIC,
                        "La condició del 'do-while' ha de ser de tipus booleà."
                ));
                dw.setHasError(true);
            }
        }
    }

    public void gest_ref(RefNode r) {
        if (r == null) return;

        // R0 -> R1 . id
        if(r instanceof RefNode.CampAccessNode ca) {
            gest_ref_camp(ca);
            return;
        }

        // R0 -> id
        String id = r.getId();
        Simbol s = currentScope.lookUp(id);

        if (s == null) {
            ErrorManager.add(new CompilerError(
                    r.line, r.column, CompilerError.TYPE.SEMANTIC,
                    "Identificador no declarat: " + id
            ));
            r.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            r.setHasError(true);
            return;
        }

        Descripcio desc = s.getDescripcio();
        r.setDesc(desc);

        DescripcioTipus tipus;
        RefNode.ModeRef mode;

        switch (desc) {
            case DescripcioVar dVar -> {
                tipus = dVar.getType();
                mode = RefNode.ModeRef.VAR;

                if (!dVar.getInitialized() && !isTuple(tipus)) {
                    ErrorManager.add(new CompilerError(
                            r.line, r.column, CompilerError.TYPE.SEMANTIC,
                            "La variable '" + id + "' s'utilitza abans d'haver estat inicialitzada."
                    ));
                    r.setHasError(true);
                }
            }
            case DescripcioArg dArg -> {
                tipus = dArg.getType();
                mode = RefNode.ModeRef.VAR;
            }
            case DescripcioConst dConst -> {
                tipus = dConst.getType();
                mode = RefNode.ModeRef.CONST;
            }
            case DescripcioProc dProc -> {
                tipus = dProc.getType();
                mode = RefNode.ModeRef.PROCF;
            }
            case null, default -> {
                ErrorManager.add(new CompilerError(
                        r.line, r.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus d'identificador no vàlid per a referència: " + id
                ));
                tipus = cercaTipus(TypeNode.Kind.UNKNOWN);
                mode = RefNode.ModeRef.UNKNOWN;
            }
        }

        r.setDescripcioTipus(tipus);
        r.setModeRef(mode);

        if (tipus.getTipusBase() == TypeNode.Kind.UNKNOWN) r.setHasError(true);
    }

    // R0 -> R1 . id
    // r -> base . campNom
    private void gest_ref_camp(RefNode.CampAccessNode r) {
        RefNode base = r.getBase();
        gest_ref(base);

        if (base.hasError()) {
            r.setHasError(true);
            r.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            return;
        }

        if(base.getModeRef() != RefNode.ModeRef.VAR && base.getModeRef() != RefNode.ModeRef.CONST) {
            ErrorManager.add(new CompilerError(r.line, r.column, CompilerError.TYPE.SEMANTIC,
                    "No es pot accedir a un camp que no sigui o variable o constant"));
            r.setHasError(true);
            r.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
        }

        String campNom = r.getId();

        DescripcioTipus tipusBase = base.getDescripcioTipus();
        if (!isTuple(tipusBase)) {
            ErrorManager.add(new CompilerError(
                    r.line, r.column, CompilerError.TYPE.SEMANTIC,
                    "No es pot accedir al camp '" + campNom +
                            "' perquè la referència no és una tupla."
            ));
            r.setHasError(true);
            r.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            return;
        }

        // cercar el camp dins la tupla
        DescripcioTipus.CampRecord campRecord = null;
        for (DescripcioTipus.CampRecord c : tipusBase.getCamps()) {
            if (c.getNom().equals(campNom)) {
                campRecord = c;
                break;
            }
        }

        if (campRecord == null) {
            ErrorManager.add(new CompilerError(
                    r.line, r.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus de la tupla '" + tipusBase.getNomTipus() +
                            "' no té cap camp anomenat '" + campNom + "'."
            ));
            r.setHasError(true);
            r.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            return;
        }

        DescripcioTipus tipusCamp = campRecord.getTipus();

        r.setDescripcioTipus(tipusCamp);
        r.setModeRef(base.getModeRef());
    }


    // E -> E_LOG
    // E_LOG0 -> E_LOG1 OP_LOG E_REL | E_REL
    // E_REL0 -> E_REL1 OP_REL E_ARIT | E_ARIT
    // E_ARIT0 -> E_ARTI1 OP_ARIT TERM | TERM
    public void gest_expr(ExprNode e) {
        if (e == null) return;

        if (e instanceof ExprNode.BinaryOpNode bin) {
            gest_expr(bin.getLeft());
            gest_expr(bin.getRight());

            DescripcioTipus tLeft = bin.getLeft().getDescripcioTipus();
            DescripcioTipus tRight = bin.getRight().getDescripcioTipus();
            String op = bin.getOperator();

            if (bin.getLeft().hasError() || bin.getRight().hasError()) {
                e.setHasError(true);
                e.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
                e.setMode(ExprNode.ModeExpr.MODERESULT);
                return;
            }

            DescripcioTipus resultat = cercaTipus(TypeNode.Kind.UNKNOWN);

            // Operacions lògiques
            if (op.equals("i") || op.equals("o")) {
                if (notBoolean(tLeft) || notBoolean(tRight)) {
                    ErrorManager.add(new CompilerError(
                            bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                            "Operació lògica només permesa entre booleans."
                    ));
                    e.setHasError(true);
                } else resultat = cercaTipus(TypeNode.Kind.LOGIC);
            }

            // Operacions relacionals
            else if (List.of("==", "!=", "<", "<=", ">", ">=").contains(op)) {
                if (!TipusUtils.sonCompatibles(tLeft, tRight)) {
                    ErrorManager.add(new CompilerError(
                            bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                            "Comparació entre tipus incompatibles: " +
                                    tLeft.getNomTipus() + " i " + tRight.getNomTipus()
                    ));
                    e.setHasError(true);
                }
                resultat = cercaTipus(TypeNode.Kind.LOGIC);
            }

            // Operacions aritmètiques
            else if (List.of("+", "-", "*", "/", "mod").contains(op)) {
                if (op.equals("+") && isString(tLeft) && isString(tRight)) {
                    resultat = cercaTipus(TypeNode.Kind.CADENA);
                } else if (notNumeric(tLeft) || notNumeric(tRight)) {
                    ErrorManager.add(new CompilerError(
                            bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                            "Operació aritmètica només permesa entre valors numèrics."
                    ));
                    e.setHasError(true);
                } else {
                    resultat = cercaTipus(TypeNode.Kind.ENTER);
                }
            }

            // Operador desconegut
            else {
                ErrorManager.add(new CompilerError(
                        bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                        "Operador desconegut: " + op
                ));
                e.setHasError(true);
            }

            e.setDescripcioTipus(resultat);
            e.setMode(ExprNode.ModeExpr.MODERESULT);
            return;
        }

        // E -> TERM
        gest_term(e);

        if (e.hasError() && e.getDescripcioTipus() == null) {
            e.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
        }
    }

    // TERM -> VALOR_LIT | REF | CALL | NOT E | ( E )
    public void gest_term(ExprNode e) {
        if (e == null) return;

        if(e.hasError()) {
            e.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
            e.setMode(ExprNode.ModeExpr.MODERESULT);
            return;
        }

        switch (e) {
            // TERM -> VALOR_LIT
            case ExprNode.LiteralNode lit -> {
                TypeNode.Kind kind = (lit.getKind() != null) ? lit.getKind() : TypeNode.Kind.UNKNOWN;
                e.setDescripcioTipus(cercaTipus(kind));
                e.setMode(ExprNode.ModeExpr.MODECONST);
                return;
            }
            // TERM -> REF
            case RefNode ref -> {
                gest_ref(ref);
                e.setDescripcioTipus(ref.getDescripcioTipus() != null ? ref.getDescripcioTipus()
                        : cercaTipus(TypeNode.Kind.UNKNOWN));

                RefNode.ModeRef modeRef = (ref.getModeRef() != null) ? ref.getModeRef()
                        : RefNode.ModeRef.UNKNOWN;

                e.setMode(modeRef == RefNode.ModeRef.CONST ? ExprNode.ModeExpr.MODECONST
                        : ExprNode.ModeExpr.MODEVAR);

                if (ref.hasError()) e.setHasError(true);
                return;
            }
            // TERM -> CALL
            case ExprNode.ExprInstrNode callExpr -> {
                InstrNode.CallNode call = callExpr.getCall();
                gest_call(call);

                if(call.hasError()) {
                    e.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
                    e.setMode(ExprNode.ModeExpr.MODERESULT);
                    e.setHasError(true);
                    return;
                }

                DescripcioTipus tipusRetorn = call.getRetornTipus();
                if (tipusRetorn == null) tipusRetorn = cercaTipus(TypeNode.Kind.UNKNOWN);

                if (tipusRetorn.getTipusBase() == TypeNode.Kind.VOID) {
                    ErrorManager.add(new CompilerError(
                            e.line, e.column, CompilerError.TYPE.SEMANTIC,
                            "Un procediment no pot aparèixer dins una expressió."
                    ));
                    tipusRetorn = cercaTipus(TypeNode.Kind.UNKNOWN);
                    e.setHasError(true);
                }

                e.setDescripcioTipus(tipusRetorn);
                e.setMode(ExprNode.ModeExpr.MODERESULT);

                return;
            }
            // TERM -> - E
            case ExprNode.UnaryOpNode un -> {
                gest_expr(un.getExpr());

                if (un.getExpr().hasError()) {
                    e.setHasError(true);
                    e.setDescripcioTipus(cercaTipus(TypeNode.Kind.UNKNOWN));
                    e.setMode(ExprNode.ModeExpr.MODERESULT);
                    return;
                }

                DescripcioTipus tipusOp = un.getExpr().getDescripcioTipus();
                String op = (un.getOperator() != null) ? un.getOperator() : "?";
                DescripcioTipus resultat = cercaTipus(TypeNode.Kind.UNKNOWN);

                switch (op) {
                    case "NOT" -> {
                        if (notBoolean(tipusOp)) {
                            ErrorManager.add(new CompilerError(
                                    un.line, un.column, CompilerError.TYPE.SEMANTIC,
                                    "L'operador 'NOT' només pot aplicar-se sobre valors booleans."
                            ));
                            e.setHasError(true);
                        } else resultat = cercaTipus(TypeNode.Kind.LOGIC);
                    }
                    case "-" -> {
                        if (notNumeric(tipusOp)) {
                            ErrorManager.add(new CompilerError(
                                    un.line, un.column, CompilerError.TYPE.SEMANTIC,
                                    "L'operador unari '-' només pot aplicar-se sobre valors numèrics."
                            ));
                            e.setHasError(true);
                        } else resultat = tipusOp; // conserva el tipus del operand
                    }
                    default -> {
                        ErrorManager.add(new CompilerError(
                                un.line, un.column, CompilerError.TYPE.SEMANTIC,
                                "Operador unari desconegut: " + op
                        ));
                        e.setHasError(true);
                    }
                }

                e.setDescripcioTipus(resultat);
                e.setMode(ExprNode.ModeExpr.MODERESULT);
            }
            default -> {}
        }
    }




    private boolean notNumeric(DescripcioTipus t) {
        return t == null || (t.getTipusBase() != TypeNode.Kind.DOUBLE && t.getTipusBase() != TypeNode.Kind.ENTER);
    }

    private boolean notBoolean(DescripcioTipus t) {
        return t == null || t.getTipusBase() != TypeNode.Kind.LOGIC;
    }

    private boolean isTuple(DescripcioTipus t) {
        return t == null || t.getTipusBase() == TypeNode.Kind.TUPLA;
    }

    private boolean isString(DescripcioTipus t) {
        return t == null || t.getTipusBase() == TypeNode.Kind.CADENA;
    }

    private DescripcioTipus cercaTipus(TypeNode.Kind kind) {
        if (kind == null) return cercaTipus(TypeNode.Kind.UNKNOWN);

        Simbol s = currentScope.lookUp(kind.name().toLowerCase());

        if (s == null) {
            ErrorManager.add(new CompilerError(
                    0, 0, CompilerError.TYPE.SEMANTIC,
                    "Tipus inexistent: " + kind.name().toLowerCase()
            ));
            return cercaTipus(TypeNode.Kind.UNKNOWN);
        }

        Descripcio d = s.getDescripcio();
        if (!(d instanceof DescripcioTipus dt)) {
            ErrorManager.add(new CompilerError(
                    0, 0, CompilerError.TYPE.SEMANTIC,
                    "El símbol '" + kind.name().toLowerCase() + "' no és un tipus vàlid."
            ));
            return cercaTipus(TypeNode.Kind.UNKNOWN);
        }

        return dt;
    }

    private DescripcioTipus cercaTipus(TypeNode.Kind kind, String customTypeName) {
        if (kind == null) return cercaTipus(TypeNode.Kind.UNKNOWN);

        switch (kind) {
            case ENTER, CADENA, CARACTER, LOGIC, DOUBLE, INTEGER, ARRAY, TUPLA -> {
                return cercaTipus(kind);
            }
            case USER -> {
                Simbol s = currentScope.lookUp(customTypeName);
                if (s == null) {
                    ErrorManager.add(new CompilerError(0, 0, CompilerError.TYPE.SEMANTIC,
                            "Tipus inexistent: " + customTypeName));
                    return cercaTipus(TypeNode.Kind.UNKNOWN);
                }

                Descripcio d = s.getDescripcio();
                if (!(d instanceof DescripcioTipus dt)) {
                    ErrorManager.add(new CompilerError(0, 0, CompilerError.TYPE.SEMANTIC,
                            "'" + customTypeName + "' no és un tipus vàlid."));
                    return cercaTipus(TypeNode.Kind.UNKNOWN);
                }

                return dt;
            }
            default -> {
                return cercaTipus(TypeNode.Kind.UNKNOWN);
            }
        }
    }


}
