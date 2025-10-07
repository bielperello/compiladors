package symbols;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Simbol> symbols = new HashMap<>();
    private SymbolTable parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public boolean add(Simbol symbol) {
        if(symbols.containsKey(symbol.getName())) {
            return false;
        }

        symbols.put(symbol.getName(), symbol);

        System.out.println("Symbol " + symbol.getName() + " added");

        return true;
    }

    public Simbol lookUp(String name) {
        Simbol s = symbols.get(name);
        if (s != null) return s;
        if (parent != null) return parent.lookUp(name);

        return null;
    }

    public Simbol lookUpLocal(String name) {
        return symbols.get(name);
    }

    public SymbolTable getParent() {
        return parent;
    }

    public Collection<Simbol> getSymbols() {
        return symbols.values();
    }
}
