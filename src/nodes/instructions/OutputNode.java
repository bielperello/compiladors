package nodes.instructions;

import nodes.expresions.ExprNode;

public class OutputNode extends InstrNode {
    private final ExprNode expr;

    public OutputNode(ExprNode expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public ExprNode getExpr() { return this.expr; }

    @Override
    public void generateCode() {
        System.out.println("output( EXPRESSIÓ );");
    }
}