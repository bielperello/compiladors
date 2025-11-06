package nodes.instructions.loops;

import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.List;

public class WhileNode extends LoopNode {
    private ExprNode condition;
    private List<InstrNode> body;

    public WhileNode(ExprNode condition, List<InstrNode> body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public ExprNode getCondition() { return this.condition; }
    public List<InstrNode> getBody() { return this.body; }

    @Override
    public void generateCode() {
        // pseudocodi per generar el while
    }
}
