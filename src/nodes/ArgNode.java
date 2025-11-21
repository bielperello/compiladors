package nodes;

import simbols.descripcio.DescripcioTipus;

public class ArgNode extends Node{
    private final String id;
    private final TypeNode type;

    private DescripcioTipus dt;

    public ArgNode(String id, TypeNode type) {
        super(type.line, type.column);
        this.id = id;
        this.type = type;
    }

    public String getName() { return this.id; }
    public TypeNode getType() {
        return this.type;
    }
    public DescripcioTipus getDescripcioTipus() { return this.dt; }

    public void setDescripcioTipus(DescripcioTipus dt) { this.dt = dt; }

    @Override
    public void generateCode() {}
}
