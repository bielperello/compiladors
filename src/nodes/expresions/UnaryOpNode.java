package nodes.expresions;

import codegen.CodeGenerator;
import codegen.OpCode;
import nodes.TypeNode;

public class UnaryOpNode extends ExprNode {
    private final ExprNode expr;
    private final String operator;

    public UnaryOpNode(String operator, ExprNode expr, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.operator = operator;
        this.mode = ModeExpr.MODERESULT;
    }

    public ExprNode getExpr() { return expr; }
    public String getOperator() { return operator; }

    @Override
    public void generateCode() {
        expr.generateCode();

        int operandVar = expr.getResultVar(); // E1.r

        int t = CodeGenerator.novaVarTemporal(); // t = novavar

        OpCode opCode = switch (operator) {
            case "not" -> OpCode.NOT;
            case "-" -> OpCode.NEG;
            default -> throw new RuntimeException("Operador unari no suportat: " + operator);
        };

        CodeGenerator.genera(opCode, operandVar, CodeGenerator.NUL_VAL, t); // t = op E1.r

        this.resultVar = t; // E0.r = t
    }
}