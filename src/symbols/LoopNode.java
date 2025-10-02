package symbols;

import java.util.List;

public abstract class LoopNode extends InstrNode {
    public LoopNode(int line, int column) {
        super(line, column);
    }

    public static class WhileNode extends LoopNode {
        public ExprNode condition;
        public List<InstrNode> body;

        public WhileNode(ExprNode condition, List<InstrNode> body, int line, int column) {
            super(line, column);
            this.condition = condition;
            this.body = body;
        }

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

        @Override
        public void generateCode() {
            // pseudocodi per generar el do-while
        }
    }
}
