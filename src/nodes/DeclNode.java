package nodes;

import nodes.expresions.ExprNode;

public class DeclNode extends Node {
    private final TypeNode type;
    private final String id;
    private final ExprNode expr;
    private boolean isConst;

    public DeclNode(TypeNode type, String id, ExprNode expr, boolean isConst, int line, int column) {
        super (line, column);
        this.type = type;
        this.id = id;
        this.expr = expr;
        this.isConst = isConst;
    }

    public DeclNode(TypeNode type, String id, ExprNode expr, int line, int column) {
        super (line, column);
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    public ExprNode getExpr() { return this.expr; }
    public TypeNode getType() {
        return type;
    }
    public String getId() { return this.id; }

    public boolean isConst() { return this.isConst; }

    @Override
    public void generateCode() {
    }
}
