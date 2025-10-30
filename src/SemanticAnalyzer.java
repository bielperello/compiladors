import symbols.*;
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
        TypeNode type = decl.getType();
        ExprNode assig = decl.getExpr();
        Simbol.STIPUS st = decl.isConst() ? Simbol.STIPUS.CONSTANT : Simbol.STIPUS.VARIABLE;

        for (ExprNode.VarNode var : decl.getIds()) {
            Simbol s = new Simbol(var.getName(), type, st, var.line, var.column, currentScope.getScopeId());

            if (isTuple(type.getKind())) {
                if (assig == null) {
                    ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC,
                            "Les tuples s'han d'inicialitzar sempre en la declaració."));
                    continue;
                }
                if (assig instanceof ExprNode.TupleNode tupleNode) {
                    s.setTupleCamps(tupleNode.getElements());
                }
            }

            if (assig != null) {
                gest_expr(assig);

                TypeNode.Kind assignedType = assig.getKind();

                if (assignedType == null || assignedType == TypeNode.Kind.UNKNOWN) {
                    ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC,
                            "Expressió d'assignació desconeguda per a '" + var.getName() + "'"));
                } else if (!type.getKind().equals(assignedType)) {
                    ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC,
                            "Tipus incompatible: es declara '" + var.getName() + "' com a " + type.getKind() +
                                    " però s'assigna una expressió de tipus " + assignedType));
                }

                s.setInitialized(true);
            }


            // Afegir a la taula de símbols, comprovant si ja existeix
            if (!currentScope.add(s)) {
                String errorMsg = (s.isConst() ? "La constant '" : "La variable '") + var.getName() + "' ja ha estat declarada.";
                ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC, errorMsg));
            }
        }
    }

    public void gest_methods(List<MethodNode> methods) {
        if (methods == null) return;
        for (MethodNode method : methods) gest_method(method);
    }

    public void gest_method(MethodNode m) {
        TypeNode type = m.getType() != null ? m.getType() : new TypeNode(TypeNode.Kind.VOID, m.line, m.column);
        Simbol.STIPUS st = m.isFunction() ? Simbol.STIPUS.FUNCIO : Simbol.STIPUS.PROCEDIMENT;

        Simbol s = new Simbol(m.getName(), type, st, m.line, m.column, m.getParams(), currentScope.getScopeId());

        if (!currentScope.add(s)) {
            ErrorManager.add(new CompilerError(m.line, m.column, CompilerError.TYPE.SEMANTIC,
                    "Nom ja utilitzat: " + m.getName()));
        }

        openScope();

        for (ArgNode arg : m.getParams()) {
            String name = arg.getVar().getName();
            TypeNode argType = arg.getType();
            Simbol sA = new Simbol(name, argType, Simbol.STIPUS.VARIABLE, arg.line, arg.column, currentScope.getScopeId());

            if (currentScope.lookUp(name) != null) {
                ErrorManager.add(new CompilerError(arg.line, arg.column, CompilerError.TYPE.SEMANTIC,
                        "Argument ja declarat: " + name));
            } else {
                currentScope.add(sA);
            }
        }

        gest_instrs(m.getInstrs());

        if (m.isFunction()) {
            ExprNode retExpr = m.getReturnExpr();
            if (retExpr == null) {
                ErrorManager.add(new CompilerError(m.line, m.column, CompilerError.TYPE.SEMANTIC,
                        "Falta l'expressió de retorn a la funció " + m.getName()));
            } else {
                gest_expr(retExpr);
                if (!retExpr.getKind().equals(m.getType().getKind())) {
                    ErrorManager.add(new CompilerError(retExpr.line, retExpr.column, CompilerError.TYPE.SEMANTIC,
                            "El tipus del retorn " + retExpr.getKind() +
                                    " no coincideix amb el tipus de la funció: " + m.getType().getKind()));
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
        ExprNode.VarNode var = a.getId();
        ExprNode expr = a.getExpr();

        Simbol s = currentScope.lookUp(var.getName());
        if (s == null) {
            ErrorManager.add(new CompilerError(a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "Variable no declarada: " + var.getName()));
            return;
        }

        // Comprovar constant
        if (s.isConst()) {
            ErrorManager.add(new CompilerError(a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "No es pot assignar a una constant: " + var.getName()));
        }

        // Comprovar tipus de l'expressió
        gest_expr(expr);
        if (!s.getKind().equals(expr.getKind())) {
            ErrorManager.add(new CompilerError(a.line, a.column, CompilerError.TYPE.SEMANTIC,
                    "Tipus incompatible: variable " + var.getName() + " és " + s.getKind() +
                            " i s'hi assigna " + expr.getKind()));
        }

        s.setInitialized(true);
    }

    public void gest_call(InstrNode.CallNode c) {
        Simbol s = currentScope.lookUp(c.getFunctionName());

        if (s == null) {
            ErrorManager.add(new CompilerError(c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "Mètode no declarat: " + c.getFunctionName()));
            return;
        }

        if (!s.isMethod()) {
            ErrorManager.add(new CompilerError(c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    c.getFunctionName() + " no és un mètode."));
            return;
        }

        List<ArgNode> params = s.getParams();
        List<ExprNode> args = c.getExpr();

        if (params == null) params = new ArrayList<>();
        if (args == null) args = new ArrayList<>();

        if (params.size() != args.size()) {
            ErrorManager.add(new CompilerError(c.line, c.column, CompilerError.TYPE.SEMANTIC,
                    "Nombre d'arguments incorrecte a la crida de " + c.getFunctionName() +
                            ": s'esperaven " + params.size() + ", s'han passat " + args.size()));
            return;
        }

        for (int i = 0; i < params.size(); i++) {
            gest_expr(args.get(i));
            TypeNode.Kind esperat = params.get(i).getType().getKind();
            TypeNode.Kind rebut = args.get(i).getKind();

            if (!esperat.equals(rebut)) {
                ErrorManager.add(new CompilerError(args.get(i).line, args.get(i).column, CompilerError.TYPE.SEMANTIC,
                        "Tipus incorrecte a l'argument " + (i + 1) + " de la crida a " + c.getFunctionName() +
                                ": s'esperava " + esperat + " i s'ha rebut " + rebut));
            }
        }
    }

    public void gest_input(InstrNode.InputNode in) {
        Simbol s = currentScope.lookUp(in.getId());

        if (s == null) {
            ErrorManager.add(new CompilerError(in.line, in.column, CompilerError.TYPE.SEMANTIC,
                    "Variable " + in.getId() + " no declarada."));
        } else if (s.isConst()) {
            ErrorManager.add(new CompilerError(in.line, in.column, CompilerError.TYPE.SEMANTIC,
                    "No es pot fer input sobre una constant: " + in.getId()));
        }
    }

    public void gest_output(InstrNode.OutputNode o) {
        gest_expr(o.getExpr());
    }

    public void gest_cond(CondNode c) {
        if (c instanceof CondNode.IfNode ifNode) {
            gest_expr(ifNode.getCondition());
            if (notBoolean(ifNode.getCondition().getKind())) {
                ErrorManager.add(new CompilerError(ifNode.line, ifNode.column, CompilerError.TYPE.SEMANTIC,
                        "La condició del if ha de ser de tipus booleà."));
            }

            openScope();
            gest_instrs(ifNode.getThenInstrs());
            closeScope();

            if (ifNode.getElseInstrs() != null) {
                openScope();
                gest_instrs(ifNode.getElseInstrs());
                closeScope();
            }
        } else if (c instanceof CondNode.SwitchNode switchNode) {
            Simbol s = currentScope.lookUp(switchNode.getId());
            if (s == null) {
                ErrorManager.add(new CompilerError(switchNode.line, switchNode.column, CompilerError.TYPE.SEMANTIC,
                        "Variable no declarada al switch: " + switchNode.getId()));
                return;
            }

            TypeNode.Kind switchType = s.getKind();
            List<CondNode.SwitchNode.CaseNode> cases = switchNode.getCases();

            if (cases != null) {
                for (CondNode.SwitchNode.CaseNode caseNode : cases) {
                    ExprNode caseValue = caseNode.getValue();

                    gest_expr(caseValue);
                    TypeNode.Kind caseType = caseValue.getKind();

                    if (!caseType.equals(switchType) && caseType != TypeNode.Kind.UNKNOWN) {
                        ErrorManager.add(new CompilerError(caseNode.line, caseNode.column, CompilerError.TYPE.SEMANTIC,
                                "Tipus incompatible al 'case': esperat " + switchType +
                                        " però trobat " + caseType));
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
        if (l instanceof LoopNode.WhileNode w) {
            gest_expr(w.getCondition());
            if (notBoolean(w.getCondition().getKind())) {
                ErrorManager.add(new CompilerError(w.line, w.column, CompilerError.TYPE.SEMANTIC,
                        "La condició del while ha de ser booleana."));
            }

            openScope();
            gest_instrs(w.getBody());
            closeScope();
        } else if (l instanceof LoopNode.DoWhileNode dw) {
            openScope();
            gest_instrs(dw.getBody());
            closeScope();

            gest_expr(dw.getCondition());

            if (notBoolean(dw.getCondition().getKind())) {
                ErrorManager.add(new CompilerError(dw.line, dw.column, CompilerError.TYPE.SEMANTIC,
                        "La condició del do-while ha de ser booleana."));
            }
        }
    }


    public void gest_expr(ExprNode e) {
        if (e == null) return;

        if (e instanceof ExprNode.BinaryOpNode bin) {
            gest_expr(bin.getLeft());
            gest_expr(bin.getRight());
            TypeNode.Kind left = bin.getLeft().getKind();
            TypeNode.Kind right = bin.getRight().getKind();

            switch (bin.getOperator()) {
                case "+": case "-": case "*": case "/":
                    if (notNumeric(left) || notNumeric(right)) {
                        ErrorManager.add(new CompilerError(bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                                "Operació aritmètica només permesa entre enters (" + left + " " + bin.getOperator() + " " + right + ")"));
                        bin.setKind(TypeNode.Kind.UNKNOWN);
                    } else bin.setKind(TypeNode.Kind.DOUBLE);
                    break;

                case "==": case "!=":
                    bin.setKind(TypeNode.Kind.BOOLEAN);
                    break;

                case "<": case "<=": case ">": case ">=":
                    if (notNumeric(left) || notNumeric(right)) {
                        ErrorManager.add(new CompilerError(bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                                "Operadors relacionals només permesos amb enters (" + left + " " + bin.getOperator() + " " + right + ")"));
                    }
                    bin.setKind(TypeNode.Kind.BOOLEAN);
                    break;

                case "i": case "o":
                    if (notBoolean(left) || notBoolean(right)) {
                        ErrorManager.add(new CompilerError(bin.line, bin.column, CompilerError.TYPE.SEMANTIC,
                                "Operació lògica només permesa entre booleans (" + left + " " + bin.getOperator() + " " + right + ")"));
                    }
                    bin.setKind(TypeNode.Kind.BOOLEAN);
                    break;

                default:
                    bin.setKind(TypeNode.Kind.UNKNOWN);
            }
        }

        else {
            gest_term(e);
        }
    }

    public void gest_term(ExprNode e) {
        if (e == null) return;
        if (e instanceof ExprNode.LiteralNode lit) {};

        if (e instanceof ExprNode.VarNode var) {
            Simbol s = currentScope.lookUp(var.getName());

            if (s == null) {
                ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC,
                        "Variable no declarada: " + var.getName()));
                var.setKind(TypeNode.Kind.UNKNOWN);
            } else {
                var.setKind(s.getKind());
            }

            if (var.getIndex() != null) {
                gest_expr(var.getIndex());

                if (notNumeric(var.getIndex().getKind())) {
                    ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC,
                            "L’índex d’una tupla ha de ser enter."));
                } else if (!isTuple(var.getKind())) {
                    ErrorManager.add(new CompilerError(var.line, var.column, CompilerError.TYPE.SEMANTIC,
                            "No es pot indexar una variable que no és de tipus tupla."));
                }
            }
        }

        else if (e instanceof ExprNode.TupleNode t) {
            // Comprovar cada element
            for (ExprNode elem : t.getElements()) gest_expr(elem);
            t.setKind(TypeNode.Kind.TUPLE);
        }

        else if (e instanceof ExprNode.UnaryOpNode un) {
            gest_expr(un.getExpr());

            if (un.getOperator().equals("NOT")) {
                if (notBoolean(un.getExpr().getKind())) {
                    ErrorManager.add(new CompilerError(un.line, un.column, CompilerError.TYPE.SEMANTIC,
                            "L’operador 'NOT' només pot aplicar-se sobre valors booleans."));
                }
                un.setKind(TypeNode.Kind.BOOLEAN);
            } else {
                un.setKind(un.getExpr().getKind());
            }
        }

        else if (e instanceof ExprNode.ExprInstrNode callExpr) {
            gest_call(callExpr.getCall());

            Simbol s = currentScope.lookUp(callExpr.getCall().getFunctionName());

            if (s == null) {
                ErrorManager.add(new CompilerError(callExpr.line, callExpr.column, CompilerError.TYPE.SEMANTIC,
                        "Crida a mètode no declarat: " + callExpr.getCall().getFunctionName()));
                e.setKind(TypeNode.Kind.UNKNOWN);
            } else if (s.getStipus() == Simbol.STIPUS.PROCEDIMENT) {
                ErrorManager.add(new CompilerError(callExpr.line, callExpr.column, CompilerError.TYPE.SEMANTIC,
                        "Un procediment no pot aparèixer dins una expressió: " + s.getName()));
                e.setKind(TypeNode.Kind.UNKNOWN);
            } else {
                e.setKind(s.getKind());
            }
        }
    }



    private boolean notNumeric(TypeNode.Kind t) {
        return (t != TypeNode.Kind.DOUBLE);
    }

    private boolean notBoolean(TypeNode.Kind t) {
        return (t != TypeNode.Kind.BOOLEAN);
    }

    private boolean isTuple(TypeNode.Kind t) {
        return (t == TypeNode.Kind.TUPLE);
    }

}
