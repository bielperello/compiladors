package codegen;

import nodes.Kind;

import java.util.*;

public class CodeGenerator {
    public static final int NUL_VAL = Integer.MIN_VALUE;

    public static int nv = 0;  // comptador variables
    public static int np = 0;  // comptador procediments
    public static int nt = -1; // comptador temporals

    private static Stack<Integer> comptadorTemporals = new Stack<>();

    private static final List<Instruction> code = new ArrayList<>();

    private static final TaulaVariables TV = new TaulaVariables();
    private static final TaulaProcediments TP = new TaulaProcediments();

    private static final Deque<Integer> pproc = new ArrayDeque<>();

    public static final Map<String, String> stringLiterals = new HashMap<>();
    private static int literalCounter = 0;
    private static int labelStringCounter = 0;

    // --- Generació d’instruccions ---
    public static void genera(OpCode op, int arg1, int arg2, int dest) {
        code.add(new Instruction(op, arg1, arg2, dest));
    }

    public static void genera(OpCode op, String literal, int dest) {
        code.add(new InstructionLiteral(op, literal, dest));
    }

    public static void genera(OpCode op, int dest, Kind tsbIO) {
        code.add(new InstructionIO(op, dest, tsbIO));
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
    public static int novavar(String nom, int ocup, int desp, Kind tipus, boolean isParam, int idProc) {
        nv++;
        TV.afegir(new EntradaVariable(nv, nom, ocup, desp, tipus, isParam, idProc));

        if (tipus == Kind.CADENA) {
            String b = "S_BUF_" + labelStringCounter++;
            TV.get(nv).setBufferLabel(b);
        }

        return nv;
    }

    public static int nouproc(String nom) {
        np++;
        TP.afegir(new EntradaProcediment(np, nom));
        return np;
    }

    public static int novaVarTemporal() {
        int comptador = comptadorTemporals.pop() + 1;
        comptadorTemporals.push(comptador);

        TP.get(currentProc()).incrementTemporals();

        return -comptador;
    }

    public static void pushProc(int np) {
        pproc.push(np);
        comptadorTemporals.push(0);
    }

    public static void popProc() {
        pproc.pop();
        comptadorTemporals.pop();
    }

    public static int currentProc() {
        return pproc.isEmpty() ? -1 : pproc.peek();
    }

    public static void registrarEtiquetaProc(int np, int ei) {
        TP.get(np).setEtiqueta(ei);
    }

    public static void registrarRetornProc(int np, int id) {
        TP.get(np).setIdRet(id);
    }

    public static EntradaProcediment getProc(int id) {
        return TP.get(id);
    }
    public static EntradaVariable getVar(int id) { return TV.get(id); }

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

    public static void actualitzarProcediments() {
        // actualitzar atributs dels procediments
        for(int x = 1; x <= nv; x++) {
            EntradaVariable ev = TV.get(x);

            if (!ev.isParam) {
                int p = ev.idProc;
                int ocupx = ev.ocupacio;

                TP.get(p).ocupVL = TP.get(p).ocupVL + ocupx;
                ev.desp = ev.desp*TP.get(p).ocupVL;
            } else {
                int p = ev.idProc;
                int ocupx = ev.ocupacio;

                TP.get(p).ocupPM = TP.get(p).ocupPM + ocupx;
                ev.desp = ev.desp*TP.get(p).ocupPM;
            }
        }
    }

    public static String literalString(Object value) {
        String text = value.toString();

        if (stringLiterals.containsKey(text)) {
            return stringLiterals.get(text);
        }

        String label = "LC_" + (literalCounter++);
        stringLiterals.put(text, label);
        return label;
    }

    public static TaulaVariables getTaulaVariables() { return TV; }
    public static TaulaProcediments getTaulaProcediments() { return TP; }
    public static List<Instruction> getCode() { return code; }

    public static String readableCode() {
        StringBuilder sb = new StringBuilder();

        for(Instruction inst : code) {
            sb.append(inst.toReadableString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
