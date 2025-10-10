package symbols;

public class TypeNode extends Node {
    public enum Kind { DOUBLE, STRING, BOOLEAN, TUPLE, NULL }

    public Kind kind;
    public java.util.List<ArgNode> tupleFields; // només s’usa si kind == TUPLE

    public TypeNode(Kind kind, int line, int column) {
        super(line, column);
        this.kind = kind;
        this.tupleFields = null;
    }

    public TypeNode(java.util.List<ArgNode> tupleFields, int line, int column) {
        super(line, column);
        this.kind = Kind.TUPLE;
        this.tupleFields = tupleFields;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public void generateCode() {
        // normalment no genera codi directe
    }
}
