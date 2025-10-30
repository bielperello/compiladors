package symbols;

import java.util.List;

public class DeclNode extends Node {
    private final TypeNode type;
    private final List<ExprNode.VarNode> ids;
    private final ExprNode expr;
    private final boolean isConst;

    public DeclNode(TypeNode type, List<ExprNode.VarNode> ids, ExprNode expr, boolean isConst,
                    int line, int column) {
        super (line, column);
        this.type = type;
        this.ids = ids;
        this.expr = expr;
        this.isConst = isConst;
    }

    public ExprNode getExpr() { return this.expr; }
    public TypeNode getType() {
        return type;
    }
    public List<ExprNode.VarNode> getIds() { return this.ids; }

    public boolean isConst() { return this.isConst; }

    @Override
    public void generateCode() {
    }
}
