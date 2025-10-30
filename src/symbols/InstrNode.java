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
        private String functionName;
        private List<ExprNode> expr;

        public CallNode(String functionName, List<ExprNode> expr, int line, int column) {
            super(line, column);
            this.functionName = functionName;
            this.expr = expr;
        }

        public String getFunctionName() { return this.functionName; }
        public List<ExprNode> getExpr() { return this.expr; }

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
        private final String id;

        public InputNode(String id, int line, int column) {
            super(line, column);
            this.id = id;
        }

        public String getId() { return this.id; }

        @Override
        public void generateCode() {
            System.out.println("input(" + id + ");");
        }
    }

    public static class OutputNode extends InstrNode {
        private final ExprNode expr;

        public OutputNode(ExprNode expr, int line, int column) {
            super(line, column);
            this.expr = expr;
        }

        public ExprNode getExpr() { return this.expr; }

        @Override
        public void generateCode() {
            System.out.println("output( EXPRESSIÓ );");
        }
    }

    public static class AssignNode extends InstrNode {
        private final ExprNode.VarNode id;
        private final ExprNode expr;

        public AssignNode(ExprNode.VarNode id, ExprNode expr, int line, int column) {
            super(line, column);
            this.id = id;
            this.expr = expr;
        }

        public ExprNode.VarNode getId() { return this.id; }
        public ExprNode getExpr() { return this.expr; }

        @Override
        public void generateCode() {}
    }

    public static class InstrDeclNode extends InstrNode {
        private DeclNode decl;

        public InstrDeclNode(DeclNode decl) {
            super(decl.line, decl.column);
            this.decl = decl;
        }

        public DeclNode getDecl() {return this.decl; }
        @Override
        public void generateCode() {
            decl.generateCode();
        }
    }
}
