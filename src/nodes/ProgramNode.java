package nodes;

import nodes.instructions.InstrNode;

import java.util.Collections;
import java.util.List;

public class ProgramNode extends Node {
    private final String name;
    private final List<DeclNode> decls;
    private final List<InstrNode> instrs;

    public ProgramNode(String name, List<DeclNode> decls, List<InstrNode> instrs,
                       int line, int column) {
        super(line, column);
        this.name = name;
        this.decls = decls != null ? decls : Collections.emptyList();
        this.instrs = instrs != null ? instrs : Collections.emptyList();
    }

    public String getName() { return this.name; }
    public List<DeclNode> getDecls() { return this.decls; }
    public List<InstrNode> getInstrs() { return this.instrs; }

    @Override
    public void generateCode() {

    }
}
