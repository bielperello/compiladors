package nodes.expresions;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.*;
import simbols.descripcio.DescripcioTipus;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends RefNode {
    private List<ExprNode> expr;
    private DescripcioTipus retornTipus;

    public CallNode(String functionName, List<ExprNode> expr, int line, int column) {
        super(functionName, line, column);
        this.expr = expr != null ? expr : new ArrayList<>();
    }

    public String getFunctionName() { return this.getId(); }
    public List<ExprNode> getExpr() { return this.expr; }
    public DescripcioTipus getRetornTipus() { return this.retornTipus; }

    public void setRetornTipus(DescripcioTipus retornTipus) {
        this.retornTipus = retornTipus;
    }

    @Override
    public void generateCode() {
        int np = CodeGenerator.getProcId(this.getId()); // obtenir ID del procediment

        for(int i = expr.size() - 1; i >= 0; i--) { // generar el codi dels paràmetres (en ordre invers)
            ExprNode param = expr.get(i);
            param.generateCode(); // generació de codi del paràmetre

            if (param.getKind() == Kind.LOGIC && param.getMode() == ExprNode.ModeExpr.MODERESULT) {
                int t = CodeGenerator.novaVarTemporal(); // t = novavar

                int ec = EtiquetaManager.novaEtiqueta("E"); // etiqueta per la branca certa
                int ef = EtiquetaManager.novaEtiqueta("E"); // etiqueta per la branca falsa
                int efi = EtiquetaManager.novaEtiqueta("E"); // etiqueta final de sentència

                // branca CERTA
                CodeGenerator.posaEtiqueta(ec); // ec: skip
                CodeGenerator.genera(OpCode.COPY, -1, t); // t = -1
                CodeGenerator.genera(OpCode.GOTO, efi);

                // branca FALSA
                CodeGenerator.posaEtiqueta(ef);
                CodeGenerator.genera(OpCode.COPY, 0, t); // t = 0

                CodeGenerator.posaEtiqueta(efi);

                // backpatch de les dues llistes
                CodeGenerator.backpatch(param.getTrueList(), ec);
                CodeGenerator.backpatch(param.getFalseList(), ef);

                CodeGenerator.genera(OpCode.PARAM_S, t); // param_s t
            } else {
                int paramVar = param.getResultVar(); // paramVar = E.r
                CodeGenerator.genera(OpCode.PARAM_S, paramVar); // param_s paramVar
            }
        }

        CodeGenerator.genera(OpCode.CALL, np); // call np

        if (this.retornTipus.getTipusBase() != Kind.VOID) {
            int result = CodeGenerator.getProc(np).idRet; // agafam l'id del temporal que té el retorn
            int t = CodeGenerator.novaVarTemporal(); // t = novavar
            CodeGenerator.genera(OpCode.COPY, result, t); // copy
            this.resultVar = t;
        }
    }
}
