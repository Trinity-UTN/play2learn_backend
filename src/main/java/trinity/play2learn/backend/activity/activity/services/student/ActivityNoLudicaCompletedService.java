package trinity.play2learn.backend.activity.activity.services.student;

    import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.mappers.NoLudicaAttemptMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNoLudicaCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidatePublishedStatusService;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaUploadFileService;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaValidationsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityNoLudicaCompletedService implements IActivityNoLudicaCompletedService {
    private final IActivityGetByIdService activityFindByIdService;

    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityValidatePublishedStatusService activityValidatePublishedStatusService;
    private final IActivityGetCompletedStateService activityGetCompletedStateService;
    private final INoLudicaUploadFileService noLudicaUploadFileService;
    private final IActivityCompletedRepository activityCompletedRepository;
    private final INoLudicaValidationsService noLudicaValidationsService;
    private final IActivityGetRemainingAttemptsService activityGetRemainingAttemptsService;
    
    @Override
    public ActivityCompletedResponseDto cu72ActivityNoLudicaCompleted(Long activityId, String plainText,
            MultipartFile file, User user) {

        if (plainText == null)
            plainText = "";// Si no se pasa el texto lo deja vacio

        // Realiza validaciones de atributos
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
        } else if (activityCompletedState == ActivityCompletedState.PENDING) {

            throw new ConflictException("La actividad se encuentra pendiente de revision.");
        }

        Integer remainingAttempts = activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student);

        if (remainingAttempts <= 0) {
            throw new ConflictException("No se puede realizar la actividad ya que no quedan intentos restantes.");
        }

        // Sube el archivo a Cloudinary
        StoredFile storedFile = noLudicaUploadFileService.uploadFileIfExist(file);

        //Crea el intento de NoLudica
        NoLudicaAttempt noLudicaAttempt = NoLudicaAttemptMapper.toModel(plainText, storedFile);

        ActivityCompleted activityCompleted = ActivityCompletedMapper.toModel(
            activity, student, null, remainingAttempts, ActivityCompletedState.PENDING, noLudicaAttempt, 0, 0, 0, 0);

        activityCompleted.setCompletedAt(LocalDateTime.now());
            
        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
    }

}
