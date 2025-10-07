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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public TYPE getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("Error %s a línia %d, columna %d: %s", type, line, column, message);
    }
}
