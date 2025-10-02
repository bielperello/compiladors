package symbols;

import java.util.List;
import java.util.ArrayList;

public class SymbolINSTRS extends SymbolBase {
    private List<SymbolINSTR> instrs;

    public SymbolINSTRS() {
        super("INSTRS", 0);
        this.instrs = new ArrayList<>();
    }

    public SymbolINSTRS(List<SymbolINSTR> instrs) {
        super("INSTRS", 0);
        this.instrs = instrs;
    }

    public void addInstr(SymbolINSTR instr) {
        this.instrs.add(instr);
    }

    public List<SymbolINSTR> getInstrs() {
        return instrs;
    }
}

