package semantic.symbols.descripcio;

public abstract class Descripcio {
    public enum TDesc {
        DVAR, DCONST, DPROC, DCAMP, DARG, DTIPUS
    }

    protected TDesc tipus;

    protected Descripcio(TDesc tipus) {
        this.tipus = tipus;
    }

    public TDesc getTipus() { return this.tipus; }

    @Override
    public String toString() { return this.tipus.name(); }
}
