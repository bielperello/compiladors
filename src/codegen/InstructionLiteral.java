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
        return this.getName(dest) + " = " + this.literal;
    }

    @Override
    public String toString() {
        String d = (dest == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(dest);
        return String.format("%-7s %6s %4s", op, literal, d);
    }
}

