package simbols;

import simbols.descripcio.Descripcio;

public class Simbol {
    private final String name;
    private final int line, column;
    private int scopeId;
    private final Descripcio descripcio;

    public Simbol(String nom, Descripcio desc, int line, int column, int scopeId) {
        this.name = nom;
        this.descripcio = desc;
        this.line = line;
        this.column = column;
        this.scopeId = scopeId;
    }

    public String getName() { return this.name; }
    public int getLine() { return this.line; }
    public int getColumn() { return this.column; }
    public int getScopeId() { return this.scopeId; }
    public Descripcio getDescripcio() { return this.descripcio; }

    @Override
    public String toString() {
        return "Simbol '" + name + "' → " + descripcio;
    }
}
