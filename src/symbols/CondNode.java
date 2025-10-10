package symbols;

import java_cup.runtime.Symbol;

import java.util.List;

public abstract class CondNode extends InstrNode {
    public CondNode(int line, int column) {
        super(line, column);
    }

    public static class IfNode extends CondNode {
        public final ExprNode condition;
        public final List<InstrNode> thenInstrs;
        public final List<InstrNode> elseInstrs;

        public IfNode(ExprNode condition, List<InstrNode> thenInstrs, List<InstrNode> elseInstrs,
                      int line, int column) {
            super(line, column);
            this.condition = condition;
            this.thenInstrs = thenInstrs;
            this.elseInstrs = elseInstrs;
        }

        @Override
        public void generateCode(){};
    }

    public static class SwitchNode extends CondNode {
        public final String id;
        public final List<CaseNode> cases;
        public final List<InstrNode> defaultInstrs;

        public SwitchNode(String id, List<CaseNode> cases, List<InstrNode> defaultInstrs,
                          int line, int column) {
            super(line, column);
            this.id = id;
            this.cases = cases;
            this.defaultInstrs = defaultInstrs;
        }

        @Override
        public void generateCode(){}

        public static class CaseNode extends InstrNode {
            public final ExprNode value;
            public final List<InstrNode> instrs;

            public CaseNode(ExprNode value, List<InstrNode> instrs) {
                super(instrs.getFirst().line, instrs.getFirst().column);
                this.value = value;
                this.instrs = instrs;
            }

            public CaseNode(ExprNode value, List<InstrNode> instrs, int line, int column) {
                super(line, column);
                this.value = value;
                this.instrs = instrs;
            }

            @Override
            public void generateCode(){}
        }
    }

}


