package ast.instructions.conditionals;

import codegen.*;
import ast.instructions.InstrNode;
import ast.expresions.ExprNode;

import java.util.ArrayList;
import java.util.List;

public class CaseNode extends InstrNode {
    private final ExprNode value;
    private final List<InstrNode> instrs;

    public CaseNode(ExprNode value, List<InstrNode> instrs, int line, int column) {
        super(line, column);
        this.value = value;
        this.instrs = instrs != null ? instrs : new ArrayList<>();
    }

    public ExprNode getValue() { return this.value; }
    public List<InstrNode> getInstrs() { return this.instrs; }

    public void generateCaseCode(int exprVar, int efi) {
        this.generateCode(); // value.generateCode()
        int casVar = value.getResultVar(); // casVar = E1.r
        int eSeg = EtiquetaManager.novaEtiqueta("E"); // etiqueta pel següent cas

        CodeGenerator.genera(OpCode.IF_NE, exprVar, casVar, eSeg); // if E0.r != E1.r goto eSeg

        for(InstrNode instrNode : this.instrs) {
            instrNode.generateCode(); // genera el codi de cada instrucció del cas
        }

        CodeGenerator.genera(OpCode.GOTO, efi); // goto efi
        CodeGenerator.posaEtiqueta(eSeg); // e_seg: skip
    }


    @Override
    public void generateCode(){
        value.generateCode(); // genera el codi de l'expressió
    }
}