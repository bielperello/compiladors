package nodes;

import java.util.List;

public abstract class LoopNode extends InstrNode {
    public LoopNode(int line, int column) {
        super(line, column);
    }

    public static class WhileNode extends LoopNode {
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

    public static class DoWhileNode extends LoopNode {
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
}
