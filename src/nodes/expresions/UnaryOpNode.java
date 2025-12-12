package nodes.expresions;

import codegen.CodeGenerator;
import codegen.OpCode;

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
        expr.generateCode(); // genera el codi de l'expressió E1

        switch (operator) {
            case "not" -> {
                // intercanvi de llistes cert/fals
                this.trueList = expr.getFalseList();
                this.falseList = expr.getTrueList();
            }

            case "-" -> {
                int operandVar = expr.getResultVar(); // E1.r
                int t = CodeGenerator.novaVarTemporal(); // t = novavar
                CodeGenerator.genera(OpCode.NEG, operandVar, t); // t = op E1.r

                this.resultVar = t; // E0.r = t
            }
            default -> throw new RuntimeException("Operador unari no suportat: " + operator);
        };
    }
}