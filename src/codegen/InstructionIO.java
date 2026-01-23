package codegen;

import ast.Kind;

public class InstructionIO extends Instruction {
    public Kind tsbIO;

    public InstructionIO(OpCode op,int dest, Kind tsbIO) {
        super(op, CodeGenerator.NUL_VAL, CodeGenerator.NUL_VAL, dest);
        this.tsbIO = tsbIO;
    }
}
