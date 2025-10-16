package symbols;

public class TypeNode extends Node {
    public enum Kind { DOUBLE, STRING, CHARACTER, BOOLEAN, TUPLE, NULL }

    public Kind kind;

    public TypeNode(Kind kind, int line, int column) {
        super(line, column);
        this.kind = kind;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public void generateCode() {
        // normalment no genera codi directe
    }
}
