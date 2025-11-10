package nodes;

import nodes.expresions.ExprNode;
import nodes.instructions.InstrNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un subprograma (procediment o funció)
 */
public class MethodNode extends DeclNode {

    private final boolean isFunction;
    private final List<ArgNode> params;
    private final List<DeclNode> decls;
    private final List<InstrNode> instrs;

    public MethodNode(boolean isFunction,
                      String name,
                      TypeNode returnType,
                      List<ArgNode> args,
                      List<DeclNode> decls,
                      List<InstrNode> instrs,
                      ExprNode returnExpr,
                      int line,
                      int column) {
        super(returnType, name, returnExpr, line, column);
        this.isFunction = isFunction;
        this.params = args != null ? args : new ArrayList<>();
        this.decls = decls != null ? decls : new ArrayList<>();
        this.instrs = instrs != null ? instrs : new ArrayList<>();
    }

    // ======================
    // Getters públics
    // ======================

    public boolean isFunction() { return isFunction; }
    public List<ArgNode> getParams() { return params; }
    public List<DeclNode> getDecls() { return decls; }
    public List<InstrNode> getInstrs() { return instrs; }

    // ======================
    // Generació de codi
    // ======================

    @Override
    public void generateCode() {}
}
