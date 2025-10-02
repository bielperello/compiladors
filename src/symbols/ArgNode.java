package symbols;

public class ArgNode extends Node{
    public String name;
    public TypeNode type;

    public ArgNode(String name, TypeNode type, int line, int column) {
        super(line, column);
        this.name = name;
        this.type = type;
    }

    @Override
    public void generateCode() {
        System.out.println(type + " " + name);
    }
}
