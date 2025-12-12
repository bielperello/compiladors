package codegen;

import nodes.Kind;

public class EntradaVariable {
    public final int id;
    public final String nom;
    public final int ocupacio;
    public int desp;
    public final Kind tsb;
    public final boolean isParam;
    public final int idProc;
    public String bufferLabel;

    public EntradaVariable(int id, String nom, int ocup, int desp, Kind tsb, boolean isParam,int idProc) {
        this.id = id;
        this.nom = nom;
        this.idProc = idProc;
        this.ocupacio = ocup;
        this.desp = desp;
        this.isParam = isParam;
        this.tsb = tsb;
    }

    public void setBufferLabel(String bufferLabel) { this.bufferLabel = bufferLabel; }

    @Override
    public String toString() {
        return String.format("%3d | %-10s | %d | desp:%4d | %-6s | proc:%d",
                id, nom, ocupacio, desp, tsb.toString().toLowerCase(), idProc);
    }
}
