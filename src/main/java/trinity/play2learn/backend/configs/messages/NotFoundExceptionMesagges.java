package trinity.play2learn.backend.configs.messages;

public class NotFoundExceptionMesagges {
    
    public static final String resourceNotFound(String resource, String id) {
        return resource + " con id " + id + " no encontrado.";
    }

    public static final String resourceDeletedNotFound (String resource, String id) {
        return resource + " con id " + id + " no encontrado o ya fue eliminado.";
    }
}
