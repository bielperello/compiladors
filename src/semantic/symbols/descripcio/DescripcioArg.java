package semantic.symbols.descripcio;

public class DescripcioArg extends DescripcioVar {
    private final String nom;

    public DescripcioArg(String nom, DescripcioTipus tipus) {
        super(tipus);
        super.setIsParam(true);
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Argument{nom=" + nom +
                ", tipus=" + getType().getNomTipus() +
                "}";
    }

}
