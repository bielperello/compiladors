package nodes;

import codegen.*;
import nodes.expresions.ExprNode;
import nodes.instructions.InstrNode;
import simbols.descripcio.DescripcioTipus;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un subprograma (procediment o funció)
 */
public class MethodNode extends DeclNode {

    private final boolean isFunction;
    private final List<ArgNode> params;
    private final List<DeclNode> decls;
    private final List<InstrNode> instrs;

    public MethodNode(boolean isFunction, String name, TypeNode returnType, List<ArgNode> args,
                      List<DeclNode> decls, List<InstrNode> instrs, ExprNode returnExpr, int line, int column) {
        super(returnType, name, returnExpr, line, column);
        this.isFunction = isFunction;
        this.params = args != null ? args : new ArrayList<>();
        this.decls = decls != null ? decls : new ArrayList<>();
        this.instrs = instrs != null ? instrs : new ArrayList<>();
    }

    // ======================
    // Getters públics
    // ======================

    public boolean isFunction() { return isFunction; }
    public List<ArgNode> getParams() { return params; }
    public List<DeclNode> getDecls() { return decls; }
    public List<InstrNode> getInstrs() { return instrs; }

    // ======================
    // Generació de codi
    // ======================
    @Override
    public void generateCode() {
        int np = CodeGenerator.nouproc(this.getId()); // crear entrada a la taula de procediments
        CodeGenerator.pushProc(np); // pproc(profunditat) = np

        int eInici = EtiquetaManager.novaEtiqueta(this.getId()); // etiqueta per el mètode
        int eBloc = EtiquetaManager.novaEtiqueta("BE_" + this.getId()); // etiqueta per el bloc executable

        CodeGenerator.registrarEtiquetaProc(np, eInici); // TP(np).etiqueta = eBloc

        CodeGenerator.posaEtiqueta(eInici);
        CodeGenerator.genera(OpCode.PMB, np); // pmb np

        for(ArgNode arg : params) {
            DescripcioTipus dt = arg.getDescripcioTipus();
            CodeGenerator.novavar(arg.getName(), dt.getOcupacio(), 1, dt.getTipusBase(), true, np);
            CodeGenerator.getProc(np).incrementParams();
        }

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

        for(InstrNode instr : instrs) {
            instr.generateCode(); // generació de codi de les instruccions del cos
        }

        // --- RETURN ---
        if (this.isFunction) {
            ExprNode retExpr = this.getExpr();
            retExpr.generateCode();
            int retVar = retExpr.getResultVar();

            CodeGenerator.genera(OpCode.COPY_RTN, retVar);

        }

        CodeGenerator.genera(OpCode.RTN, np); // rtn np
        CodeGenerator.popProc(); // sortir del context
    }
}
