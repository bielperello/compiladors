package symbols;

import errors.CompilerError;
import errors.ErrorManager;

import java.util.List;

public class Simbol {
    public enum STIPUS { VARIABLE, CONSTANT, FUNCIO, PROCEDIMENT};

    private final String name;
    private final TypeNode.Kind kind;
    private boolean isInitialized;
    private final STIPUS stipus;
    private final int line, column;

    private int scopeId;
    private Object value;

    private List<ExprNode> tupleCamps;
    private List<ArgNode> paramTypes;

    public Simbol(String name, TypeNode kind, STIPUS st, int line, int column, int sID) {
        this.name = name;
        this.kind = kind.getKind();
        this.stipus = st;
        this.line = line;
        this.column = column;
        this.paramTypes = null;
        this.scopeId = sID;
    }

    // Funcions / Procediments
    public Simbol(String name, TypeNode kind, STIPUS t,
                  int line, int column, List<ArgNode> paramTypes, int sID) {
        this.name = name;
        this.kind = kind.getKind();
        this.stipus = t;
        this.line = line;
        this.column = column;
        this.paramTypes = paramTypes;
        this.scopeId = sID;
    }

    public TypeNode.Kind getKind() {
        return kind;
    }
    public STIPUS getStipus() { return stipus; }
    public int getLine() { return line; }
    public int getColumn() {
        return column;
    }
    public List<ArgNode> getParams() {
        return this.paramTypes;
    }
    public List<ExprNode> getTupleCamps() { return this.tupleCamps; }
    public String getName() { return name; }
    public Object getValue() { return value; }

    public void setInitialized(boolean initialized) {
        this.isInitialized = initialized;
    }
    public void setParamTypes(List<ArgNode> paramTypes) {
        this.paramTypes = paramTypes;
    }
    public void setTupleCamps(List<ExprNode> tupleCamps) { this.tupleCamps = tupleCamps; }

    public void setValue(Object value) {
        if (value != null && !isCompatibleType(value)) {
            ErrorManager.add(new CompilerError(line, column, CompilerError.TYPE.SEMANTIC, "" +
                    "Valor incompatible amb el tipus " + this.kind));
        }
    }

    public boolean isCompatibleType(Object value) {
        return switch (kind) {
            case DOUBLE -> value instanceof Double || value instanceof Integer;
            case CHARACTER -> value instanceof Character;
            case BOOLEAN -> value instanceof Boolean;
            case STRING -> value instanceof String;
            case TUPLE -> value instanceof List/* tupla */;
            default -> true;
        };
    }
    public boolean isConst() { return stipus == STIPUS.CONSTANT; }
    public boolean isMethod() { return (stipus == STIPUS.FUNCIO) || (stipus == STIPUS.PROCEDIMENT); }
}
