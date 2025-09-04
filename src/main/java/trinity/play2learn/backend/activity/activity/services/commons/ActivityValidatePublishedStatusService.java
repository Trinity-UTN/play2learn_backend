package trinity.play2learn.backend.activity.activity.services.commons;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class ActivityValidatePublishedStatusService implements IActivityValidatePublishedStatusService {

    //Valida que la actividad este publicada (Fecha actual dentro de la fecha de inicio y fin de la actividad)
    @Override
    public void validatePublishedStatus(Activity activity) {

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(activity.getStartDate()) || now.isAfter(activity.getEndDate())) {

            throw new ConflictException("No es posible realizar la actividad ya que no está publicada.");
        }
    }
    
    
}
