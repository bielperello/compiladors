package simbols;

import java.util.*;
import simbols.descripcio.*;
import nodes.TypeNode;

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

    public void updateSymbol(String id, Descripcio novaDesc) {
        Simbol s = this.lookUp(id);
        if (s != null) s.setDescripcio(novaDesc);
    }


    public void inicialitzaValorsEstandard() {

        // --- Tipus LOGIC (booleà) ---
        DescripcioTipus boolDesc = new DescripcioTipus(
                "logic", TypeNode.Kind.LOGIC, 1
        );
        this.add(new Simbol("logic", boolDesc, 0, 0, this.getScopeId()));

        // Constants "cert" i "fals"
        DescripcioConst certDesc = new DescripcioConst(boolDesc, -1);  // valor true
        DescripcioConst falsDesc = new DescripcioConst(boolDesc, 0);   // valor false
        this.add(new Simbol("cert", certDesc, 0, 0, this.getScopeId()));
        this.add(new Simbol("fals", falsDesc, 0, 0, this.getScopeId()));

        // --- Tipus ENTER ---
        DescripcioTipus intDesc = new DescripcioTipus(
                "enter", Integer.MIN_VALUE, Integer.MAX_VALUE, 4
        );
        this.add(new Simbol("enter", intDesc, 0, 0, this.getScopeId()));

        // --- Tipus CARACTER ---
        DescripcioTipus charDesc = new DescripcioTipus(
                "caracter", 0, 255, 1
        );
        this.add(new Simbol("caracter", charDesc, 0, 0, this.getScopeId()));

        // --- Tipus CADENA ---
        DescripcioTipus strDesc = new DescripcioTipus(
                "cadena", TypeNode.Kind.CADENA, 0  // ocupació variable
        );
        this.add(new Simbol("cadena", strDesc, 0, 0, this.getScopeId()));

        // --- Tipus TUPLA ---
        DescripcioTipus tupleDesc = new DescripcioTipus(
                "tupla", TypeNode.Kind.TUPLA, 0
        );
        this.add(new Simbol("tupla", tupleDesc, 0, 0, this.getScopeId()));

        // --- Tipus VOID ---
        DescripcioTipus voidDesc = new DescripcioTipus(
                "void", TypeNode.Kind.VOID, 0
        );
        this.add(new Simbol("void", voidDesc, 0, 0, this.getScopeId()));

        // --- Tipus DESCONEGUT ---
        DescripcioTipus desconegutDesc = new DescripcioTipus(
                "unknown", TypeNode.Kind.UNKNOWN, 0
        );
        this.add(new Simbol("unknown", desconegutDesc, 0, 0, this.getScopeId()));
    }
}
