package nodes;

import codegen.CodeGenerator;
import codegen.EtiquetaManager;
import codegen.OpCode;
import nodes.instructions.InstrNode;
import simbols.descripcio.DescripcioVar;

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
        int np = CodeGenerator.nouproc(this.getName()); // crear entrada a la taula de procediments
        CodeGenerator.pushProc(np); // pproc(profunditat) = np

        int eBloc = EtiquetaManager.novaEtiqueta("BE_" + this.name);

        CodeGenerator.registrarEtiquetaProc(np, eBloc); // TP(np).etiqueta = eBloc

        // DECLARACIONS EXECUTABLES (variables)
        for (DeclNode decl : decls) {
            if (!(decl instanceof MethodNode)) decl.generateCode(); // generació de codi de les variables locals
        }

        if (!decls.isEmpty()) CodeGenerator.genera(OpCode.GOTO, eBloc); // goto eBloc

        // DECLARACIONS NO EXECUTABLES (mètodes)
        for(DeclNode decl : decls) {
            if (decl instanceof MethodNode) decl.generateCode();
        }

        CodeGenerator.posaEtiqueta(eBloc); // eBloc: skip

        for(InstrNode instr : instrs) instr.generateCode();

        CodeGenerator.popProc(); // sortir del context del principal
    }
}
