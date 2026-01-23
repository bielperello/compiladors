package semantic.symbols.descripcio;

import ast.Kind;
import java.util.List;

public class DescripcioTipus extends Descripcio {
    private String nomTipus;
    private final Kind tipusBase;
    private int ocupacio;

    private Integer limitInf;               // Límits per a enters
    private Integer limitSup;
    private List<DescripcioCamp> camps;         // Camps (si és una tupla)

    // Constructor bàsic per a tipus simples (enter, booleà, caracter...)
    public DescripcioTipus(String nomTipus, Kind tipusBase, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = tipusBase;
        this.ocupacio = ocupacio;
    }

    // Constructor per a enters amb límits
    public DescripcioTipus(String nomTipus, int limitInf, int limitSup, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = Kind.ENTER;
        this.limitInf = limitInf;
        this.limitSup = limitSup;
        this.ocupacio = ocupacio;
    }

    // Constructor per a records
    public DescripcioTipus(String nomTipus, List<DescripcioCamp> camps, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = Kind.TUPLA;
        this.camps = camps;
        this.ocupacio = ocupacio;
    }

    // --- Getters generals ---
    public String getNomTipus() { return nomTipus; }
    public Kind getTipusBase() { return tipusBase; }
    public int getOcupacio() { return ocupacio; }
    public Integer getLimitInf() { return limitInf; }
    public Integer getLimitSup() { return limitSup; }
    public List<DescripcioCamp> getCamps() { return camps; }

    public DescripcioCamp getCamp(String nomCamp) {
        for(DescripcioCamp camp : camps) {
            if (camp.getName().equals(nomCamp)) {
                return camp;
            }
        }

        return null;
    }

    public void setCamps(List<DescripcioCamp> c) { this.camps = c; }
    public void setOcupacio(int ocupacio) {this.ocupacio = ocupacio; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipus{nom=").append(nomTipus)
                .append(", base=").append(tipusBase)
                .append(", ocupacio=").append(ocupacio);

        // Rang d'enter
        if (tipusBase == Kind.ENTER && limitInf != null && limitSup != null) {
            sb.append(", rang=[").append(limitInf).append(", ").append(limitSup).append("]");
        }

        // Camps de tupla
        if (tipusBase == Kind.TUPLA && camps != null) {
            sb.append(", camps=").append(camps.size());
        }

        sb.append("}");
        return sb.toString();
    }

}
