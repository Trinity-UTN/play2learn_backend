package trinity.play2learn.backend.activity.activity.models;

public enum Attempts {
    
    TRES(3),CINCO(5);

    private final int value;

    private Attempts(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
