package nodes;

public class ArgNode extends Node{
    public String id;
    public TypeNode type;

    public ArgNode(String id, TypeNode type) {
        super(type.line, type.column);
        this.id = id;
        this.type = type;
    }

    public String getName() { return this.id; }
    public TypeNode getType() {
        return this.type;
    }

    @Override
    public void generateCode() {

    }
}
