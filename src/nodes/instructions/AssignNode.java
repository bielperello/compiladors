package nodes.instructions;

import codegen.CodeGenerator;
import codegen.OpCode;
import nodes.expresions.RefNode;
import nodes.expresions.ExprNode;

public class AssignNode extends InstrNode {
    private final RefNode ref;
    private final ExprNode expr;

    public AssignNode(RefNode ref, ExprNode expr, int line, int column) {
        super(line, column);
        this.ref = ref;
        this.expr = expr;
    }

    public RefNode getRef() { return this.ref; }
    public ExprNode getExpr() { return this.expr; }

    @Override
    public void generateCode() {
        // --- ASSIG → R = E ---

        expr.generateCode(); // generar el codi de l'expressió (E)
        int exprVar = expr.getResultVar();

        ref.generateCode(); // generar el codi de la referència (R)
        int base = ref.getBaseVar(); // base = R.r
        int offset = ref.getOffsetVar(); // offset = R.d

        if(offset != CodeGenerator.NUL_VAL && offset != 0) {
            CodeGenerator.genera(OpCode.IND_ASS, exprVar, offset, base); // R.r[R.d] = E.r
        } else {
            CodeGenerator.genera(OpCode.COPY, exprVar, base); // R.r = E.r
        }
    }
}
