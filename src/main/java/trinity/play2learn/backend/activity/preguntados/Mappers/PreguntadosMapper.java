package trinity.play2learn.backend.activity.preguntados.Mappers;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Component("preguntadosMapper")
public class PreguntadosMapper implements IActivityMapper{
    
    public static Preguntados toModel(PreguntadosRequestDto preguntadosDto, Subject subject) {
        Preguntados preguntados = Preguntados.builder()
            .name("Preguntados")
            .description(preguntadosDto.getDescription())
            .difficulty(preguntadosDto.getDifficulty())
            .maxTime(preguntadosDto.getMaxTime())
            .subject(subject)
            .startDate(preguntadosDto.getStartDate())
            .endDate(preguntadosDto.getEndDate())
            .attempts(preguntadosDto.getAttempts())
            .maxTimePerQuestion(preguntadosDto.getMaxTimePerQuestionInSeconds())
            .actualBalance(0.0)
            .initialBalance(preguntadosDto.getInitialBalance())
            .typeReward((preguntadosDto.getTypeReward() != null) ? preguntadosDto.getTypeReward() : TypeReward.EQUITATIVO)
            .build();
        
        preguntados.setQuestions(QuestionMapper.toModelList(preguntadosDto.getQuestions()));
        return preguntados;
    }

    public static PreguntadosResponseDto toDto(Preguntados preguntados) {
        return PreguntadosResponseDto.builder()
            .id(preguntados.getId())
            .name("Preguntados")
            .description(preguntados.getDescription())
            .difficulty(preguntados.getDifficulty())
            .maxTime(preguntados.getMaxTime())
            .subject(SubjectMapper.toSimplifiedDto(preguntados.getSubject()))
            .startDate(preguntados.getStartDate())
            .endDate(preguntados.getEndDate())
            .attempts(preguntados.getAttempts())
            .maxTimePerQuestionInSeconds(preguntados.getMaxTimePerQuestion())
            .questions(QuestionMapper.toDtoList(preguntados.getQuestions()))
            .actualBalance(preguntados.getActualBalance())
            .initialBalance(preguntados.getInitialBalance())
            .typeReward(preguntados.getTypeReward())
            .build();
    }

    @Override
    public ActivityResponseDto toActivityDto(Activity activity) {
        if (!(activity instanceof Preguntados)) {
            throw new IllegalArgumentException("Expected Preguntados, got: " + activity.getClass());
        }
        Preguntados preguntados = (Preguntados) activity;
        return toDto(preguntados);
    }
}
