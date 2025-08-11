package trinity.play2learn.backend.configs.messages;

public class NotFoundExceptionMesagges {
    
    public static final String resourceNotFoundById(String resource, String id) {
        return resource + " con id " + id + " no encontrado.";
    }

    public static final String resourceDeletedNotFoundById (String resource, String id) {
        return resource + " con id " + id + " no encontrado o ya fue eliminado.";
    }

    public static final String resourceNotFoundByEmail (String resource, String email) {
        return resource + " con email " + email + " no encontrado.";
    }
}
