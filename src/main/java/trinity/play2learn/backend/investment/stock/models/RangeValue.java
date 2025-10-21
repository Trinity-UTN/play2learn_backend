package trinity.play2learn.backend.investment.stock.models;

public enum RangeValue {
    DIARIO (1),
    SEMANAL (7),
    QUINZENAL (15),
    MENSUAL (30),
    HISTORICO (0);
    
    private final int valor;

    private RangeValue(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
