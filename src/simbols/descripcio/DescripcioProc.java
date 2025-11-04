package simbols.descripcio;

import nodes.TypeNode;
import nodes.ArgNode;
import java.util.*;

public class DescripcioProc extends Descripcio {
    private static int npCounter = 0;
    private final int np;
    private final DescripcioTipus tipus;
    private final boolean esFuncio;
    private final List<ArgNode> args;

    public DescripcioProc(DescripcioTipus tipusRetorn, boolean esFuncio, List<ArgNode> args) {
        super(TDesc.DPROC);
        this.np = ++npCounter;
        this.tipus = tipusRetorn;
        this.esFuncio = esFuncio;
        this.args = args;
    }

    public int getId() { return this.np; }
    public boolean esFuncio() { return this.esFuncio; }
    public DescripcioTipus getType() { return this.tipus; }
    public List<ArgNode> getArgs() { return this.args; }

    @Override
    public String toString() {
        return (esFuncio ? "Funció" : "Procediment") +
                " (np=" + np + ", retorn=" + tipus + ", args=" + args.size() + ")";
    }

}
