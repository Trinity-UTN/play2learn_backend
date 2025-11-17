package trinity.play2learn.backend.configs.messages;

public class SuccessfulMessages {

    public static final String deletedSuccessfully (){
        return "Eliminado correctamente";
    }

    public static final String deletedSuccessfully(String resource) {
        return resource + " eliminado correctamente";
    }

    public static final String createdSuccessfully(String resource) {
        return resource + " creado correctamente";
    }

    public static final String updatedSuccessfully(String resource) {
        return resource + " actualizado correctamente";
    }

    public static final String updatedSuccessfully() {
        return "Actualizado correctamente";
    }

    public static final String createdSuccessfully() {
        return "Creado correctamente";
    }

    public static final String okSuccessfully() {
        return "OK";
    }

    public static final String restoredSuccessfully(String resource) {
        return resource + " restaurado correctamente";
    }

    public static final String restoredSuccessfully() {
        return "Restaurado correctamente";
    }

    public static final String SUBJECT_ADD_STUDENTS_SUCCESSFULLY = "Estudiantes agregado con exito";
    public static final String SUBJECT_REMOVE_STUDENTS_SUCCESSFULLY = "Estudiantes removidos con exito";

    public static final String SUBJECT_ASSIGN_TEACHER_SUCCESFULLY = "Profesor asignado con exito";
    public static final String SUBJECT_UNASSIGN_TEACHER_SUCCESFULLY = "Profesor desasignado con exito";

    public static final String loginSuccessfully() {
        return "Logueado correctamente";
    }

    public static final String refreshTokenSuccessfully() {
        return "Token actualizado correctamente";
    }

}
