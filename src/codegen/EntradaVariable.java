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

    public String toSummaryString() {
        return String.format(
                "%3d | %-10s | %-6s | desp:%4d | proc:%d", id, nom, tsb.toString().toLowerCase(), desp, idProc
        );
    }

    public String toFullString() {
        return String.format(
                "%3d | %-10s | ocup:%2d | desp:%4d | %-6s | param:%5s | proc:%d | buf:%s",
                id, nom, ocupacio, desp, tsb.toString().toLowerCase(), isParam, idProc,
                bufferLabel != null ? bufferLabel : "-"
        );
    }

    @Override
    public String toString() {
        return toSummaryString();
    }
}
