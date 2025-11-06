package nodes.instructions;

import nodes.expresions.ExprNode;
import simbols.descripcio.DescripcioTipus;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends InstrNode {
    private String functionName;
    private List<ExprNode> expr;
    private DescripcioTipus retornTipus;

    public CallNode(String functionName, List<ExprNode> expr, int line, int column) {
        super(line, column);
        this.functionName = functionName;
        this.expr = expr != null ? expr : new ArrayList<>();
    }

    public String getFunctionName() { return this.functionName; }
    public List<ExprNode> getExpr() { return this.expr; }
    public DescripcioTipus getRetornTipus() { return this.retornTipus; }

    public void setRetornTipus(DescripcioTipus retornTipus) {
        this.retornTipus = retornTipus;
    }

    @Override
    public void generateCode() {
        System.out.print(functionName + "(");
        for (int i = 0; i < expr.size(); i++) {
            System.out.print(expr.get(i));
            if (i < expr.size() - 1) System.out.print(", ");
        }
        System.out.println(");");
    }
}
