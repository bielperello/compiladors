package codegen;

public class EntradaProcediment {
    public final int id;
    public final String nom;
    public int nParams;
    public int nLocals;

    public EntradaProcediment(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.nParams = 0;
        this.nLocals = 0;
    }

    @Override
    public String toString() {
        return String.format("%3d | %-10s | params:%d | locals:%d", id, nom, nParams, nLocals);
    }
}
