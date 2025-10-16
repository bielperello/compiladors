package symbols;

import java.util.List;

public abstract class ExprNode extends Node {
    protected TypeNode.Kind kind;

    public ExprNode(int line, int column) {
        super(line, column);
    }

    public TypeNode.Kind getKind() {
        return kind;
    }

    public void setKind(TypeNode.Kind type) {
        this.kind = type;
    }

    public static class LiteralNode extends ExprNode {
        private Object value;

        public LiteralNode(Object value, int line, int column) {
            super(line, column);
            this.value = value;
            setKind();
        }

        public LiteralNode(Object value) {
            super(Integer.MAX_VALUE, Integer.MAX_VALUE);
            this.value = value;
            setKind();
        }

        private void setKind() {
            if (value instanceof Double || value instanceof Integer) {
                this.kind = TypeNode.Kind.DOUBLE;
            } else if (value instanceof Boolean) {
                this.kind = TypeNode.Kind.BOOLEAN;
            } else if (value instanceof String) {
                this.kind = TypeNode.Kind.STRING;
            } else if (value instanceof Character) {
                this.kind = TypeNode.Kind.CHARACTER;
            } else if (value instanceof List) {
                this.kind = TypeNode.Kind.TUPLE;
            }
        }

        public Object getValue() { return this.value; }

        @Override
        public void generateCode(){};
    }

    public static class TupleNode extends ExprNode {
        private final List<ExprNode> elements;

        public TupleNode(List<ExprNode> list, int line, int column) {
            super(line, column);
            this.elements = list;
            setKind(TypeNode.Kind.TUPLE);
        }

        public List<ExprNode> getElements() {
            return elements;
        }

        @Override
        public void generateCode(){}
    }

    public static class VarNode extends ExprNode {
        private String name;
        private int index;

        public VarNode(String name, Double index, int line, int column) {
            super(line, column);
            this.name = name;
            this.index = index.intValue();
        }

        public VarNode(String name) {
            super(Integer.MAX_VALUE, Integer.MAX_VALUE);
            this.name = name;
            this.index = Integer.MIN_VALUE;
        }

        public void setIndex(Double i) { this.index = i.intValue(); }
        public int getIndex() { return this.index; }
        public String getName() {
            return this.name;
        }

        @Override
        public void generateCode() {
            System.out.print(name);
        }
    }

    public static class BinaryOpNode extends ExprNode {
        public ExprNode left;
        public ExprNode right;
        public String operator; // "+", "-", "*", "/", "i", "o", "==", "<", etc.

        public BinaryOpNode(ExprNode left, String operator, ExprNode right) {
            super(left.line, left.column);
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public void generateCode() {
            left.generateCode();
            System.out.print(" " + operator + " ");
            right.generateCode();
        }
    }

    public static class UnaryOpNode extends ExprNode {
        public ExprNode expr;
        public String operator;

        public UnaryOpNode(String operator, ExprNode expr, int line, int column) {
            super(line, column);
            this.operator = operator;
            this.expr = expr;
        }

        @Override
        public void generateCode() {
            System.out.print(operator + " ");
            expr.generateCode();
        }
    }

    public static class ExprInstrNode extends ExprNode {
        private InstrNode.CallNode call;

        public ExprInstrNode(InstrNode.CallNode call) {
            super(call.line, call.column);
            this.call = call;
        }

        @Override
        public void generateCode() {
            call.generateCode();
            System.out.println(";");
        }
    }
}
