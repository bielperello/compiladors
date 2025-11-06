package nodes.instructions;

import nodes.expresions.RefNode;
import nodes.expresions.ExprNode;

public class AssignNode extends InstrNode {
    private final RefNode ref;
    private final ExprNode expr;

    public AssignNode(RefNode ref, ExprNode expr, int line, int column) {
        super(line, column);
        this.ref = ref;
        this.expr = expr;
    }

    public RefNode getRef() { return this.ref; }
    public ExprNode getExpr() { return this.expr; }

    @Override
    public void generateCode() {}
}
