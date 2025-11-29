package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGenerateService;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateByUsersService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class BenefitGenerateService implements IBenefitGenerateService {

    private final IBenefitRepository benefitRepository;
    private final ISubjectGetByIdService subjectGetService;
    private final INotificationCreateByUsersService createUsersNotifications;

    @Override
    @Transactional
    public BenefitResponseDto cu51GenerateBenefit(BenefitRequestDto benefitDto, User user) {

        Subject subject = subjectGetService.findById(benefitDto.getSubjectId());

        // Valida que el docente este asignado a la materia sobre la cual quiere crear
        // el beneficio
        if (!subject.hasTeacherByEmail(user.getEmail())) {
            throw new UnauthorizedException(UnauthorizedExceptionMessages.BENEFIT_UNAUTHORIZED_TEACHER);
        }

        Benefit benefit = BenefitMapper.toModel(benefitDto, subject);

        List<User> users = benefit.getSubject().getStudents().stream().map(Student::getUser).toList();

        createUsersNotifications.createUsersNotifications(users,
                NotificationType.NEW_BENEFIT);

        return BenefitMapper.toDto(benefitRepository.save(benefit));
    }

}
