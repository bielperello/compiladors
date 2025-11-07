package simbols;

import nodes.Kind;
import nodes.TypeNode;

public class TipusUtils {
    /**
     * Comprova si dos tipus són compatibles per a una assignació o operació.
     */
    public static boolean sonCompatibles(Kind k1, Kind k2) {
        if (k1 == null || k2 == null) return false;

        if (k1 == k2) return true;

        // compatibilitat numèrica entre enter i double
        return (k1 == Kind.INTEGER && k2 == Kind.DOUBLE) ||
                (k1 == Kind.DOUBLE && k2 == Kind.INTEGER);
    }
}
