package simbols.descripcio;

import nodes.TypeNode;

public class DescripcioConst extends Descripcio {
    private final DescripcioTipus tipus;
    private final Object value;

    public DescripcioConst(DescripcioTipus tipus, Object value) {
        super(TDesc.DCONST);
        this.tipus = tipus;
        this.value = value;
    }

    public DescripcioTipus getType() { return this.tipus; }
    public Object getValor() { return this.value; }

    @Override
    public String toString() {
        return "Constant (" + tipus + " = " + value + ")";
    }
}
