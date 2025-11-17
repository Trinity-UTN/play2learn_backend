package trinity.play2learn.backend.activity.ahorcado.models;

public enum Errors {
    TRES(3),CINCO(5);

    private final int value;

    Errors(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
