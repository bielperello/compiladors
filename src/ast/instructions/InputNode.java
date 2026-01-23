package ast.instructions;

import codegen.CodeGenerator;
import codegen.OpCode;
import ast.expresions.RefNode;

public class InputNode extends InstrNode {
    private final RefNode ref;

    public InputNode(RefNode ref, int line, int column) {
        super(line, column);
        this.ref = ref;
    }

    public RefNode getRef() { return this.ref; }

    @Override
    public void generateCode() {
        ref.generateCode(); // generar el codi de la referència (R)

        int base = ref.getBaseVar();
        int offset = ref.getOffsetVar();

        int t = CodeGenerator.novaVarTemporal(); // t = novavar
        CodeGenerator.genera(OpCode.READ, t, ref.getKind());

        if (offset != CodeGenerator.NUL_VAL) {
            CodeGenerator.genera(OpCode.IND_ASS, t, offset, base);
        } else {
            CodeGenerator.genera(OpCode.COPY, t, base);
        }
    }
}