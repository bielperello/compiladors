package nodes.instructions.loops;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.List;

public class WhileNode extends LoopNode {
    private ExprNode condition;
    private List<InstrNode> body;

    public WhileNode(ExprNode condition, List<InstrNode> body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public ExprNode getCondition() { return this.condition; }
    public List<InstrNode> getBody() { return this.body; }

    @Override
    public void generateCode() {
        int m1 = EtiquetaManager.novaEtiqueta("M"); // etiqueta marcador M1 ( consulta expressió )
        int m2 = EtiquetaManager.novaEtiqueta("M"); // etiqueta marcador M2 ( cos del bucle )
        int eFi = EtiquetaManager.novaEtiqueta("E"); // etiqueta final de sentència

        CodeGenerator.posaEtiqueta(m1); // m1: skip

        condition.generateCode(); // genera el codi de l'expressió (E)

        CodeGenerator.backpatch(condition.getTrueList(), m2);
        CodeGenerator.backpatch(condition.getFalseList(), eFi);

        CodeGenerator.posaEtiqueta(m2); // m2: skip

        for(InstrNode instr : body) {
            instr.generateCode(); // genera el codi de cada instrucció dins el cos del bucle
        }

        CodeGenerator.genera(OpCode.GOTO, m1); // goto m1

        CodeGenerator.posaEtiqueta(eFi); // eFi: skip
    }
}
