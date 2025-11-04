package nodes;

import simbols.descripcio.DescripcioTipus;
import java.util.List;

public abstract class ExprNode extends Node {

    protected TypeNode.Kind kind;
    protected DescripcioTipus tipus;  // Tipus complet associat a l’expressió
    protected ModeExpr mode;          // Mode de l’expressió (var, const, result)

    public enum ModeExpr { MODEVAR, MODECONST, MODERESULT }

    public ExprNode(int line, int column) {
        super(line, column);
    }

    public TypeNode.Kind getKind() { return kind; }
    public void setKind(TypeNode.Kind k) { this.kind = k; }

    public DescripcioTipus getDescripcioTipus() { return tipus; }
    public void setDescripcioTipus(DescripcioTipus t) { this.tipus = t; }

    public ModeExpr getMode() { return mode; }
    public void setMode(ModeExpr m) { this.mode = m; }

    // ------------------------------
    // Subclasses
    // ------------------------------

    /** Valor literal o constant. */
    public static class LiteralNode extends ExprNode {
        private final Object value;

        public LiteralNode(Object value, int line, int column) {
            super(line, column);
            this.value = value;
            inferKind();
            this.mode = ModeExpr.MODECONST;
        }

        private void inferKind() {
            if (value instanceof Integer || value instanceof Double || value instanceof Float)
                this.kind = TypeNode.Kind.ENTER;
            else if (value instanceof Boolean)
                this.kind = TypeNode.Kind.LOGIC;
            else if (value instanceof String)
                this.kind = TypeNode.Kind.CADENA;
            else if (value instanceof Character)
                this.kind = TypeNode.Kind.CARACTER;
            else if (value instanceof List)
                this.kind = TypeNode.Kind.TUPLA;
            else
                this.kind = TypeNode.Kind.UNKNOWN;
        }

        public Object getValue() { return value; }

        @Override
        public void generateCode() {
            System.out.print(value);
        }
    }

    /** Tupla literal o expressió composta de múltiples elements. */
    public static class TupleNode extends ExprNode {
        private final List<ExprNode> elements;

        public TupleNode(List<ExprNode> list, int line, int column) {
            super(line, column);
            this.elements = list;
            setKind(TypeNode.Kind.TUPLA);
            setMode(ModeExpr.MODERESULT);
        }

        public List<ExprNode> getElements() { return elements; }

        @Override
        public void generateCode() {
            System.out.print("(");
            for (int i = 0; i < elements.size(); i++) {
                elements.get(i).generateCode();
                if (i < elements.size() - 1) System.out.print(", ");
            }
            System.out.print(")");
        }
    }

    /** Operador binari (+, -, i, o, ==, <, etc.) */
    public static class BinaryOpNode extends ExprNode {
        private final ExprNode left;
        private final ExprNode right;
        private final String operator;

        public BinaryOpNode(ExprNode left, String operator, ExprNode right) {
            super(left.line, left.column);
            this.left = left;
            this.right = right;
            this.operator = operator;
            this.mode = ModeExpr.MODERESULT;
        }

        public ExprNode getLeft() { return left; }
        public ExprNode getRight() { return right; }
        public String getOperator() { return operator; }

        public static TypeNode.Kind inferKind(String op, TypeNode.Kind left, TypeNode.Kind right) {
            if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/"))
                return (left == TypeNode.Kind.ENTER && right == TypeNode.Kind.ENTER)
                        ? TypeNode.Kind.ENTER : TypeNode.Kind.UNKNOWN;

            if (op.equals("i") || op.equals("o"))
                return (left == TypeNode.Kind.LOGIC && right == TypeNode.Kind.LOGIC)
                        ? TypeNode.Kind.LOGIC : TypeNode.Kind.UNKNOWN;

            if (List.of("==", "!=", "<", "<=", ">", ">=").contains(op))
                return TypeNode.Kind.LOGIC;

            return TypeNode.Kind.UNKNOWN;
        }

        @Override
        public void generateCode() {
            left.generateCode();
            System.out.print(" " + operator + " ");
            right.generateCode();
        }
    }

    /** Operador unari (NOT, -, etc.) */
    public static class UnaryOpNode extends ExprNode {
        private final ExprNode expr;
        private final String operator;

        public UnaryOpNode(String operator, ExprNode expr, int line, int column) {
            super(line, column);
            this.expr = expr;
            this.operator = operator;
            this.mode = ModeExpr.MODERESULT;
        }

        public ExprNode getExpr() { return expr; }
        public String getOperator() { return operator; }

        public static TypeNode.Kind inferKind(String op, TypeNode.Kind operand) {
            if (op.equals("NOT"))
                return operand == TypeNode.Kind.LOGIC ? TypeNode.Kind.LOGIC : TypeNode.Kind.UNKNOWN;
            return operand;
        }

        @Override
        public void generateCode() {
            System.out.print(operator + " ");
            expr.generateCode();
        }
    }

    /** Expressió formada per una instrucció de crida (per funcions). */
    public static class ExprInstrNode extends ExprNode {
        private final InstrNode.CallNode call;

        public ExprInstrNode(InstrNode.CallNode call) {
            super(call.line, call.column);
            this.call = call;
            this.mode = ModeExpr.MODERESULT;
        }

        public InstrNode.CallNode getCall() { return call; }

        @Override
        public void generateCode() {
            call.generateCode();
        }
    }
}
