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
    private Descripcio desc;             // descripció de la referència corresponent
    private ModeRef modeRef;             // mode de la referència
    private DescripcioTipus tipus;       // descripció de tipus associada

    private int baseVar;                 // R.r
    private int offsetVar;               // R.d

    public RefNode(String id, int line, int column) {
        super(line, column);
        this.id = id;
        this.modeRef = null;
    }

    public String getId() { return this.id; }
    public Descripcio getDesc() { return this.desc; }
    public ModeRef getModeRef() { return this.modeRef; }
    public DescripcioTipus getDescripcioTipus() { return this.tipus; }
    public int getBaseVar() { return this.baseVar; }
    public int getOffsetVar() { return this.offsetVar; }

    public void setDesc(Descripcio desc) { this.desc = desc; }
    public void setModeRef(ModeRef modeRef) { this.modeRef = modeRef; }
    public void setDescripcioTipus(DescripcioTipus tipus) { this.tipus = tipus; }
    public void setBaseVar(int baseVar) { this.baseVar = baseVar; }
    public void setOffsetVar(int offsetVar) { this.offsetVar = offsetVar; }


    public static class CampAccessNode extends RefNode {
        private final RefNode base;

        public CampAccessNode(RefNode base, String field, int line, int column) {
            super(field, line, column);
            this.base = base;
        }

        public RefNode getBase() { return base; }

        @Override
        public void generateCode() {
            // --- R0 → R1.id
            base.generateCode(); // generar el codi de la base (R1)

            DescripcioTipus d = (DescripcioTipus) this.getDesc();
            DescripcioCamp dc = d.getCamp(this.getId());

            this.setBaseVar(base.getBaseVar()); // R0.r = R1.r
            this.setOffsetVar(base.getOffsetVar() + dc.getDesplaçament()); // R0.d = R1.d + d
        }
    }

    @Override
    public void generateCode() {
        // --- R → id ---
        if (desc instanceof DescripcioVar dVar) {
            baseVar = dVar.getId();  // R.r = d.nv
        } else if (desc instanceof DescripcioConst dConst) {
            System.out.println(dConst.getValor());
            int t = CodeGenerator.novaVarTemporal();  // t = novavar
            CodeGenerator.genera(OpCode.COPY, String.valueOf(dConst.getValor()), t);  // t = d.valor
            baseVar = t;  // R.r = t
        }
        offsetVar = CodeGenerator.NUL_VAL;  // R.d = nul_val

        // --- E → R
        this.resultVar = baseVar; // E.r = R.r si era simple
    }

    @Override
    public String toString() {
        return "Ref(" + id + ", mode=" + modeRef + ", tipus=" +
                (tipus != null ? tipus.getNomTipus() : "??") + ")";
    }
}
