package symbols;

import symbols.TypeNode;

public class Simbol {
    public static enum Methods {FUNCIO, PROCEDIMENT};

    private final String name;
    private final TypeNode type;
    private final boolean isConst;
    private boolean isInitialized;
    private final Methods method;
    private final int line;
    private final int column;

    public Simbol(String name, TypeNode type, boolean isConst, int line, int column) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.method = null;
        this.line = line;
        this.column = column;
    }

    public Simbol(String name, TypeNode type, boolean isConst, Methods method, int line, int column) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.method = method;
        this.line = line;
        this.column = column;
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

    public void setInitialized(boolean initialized) {
        this.isInitialized = initialized;
    }

    @Override
    public String toString() {
        return String.format("Symbol{name='%s', type=%s, const=%s, initialized=%s, line=%d, column=%d}",
                name, type, isConst, isInitialized, line, column);
    }
}
