package simbols.descripcio;

import nodes.Kind;
import java.util.List;

public class DescripcioTipus extends Descripcio {
    private final String nomTipus;
    private final Kind tipusBase;
    private int ocupacio;

    private Integer limitInf;               // Límits per a enters
    private Integer limitSup;
    private DescripcioTipus tipusElemental; // Tipus dels elements (si és un array)
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

    // Constructor per a arrays
    public DescripcioTipus(String nomTipus, DescripcioTipus tipusElemental, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = Kind.ARRAY;
        this.tipusElemental = tipusElemental;
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
    public DescripcioTipus getTipusElemental() { return tipusElemental; }
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
}
