package nodes.instructions;

import nodes.DeclNode;
import nodes.Node;

public class InstrNode extends Node {
    public InstrNode(int line, int column) {
        super(line, column);
    }

    @Override
    public void generateCode() {}

    public static class InstrDeclNode extends InstrNode {
        private DeclNode decl;

        public InstrDeclNode(DeclNode decl) {
            super(decl.line, decl.column);
            this.decl = decl;
        }

        public DeclNode getDecl() {return this.decl; }
        @Override
        public void generateCode() {
            decl.generateCode();
        }
    }
}
