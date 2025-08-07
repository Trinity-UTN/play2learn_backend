package trinity.play2learn.backend.configs.messages;

public class ValidationMessages {
    
    //---------------------------------------- ADMIN ----------------------------------------------
    public static final String NOT_EMPTY_NAME = "El nombre no puede estar vacio.";
    public static final String MAX_LENGTH_NAME_50 = "El nombre no puede tener mas de 50 caracteres.";
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

    public static final String NOT_NULL_YEAR = "El año no puede estar vacio.";

    //--------------------------------------- ACTIVITY ------------------------------------------
    public static final String MAX_LENGTH_DESCRIPTION_1000 = "La descripcion no puede tener mas de 1000 caracteres.";
    public static final String NOT_NULL_START_DATE = "La fecha de inicio no puede estar vacia.";
    public static final String NOT_NULL_END_DATE = "La fecha de finalizacion no puede estar vacia.";
    public static final String NOT_NULL_DIFICULTY = "La dificultad no puede estar vacia.";
    public static final String NOT_NULL_SUBJECT = "La materia no puede estar vacia.";

    public static final String NOT_EMPTY_WORD = "La palabra no puede estar vacia.";
    public static final String MAX_LENGTH_WORD = "La palabra no puede tener mas de 50 caracteres.";
    public static final String PATTERN_WORD = "La palabra debe contener unicamente letras, numeros, espacios y los caracteres áéíóúÁÉÍÓÚñÑ.";
    public static final String NOT_NULL_ERRORS_PERMITED = "El numero de errores no puede estar vacio.";

    public static final String MAX_LENGTH_NAME_100 = "El nombre no puede tener mas de 100 caracteres.";
    public static final String NOT_NULL_CATEGORIES = "Las categorias no pueden estar vacias.";
    public static final String LENGTH_CATEGORIES = "Debe haber al menos 2 categorias y un maximo de 10 categorias.";
    public static final String NOT_NULL_CONCEPTS = "Los conceptos no pueden estar vacios.";
    public static final String LENGTH_CONCEPTS = "Debe haber al menos 1 conceptos y un maximo de 10 conceptos.";
    public static final String UNIQUE_CONCEPTS_NAME = "Los nombres de los conceptos deben ser unicos.";

    public static final String LENGTH_WORD = "La palabra debe tener entre 1 y 30 caracteres.";
    public static final String NOT_NULL_WORD_ORDER = "El orden de las palabras no puede estar vacio.";
    public static final String NOT_NULL_IS_MISSING = "El campo isMissing no puede estar vacio.";
    public static final String LENGTH_SENTENCE = "La oracion debe tener entre 3 y 300 palabras.";
    public static final String LENGTH_SENTENCES = "La activividad debe tener entre 1 y 20 oraciones.";
    public static final String WORD_MISSING = "Debe faltar al menos una palabra en la oracion.";
    public static final String WORD_ORDER = "Las palabras en una oración deben tener órdenes únicos.";
    public static final String SENTENCE_ORDER = "Las palabras en una oración deben tener órdenes entre 0 y números de palabras.";

    public static final String NOT_NULL_CONCEPT = "El concepto no puede estar vacio.";
    public static final String MAX_LENGTH_CONCEPT = "El concepto no puede tener mas de 50 caracteres.";
    public static final String PATTERN_CONCEPT = "El concepto debe contener unicamente letras, numeros, espacios y los caracteres áéíóúÁÉÍÓÚñÑ.";
    public static final String NOT_NULL_IMAGE = "La imagen no puede estar vacia.";
    public static final String MIN_COUPLES = "Debe haber al menos 1 pareja.";
    public static final String LENGTH_COUPLES = "El numero de parejas debe ser entre 4 y 8.";
    public static final String NOT_NULL_IMAGE_CONCEPT = "La imagen y el concepto no pueden estar vacios.";

    public static final String MAX_LENGTH_EXCERSICE = "El ejercicio no puede tener mas de 300 caracteres.";
    public static final String NOT_NULL_EXCERSICE = "El ejercicio no puede estar vacio.";
    public static final String NOT_NULL_TIPO_ENTREGA = "El tipo de entrega no puede estar vacio.";

    public static final String LENGTH_EVENTS = "El numero de eventos debe ser entre 3 y 10.";
    public static final String NOT_EMPTY_DESCRIPTION = "La descripcion no puede estar vacia.";
    public static final String MAX_LENGTH_DESCRIPTION_100 = "La descripcion no puede tener mas de 100 caracteres.";
    public static final String MIN_ORDER = "El orden debe ser mayor o igual a 0.";
    public static final String NOT_EMPTY_EVENT_LIST = "La lista de eventos no puede estar vacia.";

    public static final String NOT_EMPTY_OPTION = "La opcion no puede estar vacia.";
    public static final String MAX_LENGTH_OPTION = "La opcion no puede tener mas de 100 caracteres.";
    public static final String MIN_TIME_PER_QUESTION = "El tiempo por pregunta debe ser mayor a 10 segundos.";
    public static final String MIN_LENGTH_QUESTIONS = "El numero de preguntas debe ser mayor o igual a 5.";
    public static final String NOT_EMPTY_QUESTION = "La pregunta no puede estar vacia.";
    public static final String MAX_LENGTH_QUESTION = "La pregunta no puede tener mas de 200 caracteres.";
    public static final String LENGTH_OPTIONS = "El numero de opciones debe ser exactamente 3.";

    //------------------------------------- ARBOL DE DECISION ------------------------------------------------------
    public static final String NOT_NULL_DECISION_TREE = "El arbol de decision no puede estar vacio.";
    public static final String OPTIONS_SIZE = "Una decisión debe tener exactamente 2 opciones o ninguna.";
    public static final String OPTIONS_AND_CONSECUENCE_NULL = "La decision debe tener opciones o una consecuencia.";
    public static final String NAME_200_MAX_LENGHT = "El nombre no puede tener mas de 200 caracteres.";
    public static final String INTRODUCTION_500_MAX_LENGHT = "La introduccion no puede tener mas de 500 caracteres.";
    
    //------------------------------------- USER ------------------------------------------------------
    public static final String NOT_EMPTY_PASSWORD = "La contraseña no puede estar vacia.";
    public static final String NOT_EMPTY_ROLE = "El rol no puede estar vacio.";
    public static final String NOT_EMPTY_TOKEN = "El token no puede estar vacio.";
    

    //-------------------------------------- PROFILE -------------------------------------------------------
    public static final String NOT_NULL_PRICE = "El precio no puede estar vacio.";
    public static final String NOT_NULL_TYPE = "El tipo de aspecto no puede estar vacio.";


    //------------------------------------- BENEFIT ------------------------------------------------------
    public static final String NOT_NULL_COST = "El costo no puede estar vacio.";
    public static final String MIN_COST = "El costo debe ser mayor a 0.";
}

