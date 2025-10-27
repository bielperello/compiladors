package symbols;

import java.util.Collections;
import java.util.List;

/**
 * Representa un subprograma (procediment o funció)
 */
public class MethodNode extends Node {

    private final boolean isFunction;
    private final String name;
    private final TypeNode returnType; // null si és procediment
    private final List<ArgNode> params;
    private final List<InstrNode> instrs;
    private final ExprNode returnExpr; // només si és funció

    public MethodNode(boolean isFunction,
                      String name,
                      TypeNode returnType,
                      List<ArgNode> args,
                      List<InstrNode> instrs,
                      ExprNode returnExpr,
                      int line,
                      int column) {
        super(line, column);
        this.isFunction = isFunction;
        this.name = name;
        this.returnType = returnType;
        this.params = args != null ? args : Collections.emptyList();
        this.instrs = instrs != null ? instrs : Collections.emptyList();
        this.returnExpr = returnExpr;
    }

    // ======================
    // Getters públics
    // ======================

    public boolean isFunction() { return isFunction; }
    public String getName() { return name; }
    public TypeNode getReturnType() { return returnType; }
    public List<ArgNode> getParams() { return params; }
    public List<InstrNode> getInstrs() { return instrs; }
    public ExprNode getReturnExpr() { return returnExpr; }

    // ======================
    // Generació de codi
    // ======================

    @Override
    public void generateCode() {
        System.out.println((isFunction ? "funció " : "procediment ") + name + "()");
        for (InstrNode instr : instrs) instr.generateCode();
        if (isFunction && returnExpr != null)
            System.out.println("return [EXPRESSIÓ]");
    }
}
