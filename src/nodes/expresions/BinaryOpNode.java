package nodes.expresions;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;

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
        left.generateCode();
        right.generateCode();

        int leftVar = left.getResultVar();   // E1.r
        int rightVar = right.getResultVar(); // E2.r
        int t = CodeGenerator.novaVarTemporal(); // t = novavar

        // Distingeix entre tipus d’operadors
        switch (operator) {
            // --- Operacions aritmètiques ---
            case "+" -> CodeGenerator.genera(OpCode.ADD, leftVar, rightVar, t);
            case "-" -> CodeGenerator.genera(OpCode.SUB, leftVar, rightVar, t);
            case "*" -> CodeGenerator.genera(OpCode.PROD, leftVar, rightVar, t);
            case "/" -> CodeGenerator.genera(OpCode.DIV, leftVar, rightVar, t);

            // --- Operacions lògiques ---
            case "i" -> CodeGenerator.genera(OpCode.AND, leftVar, rightVar, t);
            case "o" -> CodeGenerator.genera(OpCode.OR, leftVar, rightVar, t);

            // --- Operacions relacionals ---
            case "==", "!=", "<", "<=", ">", ">=" -> {
                int eTrue = EtiquetaManager.novaEtiqueta("E");  // etiqueta per al cas cert
                int eEnd  = EtiquetaManager.novaEtiqueta("E");  // etiqueta final

                OpCode relOp = switch (operator) {
                    case "==" -> OpCode.IF_EQ;
                    case "!=" -> OpCode.IF_NE;
                    case "<"  -> OpCode.IF_LT;
                    case "<=" -> OpCode.IF_LE;
                    case ">"  -> OpCode.IF_GT;
                    case ">=" -> OpCode.IF_GE;
                    default -> throw new RuntimeException("Operador relacional desconegut: " + operator);
                };

                CodeGenerator.genera(relOp, leftVar, rightVar, eTrue);  // if E1.r op E2.r goto eTrue
                CodeGenerator.genera(OpCode.COPY, "0", t); // t = 0  (fals)
                CodeGenerator.genera(OpCode.GOTO, eEnd); // goto eEnd
                CodeGenerator.posaEtiqueta(eTrue); // genera SKIP amb id eTrue
                CodeGenerator.genera(OpCode.COPY, "-1", t); // t = -1 (cert)
                CodeGenerator.posaEtiqueta(eEnd);  // genera SKIP amb id eEnd
            }

            default -> throw new RuntimeException("Operador no suportat: " + operator);
        }

        this.resultVar = t; // E0.r = t
    }
}
