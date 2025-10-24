package trinity.play2learn.backend.investment.fixedTermDeposit.models;

public enum FixedTermDays {
    SEMANAL (7),
    QUINZENAL (15),
    MENSUAL (30);
    
    private final int valor;

    FixedTermDays(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
