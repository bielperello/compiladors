package nodes.instructions;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.Kind;
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
        ref.generateRef(); // genera el codi de la referència (R)

        int base = ref.getBaseVar();
        int offset = ref.getOffsetVar();

        if (expr.getKind() == Kind.LOGIC && expr.getMode() == ExprNode.ModeExpr.MODERESULT) {
            int ec = EtiquetaManager.novaEtiqueta("E"); // etiqueta per la branca certa
            int ef = EtiquetaManager.novaEtiqueta("E"); // etiqueta per la branca falsa
            int efi = EtiquetaManager.novaEtiqueta("E"); // etiqueta final de sentència

            // branca CERTA
            CodeGenerator.posaEtiqueta(ec);
            if (offset != CodeGenerator.NUL_VAL && offset != 0) {
                CodeGenerator.genera(OpCode.IND_ASS, -1, offset, base); // R.r[R.d] = -1
            } else {
                CodeGenerator.genera(OpCode.COPY, -1, base); // R.r = -1
            }

            CodeGenerator.genera(OpCode.GOTO, efi); // goto efi

            // branca FALSA
            CodeGenerator.posaEtiqueta(ef); // ef: skip
            if (offset != CodeGenerator.NUL_VAL && offset != 0) {
                CodeGenerator.genera(OpCode.IND_ASS, 0, offset, base); // R.r[R.d] = 0
            } else {
                CodeGenerator.genera(OpCode.COPY, 0, base); // R.r = 0
            }

            CodeGenerator.posaEtiqueta(efi); // efi: skip

            // backpatch de les branques
            CodeGenerator.backpatch(expr.getTrueList(), ec);
            CodeGenerator.backpatch(expr.getFalseList(), ef);
        } else {
            int exprVar = expr.getResultVar();

            if(offset != CodeGenerator.NUL_VAL) {
                CodeGenerator.genera(OpCode.IND_ASS, exprVar, offset, base); // R.r[R.d] = E.r
            } else {
                CodeGenerator.genera(OpCode.COPY, exprVar, base); // R.r = E.r
            }
        }
    }
}
