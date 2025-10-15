package trinity.play2learn.backend.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static String tiempoTranscurrido(LocalDateTime fecha) {
        if (fecha == null) return "";

        LocalDateTime ahora = LocalDateTime.now();

        // Si fue hoy
        if (fecha.toLocalDate().isEqual(LocalDate.now())) {
            long horas = Duration.between(fecha, ahora).toHours();
            if (horas == 0) {
                long minutos = Duration.between(fecha, ahora).toMinutes();
                return minutos <= 1 ? "Hace un minuto" : "Hace " + minutos + " minutos";
            }
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        }

        // Si fue ayer
        if (fecha.toLocalDate().isEqual(LocalDate.now().minusDays(1))) {
            return "Ayer";
        }

        // Si fue antes
        long dias = ChronoUnit.DAYS.between(fecha.toLocalDate(), LocalDate.now());
        return "Hace " + dias + (dias == 1 ? " día" : " días");
    }
}
