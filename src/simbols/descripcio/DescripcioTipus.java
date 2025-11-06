package simbols.descripcio;

import nodes.TypeNode;
import java.util.List;

public class DescripcioTipus extends Descripcio {
    private final String nomTipus;
    private final TypeNode.Kind tipusBase;
    private int ocupacio;

    private Integer limitInf;               // Límits per a enters
    private Integer limitSup;
    private DescripcioTipus tipusElemental; // Tipus dels elements (si és un array)
    private List<CampRecord> camps;         // Camps (si és una tupla)

    // Constructor bàsic per a tipus simples (enter, booleà, caracter...)
    public DescripcioTipus(String nomTipus, TypeNode.Kind tipusBase, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = tipusBase;
        this.ocupacio = ocupacio;
    }

    // Constructor per a enters amb límits
    public DescripcioTipus(String nomTipus, int limitInf, int limitSup, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = TypeNode.Kind.DOUBLE;
        this.limitInf = limitInf;
        this.limitSup = limitSup;
        this.ocupacio = ocupacio;
    }

    // Constructor per a arrays
    public DescripcioTipus(String nomTipus, DescripcioTipus tipusElemental, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = TypeNode.Kind.ARRAY;
        this.tipusElemental = tipusElemental;
        this.ocupacio = ocupacio;
    }

    // Constructor per a records
    public DescripcioTipus(String nomTipus, List<CampRecord> camps, int ocupacio) {
        super(TDesc.DTIPUS);
        this.nomTipus = nomTipus;
        this.tipusBase = TypeNode.Kind.TUPLA;
        this.camps = camps;
        this.ocupacio = ocupacio;
    }

    // --- Getters generals ---
    public String getNomTipus() { return nomTipus; }
    public TypeNode.Kind getTipusBase() { return tipusBase; }
    public int getOcupacio() { return ocupacio; }
    public Integer getLimitInf() { return limitInf; }
    public Integer getLimitSup() { return limitSup; }
    public DescripcioTipus getTipusElemental() { return tipusElemental; }
    public List<CampRecord> getCamps() { return camps; }

    public void setCamps(List<CampRecord> c) { this.camps = c; }
    public void setOcupacio(int ocupacio) {this.ocupacio = ocupacio; }

    public static class CampRecord {
        private final String nom;
        private final DescripcioTipus tipus;
        private final int desplacament;

        public CampRecord(String nom, DescripcioTipus tipus, int desplacament) {
            this.nom = nom;
            this.tipus = tipus;
            this.desplacament = desplacament;
        }

        public String getNom() { return nom; }
        public DescripcioTipus getTipus() { return tipus; }
        public int getDesplacament() { return desplacament; }
    }
}
