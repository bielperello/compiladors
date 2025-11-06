package codegen;

public class EntradaVariable {
    public final int id;
    public final String nom;
    public final String tipus;
    public final boolean esParam;
    public final int idProc;

    public EntradaVariable(int id, String nom, String tipus, boolean esParam, int idProc) {
        this.id = id;
        this.nom = nom;
        this.tipus = tipus;
        this.esParam = esParam;
        this.idProc = idProc;
    }

    @Override
    public String toString() {
        return String.format("%3d | %-10s | %-6s | param:%-5s | proc:%d",
                id, nom, tipus, esParam, idProc);
    }
}
