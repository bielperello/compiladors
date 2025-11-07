package nodes.expresions;

import nodes.Kind;
import nodes.instructions.CallNode;
import nodes.Node;
import simbols.descripcio.DescripcioTipus;

public class ExprNode extends Node {

    protected Kind kind;
    protected DescripcioTipus tipus;  // Tipus complet associat a l’expressió
    protected ModeExpr mode;          // Mode de l’expressió (var, const, result)
    protected int resultVar;          // E.r

    public enum ModeExpr { MODEVAR, MODECONST, MODERESULT }

    public ExprNode(int line, int column) {
        super(line, column);
    }

    @Override
    public void generateCode() {}

    public Kind getKind() { return kind; }
    public void setKind(Kind k) { this.kind = k; }

    public DescripcioTipus getDescripcioTipus() { return tipus; }
    public void setDescripcioTipus(DescripcioTipus t) { this.tipus = t; }

    public ModeExpr getMode() { return mode; }
    public void setMode(ModeExpr m) { this.mode = m; }

    public int getResultVar() { return resultVar; }
    public void setResultVar(int resultVar) { this.resultVar = resultVar; }


    // ------------------------------
    // Subclasses
    // ------------------------------
    /** Expressió formada per una instrucció de crida (per funcions). */
    public static class ExprInstrNode extends ExprNode {
        private final CallNode call;

        public ExprInstrNode(CallNode call) {
            super(call.line, call.column);
            this.call = call;
            this.mode = ModeExpr.MODERESULT;
        }

        public CallNode getCall() { return call; }

        @Override
        public void generateCode() {
            call.generateCode();
        }
    }
}
