package trinity.play2learn.backend;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

	@PostConstruct
    public void init() {
        // Establece la zona horaria por defecto a Córdoba, Argentina
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Cordoba"));
    }

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
