import simbols.*;
import nodes.*;
import nodes.expresions.*;
import nodes.instructions.*;
import nodes.instructions.conditionals.*;
import nodes.instructions.loops.*;
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
        gest_instrs(p.getInstrs());
        SymbolTable.registerClosedScope(currentScope);
    }

    // ─────────────────────────────────────────────
    // Declaracions
    // ─────────────────────────────────────────────
    public void gest_decls(List<DeclNode> decls) {
        if (decls == null) return;
        for (DeclNode decl : decls) {
            if (decl instanceof MethodNode m) gest_method(m);
            else gest_decl(decl);
        }
    }

    // DECL -> TIPUS ID ASSIG | CONST TIPUS ID := E
    public void gest_decl(DeclNode decl) {
        if (decl == null) return;

        // Gestió concreta si la declaració és de tipus TUPLA
        if (decl.getType().getKind() == Kind.TUPLA) {
            novaTupla(decl);
            return;
        }

        // Gestió del tipus de la variable a declarar
        TypeNode type = decl.getType();
        DescripcioTipus dt = gest_type(type);

        // Comprovar si el tipus té error
        if (type.hasError() || dt == null) {
            // Propagació d'errors
            decl.setHasError(true);

            // Gestionar l'expressió per observar tots els errors possibles
            gest_expr(decl.getExpr());
            return;
        }

        // Gestió concreta d'una declaració constant
        if (decl.isConst()) {
            gest_decl_const(decl, dt);
            return;
        }

        Kind tsb = dt.getTipusBase();

        // Analitzar l'expressió d'assignació
        ExprNode assig = decl.getExpr();
        gest_expr(assig);

        // Comprovar si el tipus de la variable a declarar és de tipus usuari (tupla declarada)
        if (decl.getType().getKind() == Kind.USER && assig != null) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "Les variables de tipus '" + decl.getType().getCustomTypeName() +
                            "' (tipus definits) no poden inicialitzar-se directament."
            ));
            decl.setHasError(true);
            return;
        }

        // Si hi ha assignació
        if (assig != null) {
            if (assig.hasError()) {
                // propagació d'errors
                decl.setHasError(true);
                return;
            }

            // Comprovar si són compatibles el tipus de la variable i de l'expressió
            Kind a_tsb = assig.getKind();
            if (!TipusUtils.sonCompatibles(a_tsb, tsb)) {
                ErrorManager.add(new CompilerError(
                        assig.line, assig.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus incompatible: s'espera " + tsb +
                                " però s'ha trobat " + a_tsb));
                decl.setHasError(true);
            }
        }

        // Crear una nova descripció de variable amb el tipus corresponent
        DescripcioVar dv = new DescripcioVar(dt);

        // Si té una expressió assignada marcar com a inicialitzada
        if (assig != null) dv.setInitialized(true);

        // Afegir la variable a la taula de símbols
        if (!currentScope.add(new Simbol(decl.getId(), dv, currentScope.getScopeId()))) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "La variable '" + decl.getId() + "' ja ha estat declarada."
            ));
            decl.setHasError(true);
        }
    }

    // TIPUS -> ENTER | CADENA | CARÀCTER | LOGIC | TUPLA | ID
    private DescripcioTipus gest_type(TypeNode t) {
        // Cercar el nom del tipus a la taula de símbols
        Simbol s = currentScope.lookUp(t.getLookupName());

        // Comprovar si el tipus existeix
        if (s == null) {
            ErrorManager.add(new CompilerError(
                    t.line, t.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus '" + t.getLookupName() + "' no està declarat."
            ));
            t.setHasError(true);
            return null;
        }

        // Comprovar que el tipus és una descripció de tipus
        if(!(s.getDescripcio() instanceof DescripcioTipus dt)) {
            ErrorManager.add(new CompilerError(
                    t.line, t.column, CompilerError.TYPE.SEMANTIC,
                    "El símbol '" + t.getLookupName() + "' no és un tipus vàlid."
            ));
            t.setHasError(true);
            return null;
        }

        return dt;
    }

    // DECL -> TUPLA ( ARGS [CAMPS] ) ID
    private void novaTupla(DeclNode decl) {
        TypeNode tipusNode = decl.getType();
        String nomTupla = decl.getId();

        // Comprovar que no hi ha assignació a la tupla (tupla {camps} id)
        if (decl.getExpr() != null) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "Les definicions de tipus 'tupla' no poden tenir una assignació."
            ));
            decl.setHasError(true);
            return;
        }

        // Crear la descripció base de tupla
        DescripcioTipus descTupla = new DescripcioTipus(nomTupla, Kind.TUPLA, 0);

        // Construir els camps
        List<DescripcioCamp> camps = new ArrayList<>();
        Set<String> nomsCamps = new HashSet<>();
        int offset = 0;

        // Recórrer els camps declarats al tipus
        for (ArgNode camp : tipusNode.getFields()) {
            // evitar camps duplicats
            if (!nomsCamps.add(camp.getName())) {
                ErrorManager.add(new CompilerError(
                        camp.line, camp.column, CompilerError.TYPE.SEMANTIC,
                        "Camp duplicat '" + camp.getName() + "' dins la tupla '" + nomTupla + "'."
                ));
                decl.setHasError(true);
                return;
            }

            // Emmagatzemar el tipus del camp
            DescripcioTipus dt = gest_type(camp.getType());

            // Comprovar que el tipus no tengui error
            if(camp.getType().hasError() || dt == null) {
                decl.setHasError(true);
                return;
            }

            // Afegir el camp a la descripció de camp corresponent, juntament amb el desplaçament
            camps.add(new DescripcioCamp(camp.getName(), dt, offset));
            offset += dt.getOcupacio();
        }

        if (decl.hasError()) return;

        // Afegir els camps a la descripció de la tupla, juntament amb el desplaçament
        descTupla.setCamps(camps);
        descTupla.setOcupacio(offset > 0 ? offset : 1);

        // Afegir la tupla a la taula de símbols
        if (!currentScope.add(new Simbol(nomTupla, descTupla, currentScope.getScopeId()))) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "La tupla '" + decl.getId() + "' ja ha estat declarada."
            ));
            decl.setHasError(true);
        }
    }

    // DECL -> CONST TIPUS ID := E
    public void gest_decl_const(DeclNode decl, DescripcioTipus dt) {
        if (decl == null) return;

        Kind tsb = dt.getTipusBase();

        // Comprovar que el tipus és l'adequat
        if (tsb != Kind.ENTER && tsb != Kind.CADENA && tsb != Kind.CARACTER && tsb != Kind.LOGIC) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus '" + tsb + "' no pot emprar-se per declarar una constant."
            ));
            decl.setHasError(true);
            gest_expr(decl.getExpr());
            return;
        }

        // Analitzar l'expressió associada a la constant
        ExprNode assig = decl.getExpr();

        if(assig == null) {
            ErrorManager.add(new CompilerError(decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "S'ha d'assignar valor a la constant: " + decl.getId()));
            decl.hasError();
            return;
        }

        gest_expr(assig);
        Kind a_tsb = assig.getKind();

        // Comprovar compatibilitat de tipus (valor i id)
        if (!TipusUtils.sonCompatibles(tsb, a_tsb)) {
            ErrorManager.add(new CompilerError(
                    decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                    "El tipus de l'expressió no és compatible amb el tipus de la constant '" + decl.getId() + "'."
            ));
            decl.setHasError(true);
        }


        Object valor = null;

        if (assig instanceof LiteralNode lit) {
            valor = lit.getValue();

            int v = (Integer) valor;
            if (v < dt.getLimitInf() || v > dt.getLimitSup()) {
                ErrorManager.add(new CompilerError(
                        decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                        "El valor " + v + " està fora del rang permès per al tipus '" + dt.getNomTipus() + "'."
                ));
                decl.setHasError(true);
            }
        } else if (assig instanceof RefNode r) {
            Simbol sr = currentScope.lookUp(r.getId());

            if(!(sr.getDescripcio() instanceof DescripcioConst)) {
                ErrorManager.add(new CompilerError(decl.line, decl.column, CompilerError.TYPE.SEMANTIC,
                        " "));
                decl.setHasError(true);
            }

            DescripcioConst dc = (DescripcioConst) sr.getDescripcio();
            valor = dc.getValor();
        }

        if(decl.hasError()) return;

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


    public void gest_method(MethodNode m) {
        if (m == null) return;

        gest_decls(m.getDecls());

        DescripcioTipus tipusRetorn = gest_type(m.getType());

        if (m.getType().hasError() || tipusRetorn == null) {
            m.setHasError(true);
        }

        DescripcioProc descProc = new DescripcioProc(
                tipusRetorn,
                m.isFunction(),
                m.getParams()
        );

        Simbol sProc = new Simbol(m.getId(), descProc, currentScope.getScopeId());
        if (!currentScope.add(sProc)) {
            ErrorManager.add(new CompilerError(
                    m.line, m.column, CompilerError.TYPE.SEMANTIC,
                    "Nom de subprograma ja utilitzat: " + m.getId()
            ));
            m.setHasError(true);
        }

        openScope();

        if (m.getParams() != null) {
            for (ArgNode arg : m.getParams()) {
                String name = arg.getName();
                DescripcioTipus tipusArg = gest_type(arg.getType());

                if (arg.getType().hasError() || tipusArg == null) {
                    m.setHasError(true);
                    return;
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

        if (m.isFunction() && tipusRetorn != null) {
            ExprNode retExpr = m.getExpr();

            if (retExpr == null) {
                ErrorManager.add(new CompilerError(
                        m.line, m.column, CompilerError.TYPE.SEMANTIC,
                        "Falta l'expressió de retorn a la funció " + m.getId()
                ));
                m.setHasError(true);
            } else {
                gest_expr(retExpr);
                Kind tsb = tipusRetorn.getTipusBase();
                Kind e_tsb = retExpr.getKind();
                if (retExpr.hasError()) {
                    m.setHasError(true);
                } else if (!TipusUtils.sonCompatibles(tsb, e_tsb)) {
                    ErrorManager.add(new CompilerError(
                            retExpr.line, retExpr.column, CompilerError.TYPE.SEMANTIC,
                            "Tipus de retorn incompatible: s'espera " + tsb + " però s'ha trobat " + e_tsb
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
        if (i instanceof AssignNode assign) {
            gest_assign(assign);
        } else if (i instanceof CallNode call) {
            gest_call(call);
        } else if (i instanceof InputNode input) {
            gest_input(input);
        } else if (i instanceof OutputNode output) {
            gest_output(output);
        } else if (i instanceof CondNode cond) {
            gest_cond(cond);
        } else if (i instanceof LoopNode loop) {
            gest_loop(loop);
        } else if (i instanceof InstrNode.InstrDeclNode decl) {
            gest_decl(decl.getDecl());
        }
    }


    public void gest_assign(AssignNode a) {
        if (a == null) return;

        RefNode ref = a.getRef();
        ExprNode expr = a.getExpr();

        gest_ref(ref);

        if (ref.hasError()) {
            a.setHasError(true);
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

        if (tipusRef.getTipusBase() == Kind.UNKNOWN || tipusExpr.getTipusBase() == Kind.UNKNOWN) {
            a.setHasError(true);
            return;
        }

        Kind r_tsb = ref.getKind();
        Kind e_tsb = expr.getKind();

        if (!a.hasError() && !TipusUtils.sonCompatibles(r_tsb, e_tsb)) {
            ErrorManager.add(new CompilerError(
                    a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "Tipus incompatible: s'intenta assignar una expressió de tipus " +
                            e_tsb + " a '" + ref.getId() + "' de tipus " + r_tsb
            ));
            a.setHasError(true);
        }

        if (r_tsb == Kind.TUPLA || r_tsb == Kind.USER || e_tsb == Kind.TUPLA || e_tsb == Kind.USER) {
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


    public void gest_call(CallNode c) {
        if (c == null) return;

        String nom = c.getFunctionName();
        Simbol s = currentScope.lookUp(nom);

        if (s == null) {
            ErrorManager.add(new CompilerError(
                    c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "Crida a subprograma no declarat: " + nom
            ));
            c.setRetornTipus(cercaTipus(Kind.UNKNOWN));
            c.setHasError(true);
            return;
        }

        Descripcio desc = s.getDescripcio();
        if (!(desc instanceof DescripcioProc dproc)) {
            ErrorManager.add(new CompilerError(
                    c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "'" + nom + "' no és una funció ni un procediment."
            ));
            c.setRetornTipus(cercaTipus(Kind.UNKNOWN));
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
            c.setRetornTipus(dproc.getType() != null ? dproc.getType() : cercaTipus(Kind.UNKNOWN));
            c.setHasError(true);
            return;
        }

        for (int i = 0; i < params.size(); i++) {
            ArgNode param = params.get(i);
            ExprNode arg = args.get(i);

            gest_expr(arg);
            if (arg.hasError()) {
                c.setHasError(true);
                return;
            }

            Kind p_tsb = param.getType().getKind();
            Kind arg_tsb = arg.getKind();

            if (!TipusUtils.sonCompatibles(p_tsb, arg_tsb)) {
                ErrorManager.add(new CompilerError(
                        arg.line, arg.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus incorrecte a l’argument " + (i + 1) + " de la crida a " + nom +
                                ": s'esperava " + p_tsb + " i s'ha rebut " + arg_tsb
                ));
                c.setHasError(true);
            }
        }

        DescripcioTipus tipusRetorn = dproc.getType();
        if (tipusRetorn == null) tipusRetorn = cercaTipus(Kind.UNKNOWN);
        c.setRetornTipus(tipusRetorn);
    }


    public void gest_input(InputNode in) {
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


    public void gest_output(OutputNode o) {
        if (o == null) return;

        gest_expr(o.getExpr());

        if (o.getExpr().hasError()) {
            o.setHasError(true);
        }
    }


    public void gest_cond(CondNode c) {
        switch (c) {
            case null -> {}
            case IfNode ifNode -> gest_cond_if(ifNode);
            case SwitchNode switchNode -> gest_cond_switch(switchNode);
            default -> {
            }
        }
    }

    private void gest_cond_if(IfNode ifNode) {
        gest_expr(ifNode.getCondition());
        Kind tsb = ifNode.getCondition().getKind();

        if (notBoolean(tsb)) {
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

    private void gest_cond_switch(SwitchNode switchNode) {
        ExprNode exprSwitch = switchNode.getExpr();
        gest_expr(exprSwitch);

        Kind tsb = exprSwitch.getKind();

        if (!(tsb == Kind.INTEGER || tsb == Kind.CARACTER || tsb == Kind.LOGIC   || tsb == Kind.CADENA   ||
                tsb == Kind.ENTER))
        {
            ErrorManager.add(new CompilerError(
                    switchNode.line, switchNode.column, CompilerError.TYPE.SEMANTIC,
                    "L'expressió del 'switch' ha de ser d'un tipus escalar (enter, caracter, lògic o cadena)."
            ));
            switchNode.setHasError(true);
        }

        List<CaseNode> cases = switchNode.getCases();
        if (cases != null) {
            for (CaseNode caseNode : cases) {
                ExprNode caseValue = caseNode.getValue();
                gest_expr(caseValue);
                Kind c_tsb = caseValue.getKind();

                // Comprovar compatibilitat entre el tipus principal i el valor del case
                if (!TipusUtils.sonCompatibles(c_tsb, tsb)) {
                    ErrorManager.add(new CompilerError(
                            caseNode.line, caseNode.column, CompilerError.TYPE.SEMANTIC,
                            "Tipus incompatible al 'case': s'esperava " + tsb +
                                    " però s'ha trobat " + c_tsb
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


    public void gest_loop(LoopNode l) {
        switch (l) {
            case null -> {}
            case WhileNode w -> gest_loop_while(w);
            case DoWhileNode dw -> gest_loop_dowhile(dw);
            default -> {
            }
        }
    }


    private void gest_loop_while(WhileNode w){
        gest_expr(w.getCondition());

        Kind tsb = w.getCondition().getKind();

        if (tsb != Kind.LOGIC) {
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

    private void gest_loop_dowhile(DoWhileNode dw){
        openScope();
        gest_instrs(dw.getBody());
        closeScope();

        gest_expr(dw.getCondition());
        Kind tsb = dw.getCondition().getKind();

        if (notBoolean(tsb)) {
            ErrorManager.add(new CompilerError(
                    dw.line, dw.column, CompilerError.TYPE.SEMANTIC,
                    "La condició del 'do-while' ha de ser de tipus booleà."
            ));
            dw.setHasError(true);
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
            r.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
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
                ErrorManager.add(new CompilerError(
                        r.line, r.column, CompilerError.TYPE.SEMANTIC,
                        "No es pot fer referència a un mètode sense paràmetres: " + id
                ));
                tipus = cercaTipus(Kind.UNKNOWN);
                mode = RefNode.ModeRef.UNKNOWN;
            }
            case null, default -> {
                ErrorManager.add(new CompilerError(
                        r.line, r.column, CompilerError.TYPE.SEMANTIC,
                        "Tipus d'identificador no vàlid per a referència: " + id
                ));
                tipus = cercaTipus(Kind.UNKNOWN);
                mode = RefNode.ModeRef.UNKNOWN;
            }
        }

        r.setKind(tipus.getTipusBase());
        r.setDescripcioTipus(tipus);
        r.setModeRef(mode);

        if (tipus.getTipusBase() == Kind.UNKNOWN) r.setHasError(true);
    }

    // R0 -> R1 . id
    // r -> base . campNom
    private void gest_ref_camp(RefNode.CampAccessNode r) {
        RefNode base = r.getBase();
        gest_ref(base);

        if (base.hasError()) {
            r.setHasError(true);
            r.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            return;
        }

        if(base.getModeRef() != RefNode.ModeRef.VAR && base.getModeRef() != RefNode.ModeRef.CONST) {
            ErrorManager.add(new CompilerError(r.line, r.column, CompilerError.TYPE.SEMANTIC,
                    "No es pot accedir a un camp que no sigui o variable o constant"));
            r.setHasError(true);
            r.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
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
            r.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            return;
        }

        // cercar el camp dins la tupla
        DescripcioCamp campRecord = null;
        for (DescripcioCamp c : tipusBase.getCamps()) {
            if (c.getName().equals(campNom)) {
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
            r.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            r.setModeRef(RefNode.ModeRef.UNKNOWN);
            return;
        }

        DescripcioTipus tipusCamp = campRecord.getType();

        r.setKind(tipusCamp.getTipusBase());
        r.setDescripcioTipus(tipusCamp);
        r.setModeRef(base.getModeRef());
    }


    // E -> E_LOG
    // E_LOG0 -> E_LOG1 OP_LOG E_REL | E_REL
    // E_REL0 -> E_REL1 OP_REL E_ARIT | E_ARIT
    // E_ARIT0 -> E_ARTI1 OP_ARIT TERM | TERM
    public void gest_expr(ExprNode e) {
        if (e == null) return;

        if (e instanceof BinaryOpNode bin) {
            gest_expr(bin.getLeft());
            gest_expr(bin.getRight());

            DescripcioTipus tLeft = bin.getLeft().getDescripcioTipus();
            DescripcioTipus tRight = bin.getRight().getDescripcioTipus();
            Kind tsb_l = bin.getLeft().getKind();
            Kind tsb_r = bin.getRight().getKind();

            String op = bin.getOperator();

            if (bin.getLeft().hasError() || bin.getRight().hasError()) {
                e.setHasError(true);
                e.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
                e.setMode(ExprNode.ModeExpr.MODERESULT);
                return;
            }

            DescripcioTipus resultat = cercaTipus(Kind.UNKNOWN);

            // Operacions lògiques
            if (op.equals("i") || op.equals("o")) {
                if (notBoolean(tsb_l) || notBoolean(tsb_r)) {
                    ErrorManager.add(new CompilerError(
                            bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                            "Operació lògica només permesa entre booleans."
                    ));
                    e.setHasError(true);
                } else resultat = tLeft;
            }

            // Operacions relacionals
            else if (List.of("==", "!=", "<", "<=", ">", ">=").contains(op)) {
                if (!TipusUtils.sonCompatibles(tLeft.getTipusBase(), tRight.getTipusBase())) {
                    ErrorManager.add(new CompilerError(
                            bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                            "Comparació entre tipus incompatibles: " +
                                    tLeft.getTipusBase() + " i " + tRight.getTipusBase()
                    ));
                    e.setHasError(true);
                }
                resultat = cercaTipus(Kind.LOGIC);
            }

            // Operacions aritmètiques
            else if (List.of("+", "-", "*", "/", "mod").contains(op)) {
                if (op.equals("+") && isString(tLeft) && isString(tRight)) {
                    resultat = cercaTipus(Kind.CADENA);
                } else if (notNumeric(tsb_l) || notNumeric(tsb_r)) {
                    ErrorManager.add(new CompilerError(
                            bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                            "Operació aritmètica només permesa entre valors numèrics."
                    ));
                    e.setHasError(true);
                } else {
                    resultat = tLeft;
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

            e.setKind(resultat.getTipusBase());
            e.setDescripcioTipus(resultat);
            e.setMode(ExprNode.ModeExpr.MODERESULT);
            return;
        }

        // E -> TERM
        gest_term(e);

        if (e.hasError() && e.getDescripcioTipus() == null) {
            e.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
        }
    }

    // TERM -> VALOR_LIT | REF | CALL | NOT E | ( E ) | - E
    public void gest_term(ExprNode e) {
        if (e == null) return;

        if(e.hasError()) {
            e.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            e.setMode(ExprNode.ModeExpr.MODERESULT);
            return;
        }

        switch (e) {
            // TERM -> VALOR_LIT
            case LiteralNode lit -> {
                Kind kind = (lit.getKind() != null) ? lit.getKind() : Kind.UNKNOWN;
                e.setKind(kind);
                e.setDescripcioTipus(cercaTipus(kind));
                e.setMode(ExprNode.ModeExpr.MODECONST);
            }
            // TERM -> REF
            case RefNode ref -> {
                gest_expr_ref(ref, e);
            }
            // TERM -> CALL
            case ExprNode.ExprInstrNode callExpr -> {
                gest_expr_instr(callExpr, e);
            }
            // TERM -> NOT E | - E
            case UnaryOpNode un -> {
                gest_expr_unary(un, e);
            }
            default -> {}
        }
    }

    private void gest_expr_ref(RefNode ref, ExprNode e) {
        gest_ref(ref);

        if (ref.getDescripcioTipus() == null) {
            e.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            e.setHasError(true);
        } else e.setDescripcioTipus(ref.getDescripcioTipus());

        if (ref.getDesc() instanceof DescripcioVar dVar && !dVar.getInitialized()) {
            if (!isTuple(ref.getDescripcioTipus())) {
                ErrorManager.add(new CompilerError(
                        ref.line, ref.column, CompilerError.TYPE.SEMANTIC,
                        "La variable '" + ref.getId() + "' s'utilitza abans d'haver estat inicialitzada."
                ));
                e.setHasError(true);
            }
        }

        RefNode.ModeRef modeRef = (ref.getModeRef() != null) ? ref.getModeRef()
                : RefNode.ModeRef.UNKNOWN;

        e.setMode(modeRef == RefNode.ModeRef.CONST ? ExprNode.ModeExpr.MODECONST
                : ExprNode.ModeExpr.MODEVAR);

        if (ref.hasError()) e.setHasError(true);
        e.setKind(ref.getKind());
    }

    private void gest_expr_instr(ExprNode.ExprInstrNode callExpr, ExprNode e) {
        CallNode call = callExpr.getCall();
        gest_call(call);

        if(call.hasError()) {
            e.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            e.setMode(ExprNode.ModeExpr.MODERESULT);
            e.setHasError(true);
            return;
        }

        DescripcioTipus tipusRetorn = call.getRetornTipus();
        if (tipusRetorn == null) tipusRetorn = cercaTipus(Kind.UNKNOWN);

        if (tipusRetorn.getTipusBase() == Kind.VOID) {
            ErrorManager.add(new CompilerError(
                    e.line, e.column, CompilerError.TYPE.SEMANTIC,
                    "Un procediment no pot aparèixer dins una expressió."
            ));
            tipusRetorn = cercaTipus(Kind.UNKNOWN);
            e.setHasError(true);
        }

        e.setKind(tipusRetorn.getTipusBase());
        e.setDescripcioTipus(tipusRetorn);
        e.setMode(ExprNode.ModeExpr.MODERESULT);
    }

    private void gest_expr_unary(UnaryOpNode un, ExprNode e) {
        gest_expr(un.getExpr());

        if (un.getExpr().hasError()) {
            e.setHasError(true);
            e.setDescripcioTipus(cercaTipus(Kind.UNKNOWN));
            e.setMode(ExprNode.ModeExpr.MODERESULT);
            return;
        }

        DescripcioTipus tipusOp = un.getExpr().getDescripcioTipus();
        Kind tsb = un.getKind();
        String op = (un.getOperator() != null) ? un.getOperator() : "?";
        DescripcioTipus resultat = cercaTipus(Kind.UNKNOWN);

        switch (op) {
            case "NOT" -> {
                if (notBoolean(tsb)) {
                    ErrorManager.add(new CompilerError(
                            un.line, un.column, CompilerError.TYPE.SEMANTIC,
                            "L'operador 'NOT' només pot aplicar-se sobre valors booleans."
                    ));
                    e.setHasError(true);
                } else resultat = tipusOp;
            }
            case "-" -> {
                if (notNumeric(tsb)) {
                    ErrorManager.add(new CompilerError(
                            un.line, un.column, CompilerError.TYPE.SEMANTIC,
                            "L'operador unari '-' només pot aplicar-se sobre valors numèrics."
                    ));
                    e.setHasError(true);
                } else resultat = tipusOp;
            }
            default -> {
                ErrorManager.add(new CompilerError(
                        un.line, un.column, CompilerError.TYPE.SEMANTIC,
                        "Operador unari desconegut: " + op
                ));
                e.setHasError(true);
            }
        }

        e.setKind(tsb);
        e.setDescripcioTipus(resultat);
        e.setMode(ExprNode.ModeExpr.MODERESULT);
    }




    private boolean notNumeric(Kind k) {
        return (k != Kind.DOUBLE && k != Kind.ENTER);
    }

    private boolean notBoolean(Kind k) {
        return k != Kind.LOGIC;
    }

    private boolean isTuple(DescripcioTipus t) {
        return t == null || t.getTipusBase() == Kind.TUPLA;
    }

    private boolean isString(DescripcioTipus t) {
        return t == null || t.getTipusBase() == Kind.CADENA;
    }

    private DescripcioTipus cercaTipus(Kind kind) {
        if (kind == null) return cercaTipus(Kind.UNKNOWN);

        Simbol s = currentScope.lookUp(kind.name().toLowerCase());

        if (s == null) {
            ErrorManager.add(new CompilerError(
                    0, 0, CompilerError.TYPE.SEMANTIC,
                    "Tipus inexistent: " + kind.name().toLowerCase()
            ));
            return cercaTipus(Kind.UNKNOWN);
        }

        Descripcio d = s.getDescripcio();
        if (!(d instanceof DescripcioTipus dt)) {
            ErrorManager.add(new CompilerError(
                    0, 0, CompilerError.TYPE.SEMANTIC,
                    "El símbol '" + kind.name().toLowerCase() + "' no és un tipus vàlid."
            ));
            return cercaTipus(Kind.UNKNOWN);
        }

        return dt;
    }
}
