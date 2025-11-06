package codegen;

import java.util.*;

public class EtiquetaManager {
    private static int counter = 0;
    private static final Map<String, Integer> map = new HashMap<>();

    public static int novaEtiqueta(String prefix) {
        int id = ++counter;
        map.put(prefix + id, id);
        return id;
    }
}
