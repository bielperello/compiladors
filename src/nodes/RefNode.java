package nodes;

import simbols.descripcio.DescripcioTipus;

public class RefNode extends ExprNode {

    /** Mode de referència segons el tipus de símbol a què apunta. */
    public enum ModeRef {
        CONST,   // constant
        VAR,     // variable
        PROCF,   // subprograma (referència a funció/proc, sense cridar)
        PROCC,    // subprograma cridat completament (amb paràmetres)
        UNKNOWN
    }

    private final String id;             // identificador base
    private final ExprNode indexOpt;     // índex o accés opcional
    private ModeRef modeRef;             // mode de la referència
    private DescripcioTipus tipus;       // descripció de tipus associada
    private TypeNode.Kind tsb;           // tipus subjacent bàsic (redundant però útil)

    public RefNode(String id, ExprNode indexOpt, int line, int column) {
        super(line, column);
        this.id = id;
        this.indexOpt = indexOpt;
        this.modeRef = null;
    }


    public String getId() { return this.id; }
    public ExprNode getIndexOpt() { return this.indexOpt; }
    public ModeRef getModeRef() { return this.modeRef; }
    public DescripcioTipus getDescripcioTipus() { return this.tipus; }
    public TypeNode.Kind getTsb() { return this.tsb; }


    public void setModeRef(ModeRef modeRef) { this.modeRef = modeRef; }
    public void setDescripcioTipus(DescripcioTipus tipus) {
        this.tipus = tipus;
        this.tsb = tipus.getTipusBase();
    }



    @Override
    public void generateCode() {
    }

    @Override
    public String toString() {
        String idx = (indexOpt != null) ? "[" + indexOpt + "]" : "";
        return "Ref(" + id + idx + ", mode=" + modeRef + ", tipus=" +
                (tipus != null ? tipus.getNomTipus() : "??") + ")";
    }
}
