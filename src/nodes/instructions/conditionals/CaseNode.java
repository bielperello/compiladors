package nodes.instructions.conditionals;

import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.ArrayList;
import java.util.List;

public class CaseNode extends InstrNode {
    private final ExprNode value;
    private final List<InstrNode> instrs;

    public CaseNode(ExprNode value, List<InstrNode> instrs, int line, int column) {
        super(line, column);
        this.value = value;
        this.instrs = instrs != null ? instrs : new ArrayList<>();
    }

    public ExprNode getValue() { return this.value; }
    public List<InstrNode> getInstrs() { return this.instrs; }

    @Override
    public void generateCode(){}
}