package ast;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import ast.expresions.ExprNode;
import semantic.symbols.descripcio.DescripcioTipus;

public class DeclNode extends Node {
    private final TypeNode type;
    private final String id;
    private final ExprNode expr;
    private boolean isConst;

    private DescripcioTipus dt;

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

    public void setDescripcioTipus(DescripcioTipus dt) { this.dt = dt; }

    @Override
    public void generateCode() {
        int idProc = CodeGenerator.currentProc(); // procediment actual

        if (!this.isConst && this.type.getKind() != Kind.TUPLA) {
            CodeGenerator.getProc(idProc).incrementLocals(); // incrementar variables locals
            int varId = CodeGenerator.novavar(this.id, this.dt.getOcupacio(),
                    -1, this.dt.getTipusBase(), false, idProc);

            if (expr != null) {
                expr.generateCode();

                if (type.getKind() == Kind.LOGIC && expr.getMode() == ExprNode.ModeExpr.MODERESULT) {
                    // --- Inicialització booleana amb backpatching ---
                    int ec  = EtiquetaManager.novaEtiqueta("E");
                    int ef  = EtiquetaManager.novaEtiqueta("E");
                    int efi = EtiquetaManager.novaEtiqueta("E");

                    CodeGenerator.posaEtiqueta(ec);
                    CodeGenerator.genera(OpCode.COPY, "-1", varId);
                    CodeGenerator.genera(OpCode.GOTO, efi);

                    CodeGenerator.posaEtiqueta(ef);
                    CodeGenerator.genera(OpCode.COPY, "0", varId);

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
