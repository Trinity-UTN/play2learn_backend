package trinity.play2learn.backend.configs.messages;

public class ValidationMessages {
    
    public static final String NOT_EMPTY_NAME = "El nombre no puede estar vacio.";
    public static final String MAX_LENGTH_NAME = "El nombre no puede tener mas de 50 caracteres.";
    public static final String PATTERN_NAME = "El nombre debe contener unicamente letras, numeros, espacios y los caracteres áéíóúÁÉÍÓÚñÑ.";

    public static final String NOT_EMPTY_LASTNAME = "El apellido no puede estar vacio.";
    public static final String MAX_LENGTH_LASTNAME = "El apellido no puede tener mas de 50 caracteres.";
    public static final String PATTERN_LASTNAME = "El apellido debe contener unicamente letras, numeros, espacios y los caracteres áéíóúÁÉÍÓÚñÑ.";

    public static final String NOT_EMPTY_EMAIL = "El email no puede estar vacio.";
    public static final String MAX_LENGTH_EMAIL = "El email no puede tener mas de 100 caracteres.";
    public static final String PATTERN_EMAIL = "El email debe ser un correo electronico valido.";

    public static final String NOT_EMPTY_DNI = "El DNI no puede estar vacio.";
    public static final String PATTERN_DNI = "El DNI debe contener 8 numeros.";

    public static final String NOT_NULL_COURSE = "El curso no puede estar vacio.";

    public static final String NOT_NULL_OPTIONAL = "Opcional es un campo requerido.";
}
