package trinity.play2learn.backend.notification.models;

public enum NotificationType {
    //Estudiante
    NEW_ACTIVITY_PUBLISHED("Nueva actividad disponible"),
    ACTIVITY_ABOUT_TO_EXPIRE("Tienes una actividad que expira hoy"),
    CORRECTED_ACTIVITY("Actividad corregida"),
    NEW_BENEFIT("Nuevo beneficio disponible"),
    NEW_SKINS("Nueva skin disponible"),
    NEW_ACHIEVEMENT("Nuevo logro disponible"),
    RANKING_TOP_1("Has llegado a la cima del ranking"),
    BENEFIT_USE_ACCEPTED("El docente acepto el uso del beneficio que solicitaste"),
    STOCK_ORDER_EXECUTED("Se ejecuto la orden de venta de acciones"),
    FIXED_TERM_DEPOSIT_ENDED("Finalizo tu plazo fijo y las monedas fueron depositadas en tu billetera"),

    //Docente
    STUDENT_COMPLETE_ACTIVITY("Un estudiante realizo tu actividad"),
    ALL_STUDENTS_APPROVE_ACTIVITY("Todos los estudiantes aprobaron tu actividad"),
    BENEFIT_PURCHASED("Un estudiante compró tu beneficio"),
    BENEFIT_USE_REQUESTED("Un estudiante solicito el uso de tu beneficio"),
    ;

    private final String title;

    NotificationType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
