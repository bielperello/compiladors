package codegen;

import assembler.AssemblerGenerator;

public class EntradaProcediment {
    public final int id;
    public final String nom;
    public int nParams;
    public int nLocals;
    public int nTemporals;
    public int etiqueta;
    public int idRet;
    public int primerTemp;

    public int ocupVL;
    public int ocupPM;

    public EntradaProcediment(int id, String nom, int primerTemp) {
        this.id = id;
        this.nom = nom;
        this.primerTemp = primerTemp;
        this.nParams = 0;
        this.nLocals = 0;
        this.nTemporals = 0;
        this.ocupVL = 0;
        this.ocupPM = AssemblerGenerator.DESP_PARAMS;
    }

    public void setEtiqueta(int etiqueta) { this.etiqueta = etiqueta; }
    public void incrementParams() { this.nParams++; }
    public void incrementLocals() { this.nLocals++; }
    public void incrementTemporals() { this.nTemporals++; }
    public void setIdRet(int idRet) { this.idRet = idRet; }

    public String toSummaryString() {
        return String.format("%3d | %-10s | params:%2d | locals:%2d | temps:%2d | ocupVL:%3d",
                id, nom, nParams, nLocals, nTemporals, ocupVL
        );
    }

    public String toFullString() {
        return String.format(
                "%3d | %-10s | params:%2d | locals:%2d | temps:%2d | label:%3d | idRet:%3d | ocupVL:%3d | ocupPM:%3d",
                id, nom, nParams, nLocals, nTemporals, etiqueta, idRet, ocupVL, ocupPM
        );
    }

    @Override
    public String toString() {
        return toSummaryString();
    }
}
