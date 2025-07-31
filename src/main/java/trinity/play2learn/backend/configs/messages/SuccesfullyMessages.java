package trinity.play2learn.backend.configs.messages;

public class SuccesfullyMessages {

    public static final String deletedSuccessfully (){
        return "Eliminado correctamente";
    }

    public static final String deletedSuccessfully(String resurce) {
        return resurce + " eliminado correctamente";
    }

    public static final String createdSuccessfully(String resurce) {
        return resurce + " creado correctamente";
    }

    public static final String updatedSuccessfully(String resurce) {
        return resurce + " actualizado correctamente";
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
    
}
