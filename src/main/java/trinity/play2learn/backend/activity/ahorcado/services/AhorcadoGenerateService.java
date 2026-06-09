package trinity.play2learn.backend.activity.ahorcado.services;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trinity.play2learn.backend.activity.ahorcado.repositories.IAhorcadoRepository;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.mappers.AhorcadoMapper;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.activity.ahorcado.services.interfaces.IAhorcadoGenerateService;
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
public class AhorcadoGenerateService implements IAhorcadoGenerateService {

    private final IAhorcadoRepository ahorcadoRepository;

    private final ISubjectGetByIdService getSubjectByIdService;

    private final ITransactionGenerateService transactionGenerateService;
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final INotificationCreateByUsersService createUsersNotifications;
    @Transactional
    @Override
    public AhorcadoResponseDto cu39GenerateAhorcado(AhorcadoRequestDto ahorcadoDto, User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Subject subject = getSubjectByIdService.findById(ahorcadoDto.getSubjectId()); //Lanza un 404 si no encuentra la materia con el id proporcionado

        if (!subject.getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no esta asignado a la materia");
        }
        
        Ahorcado ahorcadoSaved = ahorcadoRepository.save(AhorcadoMapper.toModel(ahorcadoDto, subject));

        transactionGenerateService.generate (
            TypeTransaction.ACTIVIDAD,
            ahorcadoSaved.getInitialBalance(),
            "Actividad de ahorcado",
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject,
            ahorcadoSaved,
            null,
            null,
            null,
            null
        );

        if (ahorcadoDto.getStartDate() == null) {
            
            List<User> users = subject.getStudents().stream()
                .map(student -> student.getUser())
                .collect(Collectors.toList());
                
            createUsersNotifications.createUsersNotifications(users,NotificationType.NEW_ACTIVITY_PUBLISHED);
        }

        return AhorcadoMapper.toDto(ahorcadoSaved);
    }
    
    
}
