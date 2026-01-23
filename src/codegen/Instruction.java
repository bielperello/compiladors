package codegen;

public class Instruction {
    public final OpCode op;
    public final int arg1;
    public final int arg2;
    public int dest;

    public String literal;

    public Instruction(OpCode op, int arg1, int arg2, int dest) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.dest = dest;

        this.literal = null;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public String getName(int id) {
        if (id < 0) return "t" + (-id);
        return CodeGenerator.getVar(id).nom;
    }

    public String toReadableString() {
        return switch (op) {
            case SKIP -> EtiquetaManager.getEtiqueta(dest) + ": skip";
            case PMB -> "pmb " + CodeGenerator.getProc(dest).nom;
            case RTN -> "rtn " + CodeGenerator.getProc(dest).nom;
            case COPY -> getName(dest) + " = " + (literal != null ? literal : getName(arg1));
            case ADD,CONCAT -> getName(dest) + " = " + getName(arg1) + " + " + getName(arg2);
            case SUB -> getName(dest) + " = " + getName(arg1) + " - " + getName(arg2);
            case PROD -> getName(dest) + " = " + getName(arg1) + " * " + getName(arg2);
            case DIV -> getName(dest) + " = " + getName(arg1) + " / " + getName(arg2);
            case NEG -> getName(dest) + " = - " + getName(arg1);
            case AND -> getName(dest) + " = " + getName(arg1) + " && " + getName(arg2);
            case OR -> getName(dest) + " = " + getName(arg1) + " || " + getName(arg2);
            case NOT -> getName(dest) + " = not " + getName(arg1);
            case IND_VAL -> getName(dest) + " = " + getName(arg1) + "[" + arg2 + "]";
            case IND_ASS -> getName(dest) + "[" + arg2 + "] = " + getName(arg1);
            case IF_EQ -> "if " + getName(arg1) + " = " + getName(arg2) + " goto " + EtiquetaManager.getEtiqueta(dest);
            case IF_NE -> "if " + getName(arg1) + " != " + getName(arg2) + " goto " + EtiquetaManager.getEtiqueta(dest);
            case IF_LE -> "if " + getName(arg1) + " <= " + getName(arg2) + " goto " + EtiquetaManager.getEtiqueta(dest);
            case IF_LT -> "if " + getName(arg1) + " < " + getName(arg2) + " goto " + EtiquetaManager.getEtiqueta(dest);
            case IF_GE -> "if " + getName(arg1) + " >= " + getName(arg2) + " goto " + EtiquetaManager.getEtiqueta(dest);
            case IF_GT -> "if " + getName(arg1) + " > " + getName(arg2) + " goto " + EtiquetaManager.getEtiqueta(dest);
            case GOTO -> "goto " + EtiquetaManager.getEtiqueta(dest);
            case PARAM_S -> "param_s " + getName(dest);
            case PARAM_C -> "param_c " + getName(dest) + "[" + getName(arg1) + "]";
            case CALL -> "call " + CodeGenerator.getProc(dest).nom;
            case WRT -> "wrt " + getName(dest);
            case READ -> "read " + getName(dest);
            case COPY_RTN -> "rtn = " + getName(dest);
            case READ_RTN -> getName(dest) + " = rtn";
        };
    }

    @Override
    public String toString() {
        String a1 = (arg1 == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(arg1);
        String a2 = (arg2 == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(arg2);
        String d = (dest == CodeGenerator.NUL_VAL) ? "-" : String.valueOf(dest);
        return String.format("%-7s %4s %4s %4s", op, a1, a2, d);
    }
}
