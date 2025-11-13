package nodes.instructions.conditionals;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.instructions.InstrNode;
import nodes.expresions.ExprNode;

import java.util.List;

public class IfNode extends CondNode {
    private final ExprNode condition;
    private final List<InstrNode> thenInstrs;
    private final List<InstrNode> elseInstrs;

    public IfNode(ExprNode condition, List<InstrNode> thenInstrs, List<InstrNode> elseInstrs,
                  int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenInstrs = thenInstrs;
        this.elseInstrs = elseInstrs;
    }

    public ExprNode getCondition() { return this.condition; }
    public List<InstrNode> getThenInstrs() { return this.thenInstrs; }
    public List<InstrNode> getElseInstrs() { return this.elseInstrs; }

    @Override
    public void generateCode() {
        // --- SI (E) LLAVORS INSTRS_0 SINO INSTRS_1 FSI
        condition.generateCode(); // genera el codi de l'expressió (E)

        int m1 = EtiquetaManager.novaEtiqueta("M"); // etiqueta marcador m1 (bloc then)
        int m2 = EtiquetaManager.novaEtiqueta("M2"); // etiqueta marcador m2 (bloc else)
        int eFi = EtiquetaManager.novaEtiqueta("E"); // etiqueta final de sentència

        CodeGenerator.backpatch(condition.getTrueList(), m1); // Si E = cert -> bloc then
        CodeGenerator.backpatch(condition.getFalseList(), m2); // Si E = fals -> bloc else

        CodeGenerator.posaEtiqueta(m1); // m1: skip
        for(InstrNode instr : thenInstrs) {
            instr.generateCode(); // genera el codi de cada instrucció del bloc then
        }

        CodeGenerator.genera(OpCode.GOTO, eFi); // goto eFi (si ve del bloc then)

        if (!elseInstrs.isEmpty()) {
            CodeGenerator.posaEtiqueta(m2); // m2: skip

            for(InstrNode instr : elseInstrs) {
                instr.generateCode(); // genera el codi de cada instrucció del bloc else
            }
        }

        CodeGenerator.posaEtiqueta(eFi); // eFi: skip
    }
}
