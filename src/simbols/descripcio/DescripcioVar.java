package simbols.descripcio;

import nodes.ExprNode;
import nodes.TypeNode;

import java.util.List;

public class DescripcioVar extends Descripcio {
    private static int nvCounter = 0;
    private final int nv;
    private final DescripcioTipus tipus;
    private List<ExprNode> tupleCamps;
    private boolean isInitialized;

    public DescripcioVar(DescripcioTipus tipus) {
        super(TDesc.DVAR);
        this.tipus = tipus;
        this.nv = ++nvCounter;
    }

    public int getId() { return this.nv; }
    public DescripcioTipus getType() { return this.tipus; }
    public boolean getInitialized() { return this.isInitialized; }

    public void setTupleCamps(List<ExprNode> tupleCamps) { this.tupleCamps = tupleCamps; }
    public void setInitialized(boolean isInitialized) { this.isInitialized = isInitialized; }

    @Override
    public String toString() {
        return "Variable (nv=" + nv + ", tipus=" + tipus + ")";
    }
}
