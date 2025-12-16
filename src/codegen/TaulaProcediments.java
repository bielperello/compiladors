package codegen;

import java.util.*;

public class TaulaProcediments {
    private final List<EntradaProcediment> llista = new ArrayList<>();

    public void afegir(EntradaProcediment ep) {
        llista.add(ep);
    }

    public EntradaProcediment get(int id) {
        return llista.get(id - 1);
    }

    public EntradaProcediment get(String nomFunc) {
        for(EntradaProcediment ep : llista) {
            if(ep.nom.equals(nomFunc)) {
                return ep;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Taula de Procediments ---\n");

        for (EntradaProcediment ep : llista) {
            sb.append(ep.toSummaryString()).append("\n");
        }

        return sb.toString();
    }

    public String toFullString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Taula de Procediments ---\n");

        for (EntradaProcediment ep : llista) {
            sb.append(ep.toFullString()).append("\n");
        }

        return sb.toString();
    }
}
