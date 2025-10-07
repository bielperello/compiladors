package symbols;

import java_cup.runtime.Symbol;

public class ArgNode extends Node{
    public ExprNode.VarNode var;
    public TypeNode type;

    public ArgNode(ExprNode.VarNode var, TypeNode type) {
        super(type.line, type.column);
        this.var = var;
        this.type = type;
    }

    @Override
    public void generateCode() {
        System.out.println(type + " " + var.name);
    }
}
