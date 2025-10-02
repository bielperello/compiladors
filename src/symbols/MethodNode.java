package symbols;

import java.util.List;
public abstract class MethodNode extends Node {
    public boolean isFunction; // mirar si es pot llevar
    public String name;
    public TypeNode returnType;
    public List<ArgNode> args;
    public List<DeclNode> decls;
    public List<InstrNode> instrs;
    public ExprNode returnExpr;

    public MethodNode(boolean isFunction, String name, TypeNode returnType, List<ArgNode> args,
                      List<DeclNode> decls, List<InstrNode> instrs, ExprNode returnExpr, int line, int column) {
        super (line, column);
        this.isFunction = isFunction;
        this.name = name;
        this.returnType = returnType;
        this.args = args;
        this.decls = decls;
        this.instrs = instrs;
        this.returnExpr = returnExpr;
    }

    @Override
    public void generateCode() {
        // exemple: generació de codi
        System.out.println((isFunction ? "funció " : "procediment ") + name + "()");
        for (DeclNode decl : decls) decl.generateCode();
        for (InstrNode instr : instrs) instr.generateCode();
        if (isFunction && returnExpr != null) {
            System.out.println("return EXPRESSIÓ");
        }
    }
}
