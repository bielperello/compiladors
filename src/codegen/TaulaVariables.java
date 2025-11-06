package codegen;

import java.util.*;

public class TaulaVariables {
    private final List<EntradaVariable> llista = new ArrayList<>();

    public void afegir(EntradaVariable ev) {
        llista.add(ev);
    }

    public EntradaVariable get(int id) {
        return llista.get(id - 1);
    }

    public void print() {
        System.out.println("\n--- Taula de Variables ---");
        llista.forEach(System.out::println);
    }
}
