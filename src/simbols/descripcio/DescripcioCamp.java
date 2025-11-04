package simbols.descripcio;

import nodes.TypeNode;

public class DescripcioCamp extends Descripcio {
    private final DescripcioTipus tipus;
    private final int despl;

    public DescripcioCamp(DescripcioTipus tipus, int desplaçament) {
        super(TDesc.DCAMP);
        this.tipus = tipus;
        this.despl = desplaçament;
    }

    public DescripcioTipus getType() { return this.tipus; }
    public int getDesplaçament() { return this.despl; }

    @Override
    public String toString() {
        return "Camp (" + tipus + ", desplaçament=" + despl + ")";
    }
}
