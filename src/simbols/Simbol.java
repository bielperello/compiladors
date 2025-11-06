package simbols;

import simbols.descripcio.Descripcio;

public class Simbol {
    private final String name;
    private Descripcio descripcio;
    private int scopeId;

    public Simbol(String name, Descripcio desc, int scopeId) {
        this.name = name;
        this.descripcio = desc;
        this.scopeId = scopeId;
    }

    public String getName() { return this.name; }
    public int getScopeId() { return this.scopeId; }
    public Descripcio getDescripcio() { return this.descripcio; }

    public void setDescripcio(Descripcio d) { this.descripcio = d; }

    @Override
    public String toString() {
        return "Simbol → " + descripcio;
    }
}
