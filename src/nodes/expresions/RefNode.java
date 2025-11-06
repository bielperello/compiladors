package nodes.expresions;

import codegen.CodeGenerator;
import codegen.OpCode;
import simbols.descripcio.*;

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
    private Descripcio desc;             // descripció de la referència corresponent
    private ModeRef modeRef;             // mode de la referència
    private DescripcioTipus tipus;       // descripció de tipus associada

    private int baseVar;                 // R.r
    private int offsetVar;               // R.d

    public RefNode(String id, int line, int column) {
        super(line, column);
        this.id = id;
        this.indexOpt = null;
        this.modeRef = null;
    }

    public RefNode(String id, ExprNode indexOpt, int line, int column) {
        super(line, column);
        this.id = id;
        this.indexOpt = indexOpt;
        this.modeRef = null;
    }


    public String getId() { return this.id; }
    public ExprNode getIndexOpt() { return this.indexOpt; }
    public Descripcio getDesc() { return this.desc; }
    public ModeRef getModeRef() { return this.modeRef; }
    public DescripcioTipus getDescripcioTipus() { return this.tipus; }

    public void setDesc(Descripcio desc) { this.desc = desc; }
    public void setModeRef(ModeRef modeRef) { this.modeRef = modeRef; }
    public void setDescripcioTipus(DescripcioTipus tipus) { this.tipus = tipus; }


    public static class CampAccessNode extends RefNode {
        private final RefNode base;

        public CampAccessNode(RefNode base, String field, int line, int column) {
            super(field, null, line, column);
            this.base = base;
        }

        public RefNode getBase() { return base; }

        @Override
        public void generateCode() {
            base.generateCode();
        }
    }

    @Override
    public void generateCode() {
        // --- R → id ---
        if (desc instanceof DescripcioVar dVar) {
            baseVar = dVar.getId();  // R.r = d.nv
        } else if (desc instanceof DescripcioConst dConst) {
            int t = CodeGenerator.novaVarTemporal();  // t = novavar
            CodeGenerator.genera(OpCode.COPY, String.valueOf(dConst.getValor()), t);  // t = d.valor
            baseVar = t;  // R.r = t
        }
        offsetVar = CodeGenerator.NUL_VAL;  // R.d = nul_val

        // --- E → R ---
        if (indexOpt != null) { // si R.d ≠ nul_val (té índex)
            indexOpt.generateCode(); // genera índex
            offsetVar = indexOpt.getResultVar(); // R.d = E(i).r

            int t = CodeGenerator.novaVarTemporal();  // t = novavar
            CodeGenerator.genera(OpCode.IND_VAL, baseVar, offsetVar, t); // t = R.r[R.d]
            baseVar = t;
            offsetVar = CodeGenerator.NUL_VAL;
        }

        this.resultVar = baseVar; // E.r = R.r si era simple / E.r = R.r = t = R.r[R.d] si era compost
    }


    @Override
    public String toString() {
        String idx = (indexOpt != null) ? "[" + indexOpt + "]" : "";
        return "Ref(" + id + idx + ", mode=" + modeRef + ", tipus=" +
                (tipus != null ? tipus.getNomTipus() : "??") + ")";
    }
}
