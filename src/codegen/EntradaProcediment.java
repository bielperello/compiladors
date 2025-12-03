package codegen;

public class EntradaProcediment {
    public final int id;
    public final String nom;
    public int nParams;
    public int nLocals;
    public int nTemporals;
    public int ei;
    public int idRet;

    public int ocupVL;
    public int ocupPM;

    public EntradaProcediment(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.nParams = 0;
        this.nLocals = 0;
        this.nTemporals = 0;
        this.ocupVL = 0;
        this.ocupPM = 4;
    }

    public void setEi(int ei) { this.ei = ei; }
    public void incrementParams() { this.nParams++; }
    public void incrementLocals() { this.nLocals++; }
    public void incrementTemporals() { this.nTemporals++; }
    public void setIdRet(int idRet) { this.idRet = idRet; }

    @Override
    public String toString() {
        return String.format("%3d | %-10s | params:%d | locals:%d | temporals:%d | ocupVL:%3d",
                id, nom, nParams, nLocals, nTemporals, ocupVL);
    }
}
