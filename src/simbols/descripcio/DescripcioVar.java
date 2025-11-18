package simbols.descripcio;

import nodes.expresions.ExprNode;

import java.util.List;

public class DescripcioVar extends Descripcio {
    public static int nvCounter = 0;
    private final int nv;
    private final DescripcioTipus tipus;
    private List<ExprNode> tupleCamps;
    private boolean isInitialized;

    private boolean isParam;

    public DescripcioVar(DescripcioTipus tipus) {
        super(TDesc.DVAR);
        this.tipus = tipus;
        this.nv = ++nvCounter;
    }

    public int getId() { return this.nv; }
    public DescripcioTipus getType() { return this.tipus; }
    public boolean getInitialized() { return this.isInitialized; }
    public boolean getIsParam() { return this.isParam; }

    public void setTupleCamps(List<ExprNode> tupleCamps) { this.tupleCamps = tupleCamps; }
    public void setInitialized(boolean isInitialized) { this.isInitialized = isInitialized; }
    public void setIsParam(boolean isParam) { this.isParam = isParam; }

    @Override
    public String toString() {
        return "Variable (nv=" + nv + ", tipus=" + tipus + ")";
    }
}
