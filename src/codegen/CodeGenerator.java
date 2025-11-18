package codegen;

import simbols.descripcio.DescripcioVar;

import java.util.*;

public class CodeGenerator {
    public static final int NUL_VAL = Integer.MIN_VALUE;

    private static int nv = 0;  // comptador variables
    private static int np = 0;  // comptador procediments
    public static int nt = -1; // comptador temporals
    private static final List<Instruction> code = new ArrayList<>();

    private static final TaulaVariables TV = new TaulaVariables();
    private static final TaulaProcediments TP = new TaulaProcediments();

    private static final Deque<Integer> pproc = new ArrayDeque<>();

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
        return nt--;
    }

    public static void pushProc(int np) {
        pproc.push(np);
    }

    public static void popProc() {
        pproc.pop();
    }

    public static int currentProc() {
        return pproc.isEmpty() ? -1 : pproc.peek();
    }

    public static void registrarEtiquetaProc(int np, int ei) {
        TP.get(np).setEi(ei);
    }

    public static void registrarRetornProc(int np, int id) {
        TP.get(np).setIdRet(id);
    }

    public static EntradaProcediment getProc(int id) {
        return TP.get(id);
    }

    public static int getProcId(String func) {
        return TP.get(func).id;
    }

    public static int pc() {
        return code.size() - 1;
    }

    public static void backpatch(List<Integer> llista, int etiqueta) {
        if (llista == null) return;
        for(int pos : llista) {
            Instruction inst = code.get(pos);
            inst.setDest(etiqueta);
        }
    }

    public static List<Integer> concat(List<Integer> l1, List<Integer> l2) {
        List<Integer> list = new ArrayList<>();
        if (l1 != null) list.addAll(l1);
        if (l2 != null) list.addAll(l2);

        return list;
    }

    public static void printTV() { TV.print(); }
    public static void printTP() { TP.print(); }
    public static void printCode() {
        for(Instruction inst : code) {
            System.out.println(inst.toString());
        }
    }
}
