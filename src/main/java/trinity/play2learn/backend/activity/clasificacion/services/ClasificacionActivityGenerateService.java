package trinity.play2learn.backend.activity.clasificacion.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.mappers.ClasificacionActivityMapper;
import trinity.play2learn.backend.activity.clasificacion.models.ClasificacionActivity;
import trinity.play2learn.backend.activity.clasificacion.repositories.IClasificacionActivityRepository;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionGenerateService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateCategoriesNamesService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateConceptsNamesService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateByUsersService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ClasificacionActivityGenerateService implements IClasificacionGenerateService {
    
    private final IClasificacionActivityRepository clasificacionRepository;
    private final ISubjectGetByIdService subjectGetService;
    private final IClasificacionValidateCategoriesNamesService validateCategoriesNamesService;
    private final IClasificacionValidateConceptsNamesService validateConceptsNamesService;
    private final ITransactionGenerateService transactionGenerateService;
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final INotificationCreateByUsersService createUsersNotifications;
    @Override
    @Transactional
    public ClasificacionActivityResponseDto cu43GenerateClasificacionActivity(ClasificacionActivityRequestDto activityRequestDto, User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        //Lanza un 404 si no encuentra la materia con el id proporcionado
        Subject subject = subjectGetService.findById(activityRequestDto.getSubjectId());
        
        if (!subject.getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no esta asignado a la materia");
        }
        
        //Lanza un 400 si las categorias tienen nombre repetidos
        validateCategoriesNamesService.validateCategoriesNames(activityRequestDto);

        //Lanza un 400 si los conceptos de todas las categorias tienen nombres repetidos
        validateConceptsNamesService.validateDuplicateConceptsNames(activityRequestDto);

        ClasificacionActivity activity = clasificacionRepository.save(ClasificacionActivityMapper.toModel(activityRequestDto, subject));

        transactionGenerateService.generate (
            TypeTransaction.ACTIVIDAD,
            activityRequestDto.getInitialBalance(),
            "Actividad de clasificación",
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject,
            activity,
            null,
            null,
            null,
            null
        );

        if (activityRequestDto.getStartDate() == null) {
            
            List<User> users = subject.getStudents().stream()
                .map(student -> student.getUser())
                .collect(Collectors.toList());
                
            createUsersNotifications.createUsersNotifications(users,NotificationType.NEW_ACTIVITY_PUBLISHED);
        }

        //Guarda la actividad en la base de datos y la retorno como response dto
        return ClasificacionActivityMapper.toDto(activity);
    }
    
    
}
