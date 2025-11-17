package trinity.play2learn.backend.configs.messages;

public class EconomyMessages {

    //-------------------------- RESERVE MESSAGES --------------------------
    public static final String NOT_ENOUGH_MONEY = "No tienes suficiente dinero.";
    public static final String NOT_ENOUGH_RESERVE_MONEY = "No hay suficiente dinero en la reserva.";
    public static final String AMOUNT_MAJOR_TO_0 = "El monto debe ser mayor a 0.";

    //-------------------------- WALLET MESSAGES --------------------------
    public static final String NOT_ENOUGH_WALLET_MONEY = "No tienes suficiente dinero en tu wallet.";
    public static final String NOT_ENOUGH_WALLET_MONEY_STUDENT = "El estudiante no tiene suficiente dinero para realizar la transaccion";

    //-------------------------- TRANSACCION MESSAGES --------------------------
    public static final String TRANSACTION_NOT_SUPPORTED = "Tipo de transaccion no soportada: ";

    public static String getTransactionNotSupported(String type) {
        return TRANSACTION_NOT_SUPPORTED + type;
    }

    public static final String INCORRECT_AMOUNT = "El monto es incorrecto.";
    public static final String NOT_ENOUGH_WALLET_MONEY_SUBJECT = "La materia no tiene suficiente dinero para realizar la transaccion";




}
