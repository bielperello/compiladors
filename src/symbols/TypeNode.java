package symbols;

public class TypeNode extends Node {
    public enum Kind {
        INTEGER, DOUBLE, STRING, CHARACTER, BOOLEAN,
        TUPLE, ARRAY, VOID, UNKNOWN
    }

    private final Kind kind;
    private final String customTypeName; // per tipus definits per l’usuari (com 'Persona')

    public TypeNode(Kind kind, int line, int column) {
        this(kind, null, line, column);
    }

    public TypeNode(Kind kind, String customTypeName, int line, int column) {
        super(line, column);
        this.kind = kind;
        this.customTypeName = customTypeName;
    }

    public Kind getKind() {
        return kind;
    }

    public String getType() { return kind.toString(); }

    public String getCustomTypeName() {
        return customTypeName;
    }

    @Override
    public void generateCode() {
        // No genera codi directe
    }

    @Override
    public String toString() {
        return customTypeName != null ? customTypeName : kind.name().toLowerCase();
    }
}
