package codegen;

import java.util.*;

public class EtiquetaManager {
    private static int counter = 0;
    private static final Map<Integer, String> map = new HashMap<>();

    public static int novaEtiqueta(String prefix) {
        int id = ++counter;
        String nomEtiqueta;

        if(prefix.charAt(0) == 'E' || prefix.charAt(0) == 'M') nomEtiqueta = "e" + id;
        else nomEtiqueta = prefix;

        map.put(id, nomEtiqueta);
        return id;
    }

    public static String getEtiqueta(int id) { return map.get(id);}
}
