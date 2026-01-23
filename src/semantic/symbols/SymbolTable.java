package semantic.symbols;

import java.util.*;

import ast.Kind;
import semantic.symbols.descripcio.DescripcioConst;
import semantic.symbols.descripcio.DescripcioTipus;

public class SymbolTable {
    private final Map<String, Simbol> symbols;
    private final SymbolTable parent;

    private final int scopeId;
    private static int nextId = 0;

    private static final List<SymbolTable> allTables = new ArrayList<>();

    public SymbolTable(SymbolTable parent) {
        this.symbols = new HashMap<>();
        this.parent = parent;
        this.scopeId = nextId++;
    }

    public int getScopeId() { return this.scopeId; }
    public SymbolTable getParent() { return this.parent; }
    public Map<String, Simbol> getSymbols() { return this.symbols; }


    public boolean add(Simbol s) {
        if (symbols.containsKey(s.getName())) return false;
        symbols.put(s.getName(), s);
        return true;
    }

    public Simbol lookUp(String name) {
        Simbol s = symbols.get(name);
        if (s != null) return s;
        if (parent != null) return parent.lookUp(name);
        return null;
    }

    public static void registerClosedScope(SymbolTable table) {
        allTables.add(table);
    }

    public static List<SymbolTable> getAllTables() {
        return allTables;
    }

    public static void reset() {
        allTables.clear();
        nextId = 0;
    }

    public void inicialitzaValorsEstandard() {

        // --- Tipus LOGIC (booleà) ---
        DescripcioTipus boolDesc = new DescripcioTipus(
                "logic", Kind.LOGIC, 4
        );
        this.add(new Simbol("logic", boolDesc, this.getScopeId()));

        // Constants "cert" i "fals"
        DescripcioConst certDesc = new DescripcioConst(boolDesc, -1);  // valor true
        DescripcioConst falsDesc = new DescripcioConst(boolDesc, 0);   // valor false
        this.add(new Simbol("cert", certDesc, this.getScopeId()));
        this.add(new Simbol("fals", falsDesc, this.getScopeId()));

        // --- Tipus ENTER ---
        DescripcioTipus intDesc = new DescripcioTipus(
                "enter", Integer.MIN_VALUE, Integer.MAX_VALUE, 4
        );
        this.add(new Simbol("enter", intDesc, this.getScopeId()));

        // --- Tipus CARACTER ---
        DescripcioTipus charDesc = new DescripcioTipus(
                "caracter", Kind.CARACTER, 4
        );
        this.add(new Simbol("caracter", charDesc, this.getScopeId()));

        // --- Tipus CADENA ---
        DescripcioTipus strDesc = new DescripcioTipus(
                "cadena", Kind.CADENA, 4  // ocupació variable
        );
        this.add(new Simbol("cadena", strDesc, this.getScopeId()));

        // --- Tipus TUPLA ---
        DescripcioTipus tupleDesc = new DescripcioTipus(
                "tupla", Kind.TUPLA, 0
        );
        this.add(new Simbol("tupla", tupleDesc, this.getScopeId()));

        // --- Tipus VOID ---
        DescripcioTipus voidDesc = new DescripcioTipus(
                "void", Kind.VOID, 0
        );
        this.add(new Simbol("void", voidDesc, this.getScopeId()));

        // --- Tipus DESCONEGUT ---
        DescripcioTipus desconegutDesc = new DescripcioTipus(
                "unknown", Kind.UNKNOWN, 0
        );
        this.add(new Simbol("unknown", desconegutDesc, this.getScopeId()));
    }

    public String dumpScope() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("--- Taula de Símbols (scope:%d, parent:%s) ---%n",
                scopeId, (parent == null ? "-" : String.valueOf(parent.scopeId))));

        // Capçalera
        sb.append(String.format(" %3s | %-18s | %-7s | %s%n", "#", "nom", "classe", "info"));
        sb.append(String.format(" %3s-+-%-18s-+-%-7s-+-%s%n", "---", "------------------", "-------", "------------------------------"));

        // Ordenar per nom per estabilitat
        List<String> names = new ArrayList<>(symbols.keySet());
        Collections.sort(names);

        int row = 1;
        for (String name : names) {
            Simbol s = symbols.get(name);

            String classe = (s.getDescripcio() == null)
                    ? "DNUL"
                    : s.getDescripcio().getTipus().name();

            String info = (s.getDescripcio() == null)
                    ? ""
                    : sanitizeOneLine(s.getDescripcio().toString());

            sb.append(String.format(" %3d | %-18s | %-7s | %s%n",
                    row++, name, classe, info));
        }

        sb.append(System.lineSeparator());
        return sb.toString();
    }

    /** Evita salts de línia dins una cel·la. */
    private static String sanitizeOneLine(String s) {
        return s == null ? "" : s.replace("\n", " ").replace("\r", " ").trim();
    }


}
