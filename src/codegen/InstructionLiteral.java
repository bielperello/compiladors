package codegen;

public class InstructionLiteral extends Instruction {
    public final String literal;

    public InstructionLiteral(OpCode op, String literal, int dest) {
        super(op, CodeGenerator.NUL_VAL, CodeGenerator.NUL_VAL, dest);
        String[] parts = literal.split("\\.");
        this.literal = parts[0];
    }

    public InstructionLiteral(OpCode op, String literal, int arg1 ,int dest) {
        super(op, arg1, CodeGenerator.NUL_VAL, dest);
        String[] parts = literal.split("\\.");
        this.literal = parts[0];
    }

    @Override
    public String toReadableString() {
        if (this.arg1 == CodeGenerator.NUL_VAL) return this.getName(dest) + " = " + this.literal;
        return this.getName(dest) + "[" + arg1 + "] = " + this.literal;

    }

    @Override
    public String toString() {
        String d = (dest == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(dest);
        String a1 = (arg1 == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(arg1);
        return String.format("%-7s %4s %4s %4s", op, literal, a1, d);
    }
}

