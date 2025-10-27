package symbols;

import symbols.TypeNode;

import java.util.ArrayList;
import java.util.List;

public class Simbol {
    public enum Methods {FUNCIO, PROCEDIMENT, NONE};

    private final String name;
    // tipus de la variable o del retorn de la funció
    private final TypeNode type;
    private final boolean isConst;
    private boolean isInitialized;
    private final Methods methodType;
    private final int line;
    private final int column;

    private int scopeId;

    private List<ExprNode> tupleCamps;
    private List<ArgNode> paramTypes;

    public Simbol(String name, TypeNode type, boolean isConst, int line, int column, int sID) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.methodType = Methods.NONE;
        this.line = line;
        this.column = column;
        this.paramTypes = null;
        this.scopeId = sID;
    }

    // Funcions / Procediments
    public Simbol(String name, TypeNode type, boolean isConst, Methods method,
                  int line, int column, List<ArgNode> paramTypes, int sID) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.methodType = method;
        this.line = line;
        this.column = column;
        this.paramTypes = paramTypes;
        this.scopeId = sID;
    }

    public String getName() {
        return name;
    }

    public TypeNode getType() {
        return type;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Methods getMethodType() {
        return this.methodType;
    }

    public List<ArgNode> getParams() {
        return this.paramTypes;
    }

    public List<ExprNode> getTupleCamps() { return this.tupleCamps; }

    public void setInitialized(boolean initialized) {
        this.isInitialized = initialized;
    }

    public void setParamTypes(List<ArgNode> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public void setTupleCamps(List<ExprNode> tupleCamps) { this.tupleCamps = tupleCamps; }


    @Override
    public String toString() {
        return String.format("Symbol{name='%s', type=%s, const=%s, initialized=%s, line=%d, column=%d}",
                name, type, isConst, isInitialized, line, column);
    }

    /* MÈTODES TAULA V2 */
    public String getTypeName() { return null; };
}
