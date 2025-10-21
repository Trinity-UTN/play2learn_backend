package trinity.play2learn.backend.investment.stock.models;

public enum RiskLevel {
    BAJO (5),
    MEDIO (10),
    ALTO (15);
    
    private final int valor;

    RiskLevel(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
