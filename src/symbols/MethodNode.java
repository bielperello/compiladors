package symbols;

import java_cup.runtime.Symbol;

import java.util.List;

public class MethodNode extends Node {
    public boolean isFunction;
    public String name;
    public TypeNode returnType;
    public List<ArgNode> args;
    public List<DeclNode> decls;
    public List<InstrNode> instrs;
    public ExprNode returnExpr;
    public SymbolTable symbolTable;

    public MethodNode(boolean isFunction, String name, TypeNode returnType, List<ArgNode> args,
                      List<DeclNode> decls, List<InstrNode> instrs, ExprNode returnExpr, int line, int column, SymbolTable parentScope) {
        super (line, column);
        this.isFunction = isFunction;
        this.name = name;
        this.returnType = returnType;
        this.args = args;
        this.decls = decls;
        this.instrs = instrs;
        this.returnExpr = returnExpr;
        this.symbolTable = new SymbolTable(parentScope);
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
