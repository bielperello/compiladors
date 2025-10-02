package symbols;


import java.util.List;

public class DeclNode extends Node {
    public boolean isConst;
    public TypeNode type;
    public List<String> ids;
    public ExprNode expr;

    public DeclNode(boolean isConst, TypeNode type, List<String> ids, ExprNode expr,
                    int line, int column) {
        super (line, column);
        this.isConst = isConst;
        this.type = type;
        this.ids = ids;
        this.expr = expr;
    }

    @Override
    public void generateCode() {
        for (String id : ids) {
            if (expr != null) {
                System.out.println((isConst ? "const " : "") + type + " " + id + " = EXPRESIÓ");
            } else {
                System.out.println((isConst ? "const " : "") + type + " " + id + ";");
            }
        }
    }
}
