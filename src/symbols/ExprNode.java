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
        private final Object value;

        public LiteralNode(Object value, int line, int column) {
            super(line, column);
            this.value = value;
            setKind();
        }

        private void setKind() {
            if (value instanceof Integer || value instanceof Double || value instanceof Float) {
                this.kind = TypeNode.Kind.DOUBLE;
            } else if (value instanceof Boolean) {
                this.kind = TypeNode.Kind.BOOLEAN;
            } else if (value instanceof String) {
                this.kind = TypeNode.Kind.STRING;
            } else if (value instanceof Character) {
                this.kind = TypeNode.Kind.CHARACTER;
            } else if (value instanceof List) {
                this.kind = TypeNode.Kind.TUPLE;
            } else {
                this.kind = TypeNode.Kind.UNKNOWN;
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
        private final String name;
        private final ExprNode index;

        public VarNode(String name, int line, int column) {
            super(line, column);
            this.name = name;
            this.index = null;
        }

        public VarNode(String name, ExprNode index , int line, int column) {
            super(line, column);
            this.name = name;
            this.index = index;
        }

        public ExprNode getIndex() { return this.index; }
        public String getName() {
            return this.name;
        }

        @Override
        public void generateCode() {
            System.out.print(name);
        }
    }

    public static class BinaryOpNode extends ExprNode {
        private final ExprNode left;
        private final ExprNode right;
        private final String operator; // "+", "-", "*", "/", "i", "o", "==", "<", etc.

        public BinaryOpNode(ExprNode left, String operator, ExprNode right) {
            super(left.line, left.column);
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public ExprNode getLeft() { return this.left; }
        public ExprNode getRight() { return this.right; }
        public String getOperator() { return this.operator; }

        public static TypeNode.Kind inferKind(String op, TypeNode.Kind left, TypeNode.Kind right) {
            if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
                if (left == TypeNode.Kind.DOUBLE && right == TypeNode.Kind.DOUBLE) return TypeNode.Kind.DOUBLE;
                return TypeNode.Kind.UNKNOWN;
            }
            if (op.equals("i") || op.equals("o")) {
                if (left == TypeNode.Kind.BOOLEAN && right == TypeNode.Kind.BOOLEAN)
                    return TypeNode.Kind.BOOLEAN;
                return TypeNode.Kind.UNKNOWN;
            }
            if (List.of("==", "!=", "<", "<=", ">", ">=").contains(op))
                return TypeNode.Kind.BOOLEAN;

            return TypeNode.Kind.UNKNOWN;
        }

        @Override
        public void generateCode() {
            left.generateCode();
            System.out.print(" " + operator + " ");
            right.generateCode();
        }
    }

    public static class UnaryOpNode extends ExprNode {
        private final ExprNode expr;
        private final String operator;

        public UnaryOpNode(String operator, ExprNode expr, int line, int column) {
            super(line, column);
            this.operator = operator;
            this.expr = expr;
        }

        public ExprNode getExpr() { return this.expr; }
        public String getOperator() { return this.operator; }

        public static TypeNode.Kind inferKind(String op, TypeNode.Kind operand) {
            if (op.equals("NOT")) {
                return operand == TypeNode.Kind.BOOLEAN ? TypeNode.Kind.BOOLEAN : TypeNode.Kind.UNKNOWN;
            }
            return operand;
        }


        @Override
        public void generateCode() {
            System.out.print(operator + " ");
            expr.generateCode();
        }
    }

    public static class ExprInstrNode extends ExprNode {
        private final InstrNode.CallNode call;

        public ExprInstrNode(InstrNode.CallNode call) {
            super(call.line, call.column);
            this.call = call;
        }

        public InstrNode.CallNode getCall() { return this.call; }

        @Override
        public void generateCode() {
            call.generateCode();
            System.out.println(";");
        }
    }
}
