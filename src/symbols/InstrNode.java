package symbols;

import java_cup.runtime.Symbol;

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
        public List<ExprNode> expr;

        public CallNode(String functionName, List<ExprNode> expr, int line, int column) {
            super(line, column);
            this.functionName = functionName;
            this.expr = expr;
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

    public static class InstrDeclNode extends InstrNode {
        private DeclNode decl;

        public InstrDeclNode(DeclNode decl) {
            super(decl.line, decl.column);
            this.decl = decl;
        }

        @Override
        public void generateCode() {
            decl.generateCode();
        }
    }
}
