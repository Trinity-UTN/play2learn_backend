package trinity.play2learn.backend.configs.messages;

public class ConflictExceptionMessages {
    
    public static final String resourceAlreadyDeleted(String resource, String id) {
        return resource + " con id " + id + " ya fue eliminado.";
    }

    public static final String resourceDeletionNotAllowedDueToAssociations (String resource, String id, String associations) {
        return "El recurso " + resource + " con id " + id + " no puede ser eliminado porque tiene asociaciones con: " + associations + ".";
    }

    public static final String resourceAlreadyExists(String resource) {
        return resource + " ya existe.";
    }

    public static final String resourceAlreadyExists(String resource, String id) {
        return resource + " con id " + id + " ya existe.";
    }

    public static final String resourceAlreadyExistsByName(String resource, String name) {
        return resource + " con el nombre " + name + " ya existe.";
    }

    public static final String resourceAlreadyExistsByDni (String resource, String dni) {
        return resource + " con el DNI " + dni + " ya existe.";
    }

    public static final String resourceAlreadyExistsByAtribute (String resource, String attribute, String value) {
        return resource + " con el atributo " + attribute + " con el valor " + value + " ya existe.";
    }
}
