package codegen;

public class InstructionLiteral extends Instruction {
    private final String literal;

    public InstructionLiteral(OpCode op, String literal, int dest) {
        super(op, CodeGenerator.NUL_VAL, CodeGenerator.NUL_VAL, dest);
        this.literal = literal;
    }

    @Override
    public String toString() {
        return String.format("%-7s %-6s -> %4s", op, literal, dest);
    }
}

