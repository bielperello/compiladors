package nodes.instructions.conditionals;

import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.List;

public class IfNode extends CondNode {
    private final ExprNode condition;
    private final List<InstrNode> thenInstrs;
    private final List<InstrNode> elseInstrs;

    public IfNode(ExprNode condition, List<InstrNode> thenInstrs, List<InstrNode> elseInstrs,
                  int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenInstrs = thenInstrs;
        this.elseInstrs = elseInstrs;
    }

    public ExprNode getCondition() { return this.condition; }
    public List<InstrNode> getThenInstrs() { return this.thenInstrs; }
    public List<InstrNode> getElseInstrs() { return this.elseInstrs; }

    @Override
    public void generateCode(){};
}
