package ast.expresions;

import codegen.*;
import ast.Kind;

import java.util.List;

/** Operador binari (+, -, i, o, ==, <, etc.) */
public class BinaryOpNode extends ExprNode {
    private final ExprNode left;
    private final ExprNode right;
    private final String operator;

    public BinaryOpNode(ExprNode left, String operator, ExprNode right) {
        super(left.line, left.column);
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.mode = ModeExpr.MODERESULT;
    }

    public ExprNode getLeft() { return left; }
    public ExprNode getRight() { return right; }
    public String getOperator() { return operator; }

    @Override
    public void generateCode() {
        if (operator.equals("i") || operator.equals("o")) {
            left.generateCode();

            int m1 = EtiquetaManager.novaEtiqueta("M"); // etiqueta marcador M1 (continuar amb la comprovació)
            CodeGenerator.posaEtiqueta(m1);

            right.generateCode();

            switch (operator) {
                case "i" -> {
                    CodeGenerator.backpatch(left.getTrueList(), m1);

                    this.falseList = CodeGenerator.concat(left.getFalseList(), right.getFalseList());
                    this.trueList = right.getTrueList();
                }

                case "o" -> {
                    CodeGenerator.backpatch(left.getFalseList(), m1);

                    this.trueList = CodeGenerator.concat(left.getTrueList(), right.getTrueList());
                    this.falseList = right.getFalseList();
                }
            }
        } else {
            left.generateCode();
            right.generateCode();

            int leftVar = left.getResultVar();   // E1.r
            int rightVar = right.getResultVar(); // E2.r

            // Distingeix entre tipus d’operadors
            switch (operator) {
                // --- Operacions aritmètiques ---
                case "+", "-", "*", "/" -> {
                    int t = CodeGenerator.novaVarTemporal(); // t = novavar
                    OpCode op = switch (operator) {
                        case "+" -> {
                            if (left.getKind() == Kind.CADENA) { yield OpCode.CONCAT;} else { yield OpCode.ADD;}
                        }
                        case "-" -> OpCode.SUB;
                        case "*" -> OpCode.PROD;
                        case "/" -> OpCode.DIV;
                        default -> throw new RuntimeException("Operador aritmètic no suportat: " + operator);
                    };
                    CodeGenerator.genera(op, leftVar, rightVar, t);
                    this.resultVar = t; // E0.r = t
                }

                // --- Operacions relacionals (backpatching) ---
                case "==", "!=", "<", "<=", ">", ">=" -> {
                    OpCode op = switch (operator) {
                        case "==" -> OpCode.IF_EQ;
                        case "!=" -> OpCode.IF_NE;
                        case "<"  -> OpCode.IF_LT;
                        case "<=" -> OpCode.IF_LE;
                        case ">"  -> OpCode.IF_GT;
                        case ">=" -> OpCode.IF_GE;
                        default -> throw new RuntimeException("Operador relacional desconegut: " + operator);
                    };

                    CodeGenerator.genera(op, leftVar, rightVar, CodeGenerator.NUL_VAL); // if E1.r op E2.r goto ???
                    int posIf = CodeGenerator.pc();

                    CodeGenerator.genera(OpCode.GOTO, CodeGenerator.NUL_VAL); // goto ???
                    int posGoto = CodeGenerator.pc();

                    // crea les llistes de pendents
                    this.trueList = List.of(posIf);
                    this.falseList = List.of(posGoto);
                }
            }
        }
    }
}
