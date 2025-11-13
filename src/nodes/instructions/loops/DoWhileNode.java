package nodes.instructions.loops;

import codegen.*;
import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.List;

public class DoWhileNode extends LoopNode {
    public ExprNode condition;
    public List<InstrNode> body;

    public DoWhileNode(List<InstrNode> body, ExprNode condition, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public ExprNode getCondition() { return this.condition; }
    public List<InstrNode> getBody() { return this.body; }

    @Override
    public void generateCode() {
        int m1 = EtiquetaManager.novaEtiqueta("M"); // etiqueta marcador M1 ( cos del bucle )
        int eFi = EtiquetaManager.novaEtiqueta("E"); // etiqueta final de sentència

        CodeGenerator.posaEtiqueta(m1); // m1: skip

        for(InstrNode instr : body) {
            instr.generateCode(); // genera el codi de cada instrucció dins el cos del bucle
        }

        condition.generateCode(); // genera el codi de l'expressió (E)

        CodeGenerator.backpatch(condition.getTrueList(), m1);
        CodeGenerator.backpatch(condition.getFalseList(), eFi);

        CodeGenerator.posaEtiqueta(eFi); // eFi: skip
    }
}
