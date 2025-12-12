package nodes.instructions;

import nodes.Kind;
import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.expresions.ExprNode;

public class OutputNode extends InstrNode {
    private final ExprNode expr;

    public OutputNode(ExprNode expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public ExprNode getExpr() { return this.expr; }

    @Override
    public void generateCode() {
        expr.generateCode(); // generar el codi de l'expressió (E)

        if (expr.getKind() == Kind.LOGIC) {
            int t = CodeGenerator.novaVarTemporal();

            int ec  = EtiquetaManager.novaEtiqueta("E");
            int ef  = EtiquetaManager.novaEtiqueta("E");
            int efi = EtiquetaManager.novaEtiqueta("E");

            CodeGenerator.posaEtiqueta(ec);
            CodeGenerator.genera(OpCode.COPY, -1, t);
            CodeGenerator.genera(OpCode.GOTO, efi);

            CodeGenerator.posaEtiqueta(ef);
            CodeGenerator.genera(OpCode.COPY, 0, t);

            CodeGenerator.posaEtiqueta(efi);

            CodeGenerator.backpatch(expr.getTrueList(), ec);
            CodeGenerator.backpatch(expr.getFalseList(), ef);

            CodeGenerator.genera(OpCode.WRT, t);
        } else {
            int exprVar = expr.getResultVar();
            CodeGenerator.genera(OpCode.WRT, exprVar, expr.getKind());
        }
    }
}