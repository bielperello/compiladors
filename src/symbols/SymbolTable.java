package symbols;

import java.util.*;

public class SymbolTable {
    private Map<String, Simbol> symbols;
    private SymbolTable parent;

    private int scopeId;
    private static int nextId = 0;

    private static List<SymbolTable> allTables = new ArrayList<>();

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
}
