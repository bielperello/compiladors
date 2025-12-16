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

    public List<EntradaVariable> getList() { return this.llista; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Taula de Variables ---\n");

        for (EntradaVariable ev : llista) {
            sb.append(ev.toSummaryString()).append("\n");
        }

        return sb.toString();
    }

    public String toFullString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Taula de Variables ---\n");

        for (EntradaVariable ev : llista) {
            sb.append(ev.toFullString()).append("\n");
        }

        return sb.toString();
    }
}
