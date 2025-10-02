package symbols;

import java.util.List;

public abstract class InstrNode extends Node {
    public InstrNode(int line, int column) {
        super(line, column);
    }

    public abstract void generateCode();

    // ------------------------
    // Classes concretes
    // ------------------------

    public static class CallNode extends InstrNode {
        public String functionName;
        public List<ExprNode> args;

        public CallNode(String functionName, List<ExprNode> args, int line, int column) {
            super(line, column);
            this.functionName = functionName;
            this.args = args;
        }

        @Override
        public void generateCode() {
            System.out.print(functionName + "(");
            for (int i = 0; i < args.size(); i++) {
                System.out.print(args.get(i));
                if (i < args.size() - 1) System.out.print(", ");
            }
            System.out.println(");");
        }
    }

    public static class LoopNode extends InstrNode {
        public ExprNode condition;
        public List<InstrNode> body;

        public LoopNode(ExprNode condition, List<InstrNode> body, int line, int column) {
            super(line, column);
            this.condition = condition;
            this.body = body;
        }

        @Override
        public void generateCode() {
            System.out.println("while ( EXPRESSIÓ ) {");
            for (InstrNode instr : body) instr.generateCode();
            System.out.println("}");
        }
    }

    public static class InputNode extends InstrNode {
        public String id;

        public InputNode(String id, int line, int column) {
            super(line, column);
            this.id = id;
        }

        @Override
        public void generateCode() {
            System.out.println("input(" + id + ");");
        }
    }

    public static class OutputNode extends InstrNode {
        public ExprNode expr;

        public OutputNode(ExprNode expr, int line, int column) {
            super(line, column);
            this.expr = expr;
        }

        @Override
        public void generateCode() {
            System.out.println("output( EXPRESSIÓ );");
        }
    }
}
