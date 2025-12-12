package codegen;

public class InstructionLiteral extends Instruction {
    public final String literal;

    public InstructionLiteral(OpCode op, String literal, int dest) {
        super(op, CodeGenerator.NUL_VAL, CodeGenerator.NUL_VAL, dest);
        String[] parts = literal.split("\\.");
        this.literal = parts[0];
    }

    @Override
    public String toReadableString() {
        return switch (op) {
            case COPY -> super.getName(dest) + " = " + literal;
            default -> throw new IllegalStateException("Unexpected value: " + op);
        };
    }
}

