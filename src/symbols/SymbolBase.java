package symbols;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;

/**
 * Classe que implementa la classe base a partir de la que s'implementen totes
 * les varaibles de la gramàtica.
 *
 *  Conté un valor enter
 *
 * @author Biel Perelló
 */
public class SymbolBase extends ComplexSymbol {
    private static int idAutoIncrement = 0;

    public SymbolBase(String variable, Integer valor) {
        super(variable, ++idAutoIncrement, valor);
    }
}
