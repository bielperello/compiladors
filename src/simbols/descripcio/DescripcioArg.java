package simbols.descripcio;

public class DescripcioArg extends DescripcioVar {
    private final String nom;

    public DescripcioArg(String nom, DescripcioTipus tipus) {
        super(tipus);
        super.setIsParam(true);
        this.nom = nom;
    }

    public String getNom() { return this.nom; }
}
