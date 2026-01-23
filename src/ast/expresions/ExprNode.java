package ast.expresions;

import ast.Kind;
import ast.Node;
import semantic.symbols.descripcio.DescripcioTipus;

import java.util.List;

public class ExprNode extends Node {

    protected Kind kind;
    protected DescripcioTipus tipus;  // Tipus complet associat a l’expressió
    protected ModeExpr mode;          // Mode de l’expressió (var, const, result)
    protected int resultVar;          // E.r

    // Model d'incorporació retroactiva d'etiquetes (backpatching)
    protected List<Integer> trueList;
    protected List<Integer> falseList;

    public enum ModeExpr { MODEVAR, MODECONST, MODERESULT }

    public ExprNode(int line, int column) {
        super(line, column);
    }

    @Override
    public void generateCode() {}

    public Kind getKind() { return kind; }
    public void setKind(Kind k) { this.kind = k; }

    public DescripcioTipus getDescripcioTipus() { return tipus; }
    public void setDescripcioTipus(DescripcioTipus t) { this.tipus = t; }

    public ModeExpr getMode() { return mode; }
    public void setMode(ModeExpr m) { this.mode = m; }

    public int getResultVar() { return resultVar; }
    public void setResultVar(int resultVar) { this.resultVar = resultVar; }

    public List<Integer> getTrueList() { return this.trueList; }
    public void setTrueList(List<Integer> trueList) { this.trueList = trueList; }

    public List<Integer> getFalseList() { return this.falseList; }
    public void setFalseList(List<Integer> falseList) { this.falseList = falseList; }
}
