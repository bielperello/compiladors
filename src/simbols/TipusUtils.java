package simbols;

import nodes.TypeNode;
import simbols.descripcio.DescripcioTipus;

public class TipusUtils {

    /**
     * Comprova si dos tipus són compatibles per a una assignació o operació.
     */
    public static boolean sonCompatibles(DescripcioTipus t1, DescripcioTipus t2) {
        if (t1 == null || t2 == null) return false;

        TypeNode.Kind k1 = t1.getTipusBase();
        TypeNode.Kind k2 = t2.getTipusBase();

        if (k1 == k2) return true;

        // compatibilitat numèrica entre enter i double
        return (k1 == TypeNode.Kind.INTEGER && k2 == TypeNode.Kind.DOUBLE) ||
                (k1 == TypeNode.Kind.DOUBLE && k2 == TypeNode.Kind.INTEGER);
    }
}
