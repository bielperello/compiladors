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

    public void print() {
        System.out.println("\n--- Taula de Procediments ---");
        llista.forEach(System.out::println);
    }
}
