package nodes;

import java.util.ArrayList;
import java.util.List;

public class TypeNode extends Node {
    private final Kind kind;
    private String customTypeName;
    private List<ArgNode> fields;      // Camps si és TUPLA

    public TypeNode(Kind kind, int line, int column) {
        this(kind, new ArrayList<>(), line, column);
    }

    public TypeNode(Kind kind, List<ArgNode> fields ,int line, int column) {
        super(line, column);
        this.kind = kind;
        this.fields = fields;
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
    public String getLookupName() {
        if (customTypeName != null) return customTypeName;
        return kind.name().toLowerCase();
    }
    public String getCustomTypeName() { return this.customTypeName; }
    public List<ArgNode> getFields() { return this.fields; }

    public void setCustomTypeName(String customTypeName) { this.customTypeName = customTypeName; }

    @Override
    public void generateCode() {}

    @Override
    public String toString() {
        return kind.name().toLowerCase();
    }
}
