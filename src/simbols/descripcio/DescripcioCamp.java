package simbols.descripcio;

public class DescripcioCamp extends Descripcio {
    private final String name;
    private final DescripcioTipus tipus;
    private final int despl;

    public DescripcioCamp(String name, DescripcioTipus tipus, int desplaçament) {
        super(TDesc.DCAMP);
        this.name = name;
        this.tipus = tipus;
        this.despl = desplaçament;
    }

    public String getName() { return this.name; }
    public DescripcioTipus getType() { return this.tipus; }
    public int getDesplaçament() { return this.despl; }

    @Override
    public String toString() {
        return "Camp (" + tipus + ", desplaçament=" + despl + ")";
    }
}
