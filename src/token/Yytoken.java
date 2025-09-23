package token;

/**
 * Classe base per a tokens generats pel lexer
 */
public class Yytoken {
    public final TokenType etiqueta;
    public final int linia, columna;

    public Yytoken(TokenType etiqueta, int linia, int columna) {
        this.etiqueta = etiqueta;
        this.linia = linia;
        this.columna = columna;
    }

    public Yytoken(TokenType etiqueta) {
        this.etiqueta = etiqueta;
        this.linia = Integer.MAX_VALUE;
        this.columna = Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return etiqueta.toString();
    }

    // ───────────────────────────────
    // Enums per operadors
    // ───────────────────────────────

    public enum TipusRelacional {
        LT, // <
        LE, // <=
        GT, // >
        GE, // >=
        EQ, // ==
        NE  // !=
    }

    public enum TipusAritmetic {
        PLUS,    // +
        MINUS,   // -
        TIMES,   // *
        DIVIDE,  // /
        MODULO   // %
    }

    public enum TipusLogic {
        AND,  // i
        OR,   // o
        NOT   // no
    }

    // ───────────────────────────────
    // Subclasses de tokens
    // ───────────────────────────────
    /** Tokens simples amb un lexema (identificadors, paraules clau, etc.) */
    public static class TSimple extends Yytoken {
        public final String lexema;

        public TSimple(TokenType etiqueta, String lexema, int linia, int columna) {
            super(etiqueta, linia, columna);
            this.lexema = lexema;
        }

        @Override
        public String toString() {
            return etiqueta + "(" + lexema + ")";
        }
    }

    /** Nombres enters o reals */
    public static class Nombre extends Yytoken {
        public final double valor;

        public Nombre(double valor, int linia, int columna) {
            super(TokenType.ENTER, linia, columna);
            this.valor = valor;
        }

        @Override
        public String toString() {
            return "Nombre(" + valor + ")";
        }
    }

    /** Valors lògics cert/fals */
    public static class ValorLogic extends Yytoken {
        public final boolean valor;

        public ValorLogic(boolean valor, int linia, int columna) {
            super(TokenType.VALOR_LOGIC, linia, columna);
            this.valor = valor;
        }

        @Override
        public String toString() {
            return "ValorLogic(" + valor + ")";
        }
    }
}
