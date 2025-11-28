package trinity.play2learn.backend.notification.models;

public enum NotificationType {
    NEW_ACTIVITY_PUBLISHED("Nueva actividad disponible"),
    ACTIVITY_ABOUT_TO_EXPIRE("Actividad por vencer"),
    CORRECTED_ACTIVITY("Actividad corregida"),
    NEW_BENEFIT("Nuevo beneficio disponible"),
    NEW_SKINS("Nueva skin disponible"),
    NEW_ACHIEVEMENT("Nuevo logro disponible"),
    RANKING_TOP_1("Has llegado a la cima del ranking");

    private final String title;

    NotificationType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
