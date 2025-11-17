package trinity.play2learn.backend.configs.messages;

public class BadRequestExceptionMessages {
    
    public static final String invalidFormat(String atribute) {
        return "Formato invalido para el atributo: " + atribute;
    }

    public static final String eventOrderRepeated (String order){
        return "El orden " + order + " se encuentra repetido.";
    }

    public static final String eventOrderNotConsecutive (String maxOrder){
        return "El orden no es consecutivo, se esperaban " + maxOrder + " eventos consecutivos sin saltos.";
    }

    public static final String oneCorrectOptionPerQuestion (String question){
        return "La pregunta " + question + " debe tener una sola opcion correcta.";
    }
}
