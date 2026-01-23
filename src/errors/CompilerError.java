package errors;

public class CompilerError {
    public enum TYPE { LEXIC, SINTACTIC, SEMANTIC }

    private final int line;
    private final int column;
    private final TYPE type;
    private final String message;

    public CompilerError(int line, int column, TYPE type, String message) {
        this.line = line;
        this.column = column;
        this.type = type;
        this.message = message;
    }

    public CompilerError(TYPE type, String message) {
        this(0, 0, type, message);
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
    public TYPE getType() { return type; }
    public String getMessage() { return message; }

    /** Per consola */
    @Override
    public String toString() {
        if (line > 0) {
            return String.format("[%s] línia %d, col %d: %s", type, line, column, message);
        }
        return String.format("[%s] %s", type, message);
    }

    /** Per fitxer (una fila) */
    public String toFileRow() {
        return String.format("%-9s\t%5d\t%5d\t%s", type, line, column, message);
    }
}
