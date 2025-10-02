package symbols;

import java.util.List;

public class ProgramNode extends Node {
    public String name;
    public List<DeclNode> decls;
    public List<MethodNode> methods;
    public List<InstrNode> instrs;

    public ProgramNode(String name, List<DeclNode> decls, List<MethodNode> methods, List<InstrNode> instrs,
                       int line, int column) {
        super(line, column);
        this.name = name;
        this.decls = decls;
        this.methods = methods;
        this.instrs = instrs;
    }

    @Override
    public void generateCode() {

    }
}
