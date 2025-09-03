package trinity.play2learn.backend.activity.preguntados.Mappers;

import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class PreguntadosMapper {
    
    public static Preguntados toModel(PreguntadosRequestDto preguntadosDto, Subject subject) {
        Preguntados preguntados = Preguntados.builder()
            .name("Preguntados")
            .description(preguntadosDto.getDescription())
            .dificulty(preguntadosDto.getDificulty())
            .maxTime(preguntadosDto.getMaxTime())
            .subject(subject)
            .startDate(preguntadosDto.getStartDate())
            .endDate(preguntadosDto.getEndDate())
            .attempts(preguntadosDto.getAttempts())
            .maxTimePerQuestion(preguntadosDto.getMaxTimePerQuestionInSeconds())
            .actualBalance(preguntadosDto.getInitialBalance())
            .initialBalance(preguntadosDto.getInitialBalance())
            .typeReward(TypeReward.EQUITATIVO)
            .build();
        
        preguntados.setQuestions(QuestionMapper.toModelList(preguntadosDto.getQuestions()));
        return preguntados;
    }

    public static PreguntadosResponseDto toDto(Preguntados preguntados) {
        return PreguntadosResponseDto.builder()
            .id(preguntados.getId())
            .name("Preguntados")
            .description(preguntados.getDescription())
            .dificulty(preguntados.getDificulty())
            .maxTime(preguntados.getMaxTime())
            .subject(SubjectMapper.toSubjectDto(preguntados.getSubject()))
            .startDate(preguntados.getStartDate())
            .endDate(preguntados.getEndDate())
            .attempts(preguntados.getAttempts())
            .maxTimePerQuestionInSeconds(preguntados.getMaxTimePerQuestion())
            .questions(QuestionMapper.toDtoList(preguntados.getQuestions()))
            .actualBalance(preguntados.getActualBalance())
            .initialBalance(preguntados.getInitialBalance())
            .build();
    }
}
