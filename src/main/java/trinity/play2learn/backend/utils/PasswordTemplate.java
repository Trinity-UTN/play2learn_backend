package trinity.play2learn.backend.utils;

import java.util.regex.Pattern;

public class PasswordTemplate {

    // La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un carácter especial.
    // La contraseña debe tener al menos 8 caracteres.
    // La contraseña no puede contener espacios.
    // La contraseña no puede tener mas de 32 caracteres.
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";

    public static Pattern getPasswordPattern() {
        return Pattern.compile(PASSWORD_PATTERN);
    }

    public static boolean validate(String password) {
        return getPasswordPattern().matcher(password).matches();
    }

}