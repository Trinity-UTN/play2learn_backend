package trinity.play2learn.backend.activity.activity.services.teacher;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityReviewNoLudicaRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityTeacherReviewNoLudicaService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.noLudica.models.NoLudica;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityTeacherReviewNoLudicaService implements IActivityTeacherReviewNoLudicaService {

    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IActivityGetCompletedByIdService activityGetCompletedByIdService;
    private final IActivityGetCompletedStateService activityGetCompletedStateService;
    private final Map<String, IActivityCompletedStrategyService> activityCompletedStrategyServiceMap;

    @Override
    public ActivityCompletedResponseDto cu129TeacherReviewNoLudica(User user,
            ActivityReviewNoLudicaRequestDto activityReviewNoLudicaDto) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        ActivityCompleted activityCompleted = activityGetCompletedByIdService
                .findActivityCompletedById(activityReviewNoLudicaDto.getActivityCompletedId());

        // Valida que el docente sea el dueño de la actividad
        if (!activityCompleted.getActivity().getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no es el dueño de la actividad");
        }

        // Valida que la actividad sea de tipo NoLudica
        if (!(activityCompleted.getActivity() instanceof NoLudica)) {
            throw new ConflictException("La actividad no es de tipo NoLudica");
        }

        // Valida que la actividad se encuentra en estado pendiente
        if (activityCompleted.getState() != ActivityCompletedState.PENDING) {
            throw new ConflictException("La actividad no se encuentra en estado pendiente");
        }

        // Valida que la actividad no haya sido aprobada
        ActivityCompletedState activityCompletedState = activityGetCompletedStateService
                .getActivityCompletedState(activityCompleted.getActivity(), activityCompleted.getStudent());
        if (activityCompletedState == ActivityCompletedState.APPROVED) {
            throw new ConflictException("La actividad ya ha sido aprobada.");
        }

        activityCompleted.setScore(activityReviewNoLudicaDto.getScore());

        activityCompleted.setComment(activityReviewNoLudicaDto.getComment());

        IActivityCompletedStrategyService strategyService = activityCompletedStrategyServiceMap
                .get(activityReviewNoLudicaDto.getState().name());

        return strategyService.execute(activityCompleted);
    }
}
