package codegen;

public class Instruction {
    public final OpCode op;
    public final int arg1;
    public final int arg2;
    public final int dest;

    public Instruction(OpCode op, int arg1, int arg2, int dest) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.dest = dest;
    }

    @Override
    public String toString() {
        String a1 = (arg1 == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(arg1);
        String a2 = (arg2 == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(arg2);
        String d = (dest == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(dest);
        return String.format("%-7s %4s %4s %4s", op, a1, a2, d);
    }
}
