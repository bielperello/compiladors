package nodes.instructions;

import nodes.expresions.RefNode;

public class InputNode extends InstrNode {
    private final RefNode ref;

    public InputNode(RefNode ref, int line, int column) {
        super(line, column);
        this.ref = ref;
    }

    public RefNode getRef() { return this.ref; }

    @Override
    public void generateCode() {}
}