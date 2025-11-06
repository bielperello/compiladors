package nodes.instructions.loops;

import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.List;

public class DoWhileNode extends LoopNode {
    public ExprNode condition;
    public List<InstrNode> body;

    public DoWhileNode(List<InstrNode> body, ExprNode condition, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public ExprNode getCondition() { return this.condition; }
    public List<InstrNode> getBody() { return this.body; }

    @Override
    public void generateCode() {
        // pseudocodi per generar el do-while
    }
}
