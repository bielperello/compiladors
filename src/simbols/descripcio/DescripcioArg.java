package simbols.descripcio;

public class DescripcioArg extends Descripcio {
    private final DescripcioTipus tipus;
    private final String nom;
    private boolean isInitialized;

    public DescripcioArg(String nom, DescripcioTipus tipus) {
        super(TDesc.DARG);
        this.nom = nom;
        this.tipus = tipus;
    }

    public String getName() { return this.nom; }
    public DescripcioTipus getType() { return this.tipus; }
    public boolean getInitialized() { return this.isInitialized; }

    public void setInitialized(boolean b) { this.isInitialized = b; }

    @Override
    public String toString() {
        return "Argument (" + nom + ": " + tipus + " )";
    }
}
