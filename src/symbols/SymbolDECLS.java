package symbols;

import java.util.List;
import java.util.ArrayList;

public class SymbolDECLS extends SymbolBase {
    private List<SymbolDECL> decls;

    // Constructor buit (llista buida)
    public SymbolDECLS() {
        super("DECLS", 0); // Nom del símbol i valor dummy
        this.decls = new ArrayList<>();
    }

    // Constructor amb llista existent
    public SymbolDECLS(List<SymbolDECL> decls) {
        super("DECLS", 0);
        this.decls = decls;
    }

    // Afegir una declaració
    public void addDecl(SymbolDECL decl) {
        this.decls.add(decl);
    }

    // Obtenir la llista
    public List<SymbolDECL> getDecls() {
        return decls;
    }
}
