package trinity.play2learn.backend.activity.activity.services.teacher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.noLudica.NoLudicaAttemptResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.NoLudicaAttemptMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetNoLudicaAttemptService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityGetNoLudicaAttemptService implements IActivityGetNoLudicaAttemptService {

    private final IActivityGetCompletedByIdService activityGetCompletedByIdService;
    private final ITeacherGetByEmailService teacherGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public NoLudicaAttemptResponseDto cu127GetNoLudicaAttempt(Long activityId, User user) {

        ActivityCompleted activityCompleted = findAndValidateActivityCompleted(activityId, user);

        NoLudicaAttempt noLudicaAttempt = activityCompleted.getNoLudicaAttempt();

        return NoLudicaAttemptMapper.toDto(noLudicaAttempt, activityCompleted.getStudent().getId());
    }

    private ActivityCompleted findAndValidateActivityCompleted(Long activityCompletedId, User user) {

        ActivityCompleted activityCompleted = activityGetCompletedByIdService
                .findActivityCompletedById(activityCompletedId);

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        if (!activityCompleted.getActivity().getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no tiene permiso para acceder a esta actividad");
        }

        if (!activityCompleted.getActivity().getName().equals("No Ludica")) {
            throw new ConflictException("La actividad no es una actividad No Ludica");
        }

        if (activityCompleted.getNoLudicaAttempt() == null) {
            throw new ConflictException("La actividad no tiene un intento No Ludica");
        }

        return activityCompleted;
    }

}
