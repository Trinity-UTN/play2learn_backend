package trinity.play2learn.backend.configs.levels;

public enum ValueXp {
    BASE (100L),
    ACTIVITY_FACIL (10L),
    ACTIVITY_MEDIO (20L),
    ACTIVITY_DIFICIL (30L),
    ASPECT_BOUGHT (100L),
    BENEFIT_BOUGHT (100L),
    FIXED_INVESTMENT (100L),
    SAVING_ACCOUNT_DEPOSIT (100L),
    STOCK_BOUGHT (100L);

    private final Long value;

    ValueXp(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}
