package codegen;

import java.util.*;

public class CodeGenerator {
    public static final int NUL_VAL = -1;

    private static int nv = 0;  // comptador variables
    private static int np = 0;  // comptador procediments
    private static final List<Instruction> code = new ArrayList<>();

    private static final TaulaVariables TV = new TaulaVariables();
    private static final TaulaProcediments TP = new TaulaProcediments();

    // --- Generació d’instruccions ---
    public static void genera(OpCode op, int arg1, int arg2, int dest) {
        code.add(new Instruction(op, arg1, arg2, dest));
    }

    public static void genera(OpCode op, String literal, int dest) {
        code.add(new InstructionLiteral(op, literal, dest));
    }

    public static void genera(OpCode op, int arg1, int dest) {
        genera(op, arg1, NUL_VAL, dest);
    }

    public static void genera(OpCode op, int dest) {
        genera(op, NUL_VAL, NUL_VAL, dest);
    }

    public static void posaEtiqueta(int id) {
        genera(OpCode.SKIP, NUL_VAL, NUL_VAL, id);
    }

    // --- Gestió de variables i procediments ---
    public static int novavar(String nom, String tipus, boolean esParam, int idProc) {
        nv++;
        TV.afegir(new EntradaVariable(nv, nom, tipus, esParam, idProc));
        return nv;
    }

    public static int nouproc(String nom) {
        np++;
        TP.afegir(new EntradaProcediment(np, nom));
        return np;
    }

    public static int novaVarTemporal() {
        return novavar("t" + nv, "temp", false, -1);
    }

    public static void printCode() {
        System.out.println("\n--- Codi Intermedi ---");
        for (int i = 0; i < code.size(); i++)
            System.out.printf("%3d: %s\n", i + 1, code.get(i));
    }

    public static void printTV() { TV.print(); }
    public static void printTP() { TP.print(); }
}
