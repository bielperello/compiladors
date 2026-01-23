package ast.expresions;

import codegen.CodeGenerator;
import codegen.OpCode;
import ast.Kind;

import java.util.List;

public class LiteralNode extends ExprNode {
    private final Object value;

    public LiteralNode(Object value, int line, int column) {
        super(line, column);
        this.value = value;
        inferKind();
        this.mode = ModeExpr.MODECONST;
    }

    private void inferKind() {
        if (value instanceof Integer || value instanceof Double || value instanceof Float)
            this.kind = Kind.ENTER;
        else if (value instanceof Boolean)
            this.kind = Kind.LOGIC;
        else if (value instanceof String)
            this.kind = Kind.CADENA;
        else if (value instanceof Character)
            this.kind = Kind.CARACTER;
        else if (value instanceof List)
            this.kind = Kind.TUPLA;
        else
            this.kind = Kind.UNKNOWN;
    }

    public Object getValue() { return value; }

    @Override
    public void generateCode() {
        if (this.value instanceof Boolean) {
            if (this.mode == ModeExpr.MODERESULT) {
                CodeGenerator.genera(OpCode.GOTO, CodeGenerator.NUL_VAL);
                int pos = CodeGenerator.pc();

                if ((Boolean) value) {
                    this.trueList = List.of(pos);
                    this.falseList = List.of();
                } else {
                    this.falseList = List.of(pos);
                    this.trueList = List.of();
                }
            } else {
                int t = CodeGenerator.novaVarTemporal();
                CodeGenerator.genera(OpCode.COPY, (Boolean) value ? "-1" : "0", t);

                this.resultVar = t;
            }

        } else if (this.value instanceof String) {
            int t = CodeGenerator.novaVarTemporal(); // t = novavar

            String label = CodeGenerator.literalString(this.value);
            CodeGenerator.genera(OpCode.COPY, label, t); // t = label

            this.resultVar = t; // E.r = t
        } else {
            int t = CodeGenerator.novaVarTemporal(); // t = novavar

            String literalText = String.valueOf(value);
            CodeGenerator.genera(OpCode.COPY, literalText, t); // t = lit

            this.resultVar = t; // E.r = t
        }
    }
}
