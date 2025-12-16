package simbols;

import java.util.*;

import nodes.Kind;
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
}
