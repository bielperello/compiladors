package symbols;

import java.util.Collections;
import java.util.List;

public class ProgramNode extends Node {
    private final String name;
    private final List<DeclNode> decls;
    private final List<MethodNode> methods;
    private final List<InstrNode> instrs;

    public ProgramNode(String name, List<DeclNode> decls, List<MethodNode> methods, List<InstrNode> instrs,
                       int line, int column) {
        super(line, column);
        this.name = name;
        this.decls = decls != null ? decls : Collections.emptyList();
        this.methods = methods != null ? methods : Collections.emptyList();
        this.instrs = instrs != null ? instrs : Collections.emptyList();
    }

    public String getName() { return this.name; }
    public List<DeclNode> getDecls() { return this.decls; }
    public List<MethodNode> getMethods() { return this.methods; }
    public List<InstrNode> getInstrs() { return this.instrs; }

    @Override
    public void generateCode() {

    }
}
