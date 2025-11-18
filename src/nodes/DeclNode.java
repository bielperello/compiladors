package nodes;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.expresions.ExprNode;

public class DeclNode extends Node {
    private final TypeNode type;
    private final String id;
    private final ExprNode expr;
    private boolean isConst;

    public DeclNode(TypeNode type, String id, ExprNode expr, boolean isConst, int line, int column) {
        super (line, column);
        this.type = type;
        this.id = id;
        this.expr = expr;
        this.isConst = isConst;
    }

    public DeclNode(TypeNode type, String id, ExprNode expr, int line, int column) {
        super (line, column);
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    public ExprNode getExpr() { return this.expr; }
    public TypeNode getType() {
        return type;
    }
    public String getId() { return this.id; }

    public boolean isConst() { return this.isConst; }

    @Override
    public void generateCode() {
        if (this instanceof MethodNode) return;

        int idProc = CodeGenerator.currentProc(); // procediment actual

        if (!this.isConst) {
            int varId = CodeGenerator.novavar(this.id, this.type.getLookupName(), false, idProc);

            if (expr != null) {
                expr.generateCode();

                if (type.getKind() == Kind.LOGIC) {
                    // --- Inicialització booleana amb backpatching ---
                    int ec  = EtiquetaManager.novaEtiqueta("E");
                    int ef  = EtiquetaManager.novaEtiqueta("E");
                    int efi = EtiquetaManager.novaEtiqueta("E");

                    CodeGenerator.posaEtiqueta(ec);
                    CodeGenerator.genera(OpCode.COPY, -1, varId);
                    CodeGenerator.genera(OpCode.GOTO, efi);

                    CodeGenerator.posaEtiqueta(ef);
                    CodeGenerator.genera(OpCode.COPY, 0, varId);

                    CodeGenerator.posaEtiqueta(efi);

                    CodeGenerator.backpatch(expr.getTrueList(), ec);
                    CodeGenerator.backpatch(expr.getFalseList(), ef);
                } else {
                    int exprVar = expr.getResultVar();
                    CodeGenerator.genera(OpCode.COPY, exprVar, varId);
                }
            }
        }
    }
}
