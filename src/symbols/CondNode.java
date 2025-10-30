package symbols;

import java_cup.runtime.Symbol;

import java.util.ArrayList;
import java.util.List;

public abstract class CondNode extends InstrNode {
    public CondNode(int line, int column) {
        super(line, column);
    }

    public static class IfNode extends CondNode {
        private final ExprNode condition;
        private final List<InstrNode> thenInstrs;
        private final List<InstrNode> elseInstrs;

        public IfNode(ExprNode condition, List<InstrNode> thenInstrs, List<InstrNode> elseInstrs,
                      int line, int column) {
            super(line, column);
            this.condition = condition;
            this.thenInstrs = thenInstrs;
            this.elseInstrs = elseInstrs;
        }

        public ExprNode getCondition() { return this.condition; }
        public List<InstrNode> getThenInstrs() { return this.thenInstrs; }
        public List<InstrNode> getElseInstrs() { return this.elseInstrs; }

        @Override
        public void generateCode(){};
    }

    public static class SwitchNode extends CondNode {
        private final String id;
        private final List<CaseNode> cases;
        private final List<InstrNode> defaultInstrs;

        public SwitchNode(String id, List<CaseNode> cases, List<InstrNode> defaultInstrs,
                          int line, int column) {
            super(line, column);
            this.id = id;
            this.cases = cases != null ? cases : new ArrayList<>();
            this.defaultInstrs = defaultInstrs;
        }

        public String getId() { return this.id; }
        public List<CaseNode> getCases() { return this.cases; }
        public List<InstrNode> getDefaultInstrs() { return this.defaultInstrs; }

        @Override
        public void generateCode(){}

        public static class CaseNode extends InstrNode {
            private final ExprNode value;
            private final List<InstrNode> instrs;

            public CaseNode(ExprNode value, List<InstrNode> instrs, int line, int column) {
                super(line, column);
                this.value = value;
                this.instrs = instrs != null ? instrs : new ArrayList<>();
            }

            public ExprNode getValue() { return this.value; }
            public List<InstrNode> getInstrs() { return this.instrs; }

            @Override
            public void generateCode(){}
        }
    }

}


