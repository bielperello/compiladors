package symbols;

/**
 * @author Biel Perelló
 */
public abstract class Node {
    public int line;
    public int column;

    public Node(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public abstract void generateCode();
}
