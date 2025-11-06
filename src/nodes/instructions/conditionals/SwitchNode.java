package nodes.instructions.conditionals;

import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.ArrayList;
import java.util.List;

public class SwitchNode extends CondNode {
    private final ExprNode expr;
    private final List<CaseNode> cases;
    private final List<InstrNode> defaultInstrs;

    public SwitchNode(ExprNode expr, List<CaseNode> cases, List<InstrNode> defaultInstrs,
                      int line, int column) {
        super(line, column);
        this.expr = expr;
        this.cases = cases != null ? cases : new ArrayList<>();
        this.defaultInstrs = defaultInstrs;
    }

    public ExprNode getExpr() { return this.expr; }
    public List<CaseNode> getCases() { return this.cases; }
    public List<InstrNode> getDefaultInstrs() { return this.defaultInstrs; }

    @Override
    public void generateCode(){}
}
