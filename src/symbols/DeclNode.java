package symbols;

import java.util.List;

public class DeclNode extends Node {
    private final boolean isConst;
    private final TypeNode type;
    private final List<ExprNode.VarNode> ids;
    private final ExprNode expr;

    public DeclNode(boolean isConst, TypeNode type, List<ExprNode.VarNode> ids, ExprNode expr,
                    int line, int column) {
        super (line, column);
        this.isConst = isConst;
        this.type = type;
        this.ids = ids;
        this.expr = expr;
    }

    public DeclNode(boolean isConst, TypeNode type, List<ExprNode.VarNode> ids, ExprNode expr) {
        super (type.line, type.column);
        this.isConst = isConst;
        this.type = type;
        this.ids = ids;
        this.expr = expr;
    }

    public boolean isConst() { return this.isConst; }
    public ExprNode getExpr() { return this.expr; }
    public TypeNode getType() {
        return type;
    }
    public List<ExprNode.VarNode> getIds() { return this.ids; }

    @Override
    public void generateCode() {
    }
}
