package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLast5RealizationsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityRealizationResponseDto;
import trinity.play2learn.backend.utils.TimeUtils;

@Service
@AllArgsConstructor
public class ActivityGetLast5RealizationsService implements IActivityGetLast5RealizationsService{

    private final IActivityCompletedRepository repository;

    @Override
    public List<StatisticsActivityRealizationResponseDto> execute(Student student) {
        List<StatisticsActivityRealizationResponseDto> response = new ArrayList<>();
        for (ActivityCompleted actividad : repository.findTop5ByStudentAndStateNotOrderByCompletedAtDesc(student, ActivityCompletedState.IN_PROGRESS)) {
            response.add(
                StatisticsActivityRealizationResponseDto.builder()
                    .name(actividad.getActivity().getName())
                    .subject(actividad.getActivity().getSubject().getName())
                    .result(actividad.getState().name())
                    .reward((actividad.getReward() == null) ? 0.0 : (Math.round(actividad.getReward() * 100.0) / 100.0))
                    .doneAgo(TimeUtils.tiempoTranscurrido(actividad.getCompletedAt()))
                    .build()
            );
        };

        return response;
    }
    
}
