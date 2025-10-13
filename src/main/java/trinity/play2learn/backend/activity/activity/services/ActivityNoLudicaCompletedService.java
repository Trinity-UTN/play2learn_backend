package trinity.play2learn.backend.activity.activity.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNoLudicaCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaCreateAttemptService;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaValidationsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityNoLudicaCompletedService implements IActivityNoLudicaCompletedService {
    private final IActivityGetByIdService activityFindByIdService;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IActivityValidatePublishedStatusService activityValidatePublishedStatusService;

    private final IActivityGetCompletedStateService activityGetCompletedStateService;

    private final IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;

    private final INoLudicaCreateAttemptService noLudicaCreateAttemptService;

    private final IActivityCompletedRepository activityCompletedRepository;

    private final INoLudicaValidationsService noLudicaValidationsService;

    @Override
    public ActivityCompletedResponseDto cu72ActivityNoLudicaCompleted(Long activityId, String plainText,
            MultipartFile file, User user) {

        if(plainText == null) plainText = "";//Si no se pasa el texto lo deja vacio
         
        //Realiza validaciones de atributos
        noLudicaValidationsService.validateNoLudicaCompleted(plainText, file);

        Activity activity = activityFindByIdService.findActivityById(activityId);

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        // Valida que la actividad este publicada(fecha actual dentro de la fecha de
        // inicio y fin de la actividad)
        activityValidatePublishedStatusService.validatePublishedStatus(activity);

        // Valida que la actividad no haya sido aprobada
        ActivityCompletedState activityCompletedState = activityGetCompletedStateService
                .getActivityCompletedState(activity, student);
        if (activityCompletedState == ActivityCompletedState.APPROVED) {
            throw new ConflictException("La actividad ya ha sido aprobada.");
        }else if(activityCompletedState == ActivityCompletedState.PENDING){

            throw new ConflictException("La actividad se encuentra pendiente de revision.");
        }

        Optional<ActivityCompleted> lastStarted = activityCompletedGetLastStartedService.get(activity, student);

        if (lastStarted.isEmpty()) {
            throw new ConflictException("No se puede actualizar la actividad ya que no se encuentra en curso.");
        }

        ActivityCompleted activityCompleted = lastStarted.get();

        activityCompleted.setState(ActivityCompletedState.PENDING);

        // Si el tiempo de intento es mayor al tiempo maximo de la actividad, se
        // desaprueba automaticamente
        if (((int) (Duration.between(lastStarted.get().getStartedAt(), LocalDateTime.now()).getSeconds())
                / 60) > activity.getMaxTime()) {
            activityCompleted.setState(ActivityCompletedState.DISAPPROVED);

        } else { //Si se desaprueba la actividad no se guarda la respuesta

            //Sube el archivo a uploadCare y crea el intento de NoLudica
            NoLudicaAttempt noLudicaAttempt = noLudicaCreateAttemptService.createAttempt(plainText, file);
            activityCompleted.setNoLudicaAttempt(noLudicaAttempt);
            
        }

        activityCompleted.setCompletedAt(LocalDateTime.now());

        ActivityCompleted savedActivityCompleted = activityCompletedRepository.save(activityCompleted);
        
        return ActivityCompletedMapper.toDto(savedActivityCompleted);
    }

}
