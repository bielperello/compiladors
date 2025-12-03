package nodes.expresions;

import codegen.CodeGenerator;
import codegen.OpCode;
import nodes.Kind;
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

        public void generateRef() {
            // --- R0 → R1.id
            base.generateRef(); // generar el codi de la base (R1)

            DescripcioCamp dc = base.getDescripcioTipus().getCamp(this.getId());

            this.setBaseVar(base.getBaseVar()); // R0.r = R1.r
            this.setOffsetVar(base.getOffsetVar() + dc.getDesplaçament()); // R0.d = R1.d + d
        }
    }

    public void generateRef() {
        // --- R → id ---
        if (desc instanceof DescripcioVar dVar) {
            baseVar = dVar.getId(); // R.r = d.nv
            offsetVar = (tipus.getTipusBase().equals(Kind.TUPLA)) ? 0 : CodeGenerator.NUL_VAL; // R.d = 0 / nul_val
        } else if (desc instanceof DescripcioConst dConst) {
            int t = CodeGenerator.novaVarTemporal(); // t = novavar
            CodeGenerator.genera(OpCode.COPY, String.valueOf(dConst.getValor()), t); // t = d.valor
            baseVar = t; // R.r = t
            offsetVar = CodeGenerator.NUL_VAL;
        }
    }

    @Override
    public void generateCode() {
        // --- R → id ---
        this.generateRef();

        // --- E → R
        if (offsetVar == CodeGenerator.NUL_VAL) {
            this.resultVar = baseVar; // E.r = R.r si era simple
        } else { // no generar l'IND_VAL fins que no sigui tupla
            int t = CodeGenerator.novaVarTemporal(); // t = novavar
            CodeGenerator.genera(OpCode.IND_VAL, baseVar, offsetVar, t); // t = R.r[R.d]
            this.resultVar = t; // E.r = t
        }
    }

    @Override
    public String toString() {
        return "Ref(" + id + ", mode=" + modeRef + ", tipus=" +
                (tipus != null ? tipus.getNomTipus() : "??") + ")";
    }
}
