package symbols;

import java.util.ArrayList;
import java.util.List;

public class DeclNode extends Node {
    public boolean isConst;
    public TypeNode type;
    public List<ExprNode.VarNode> ids = new ArrayList<>();
    public ExprNode expr;

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

    public TypeNode getType() {
        return type;
    }

    @Override
    public void generateCode() {
        for (ExprNode.VarNode id : ids) {
            if (expr != null) {
                System.out.println((isConst ? "const " : "") + type + " " + id + " = EXPRESIÓ");
            } else {
                System.out.println((isConst ? "const " : "") + type + " " + id + ";");
            }
        }
    }
}
