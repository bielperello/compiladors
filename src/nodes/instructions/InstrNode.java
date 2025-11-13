package nodes.instructions;

import nodes.DeclNode;
import nodes.Node;
import nodes.expresions.ExprNode;
import nodes.expresions.RefNode;

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

    public static class InstrRefNode extends InstrNode {
        private RefNode ref;

        public InstrRefNode(RefNode ref) {
            super(ref.line, ref.column);
            this.ref = ref;
        }

        public RefNode getRef() { return this.ref; }

        @Override
        public void generateCode() {
            ref.generateCode();
        }
    }
}
