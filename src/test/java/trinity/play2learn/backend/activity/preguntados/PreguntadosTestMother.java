package trinity.play2learn.backend.activity.preguntados;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.preguntados.dtos.request.OptionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PreguntadosTestMother {

    public static final String DEFAULT_QUESTION = "¿Pregunta?";
    public static final String DEFAULT_OPTION = "Opción";
    public static final String MAX_LENGTH_QUESTION = "a".repeat(200);
    public static final String MAX_LENGTH_OPTION = "a".repeat(100);
    public static final Integer DEFAULT_MAX_TIME_PER_QUESTION = 10;
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static PreguntadosRequestDto preguntadosRequestDto(
        List<QuestionRequestDto> questions
    ) {
        LocalDateTime now = LocalDateTime.now();
        return PreguntadosRequestDto.builder()
            .description("Descripción de la actividad de preguntados")
            .startDate(now.plusDays(1))
            .endDate(now.plusDays(7))
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subjectId(ActivityTestMother.SUBJECT_ID)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .maxTimePerQuestionInSeconds(DEFAULT_MAX_TIME_PER_QUESTION)
            .questions(questions)
            .build();
    }

    public static PreguntadosRequestDto validPreguntadosRequestDto() {
        return preguntadosRequestDto(questions(5));
    }

    public static QuestionRequestDto question(String questionText) {
        return QuestionRequestDto.builder()
            .question(questionText != null ? questionText : DEFAULT_QUESTION)
            .options(options(4))
            .build();
    }

    public static QuestionRequestDto question(String questionText, List<OptionRequestDto> options) {
        return QuestionRequestDto.builder()
            .question(questionText != null ? questionText : DEFAULT_QUESTION)
            .options(options)
            .build();
    }

    public static QuestionRequestDto questionWithoutCorrectOption(String questionText) {
        return QuestionRequestDto.builder()
            .question(questionText != null ? questionText : DEFAULT_QUESTION)
            .options(optionsWithoutCorrect(4))
            .build();
    }

    public static QuestionRequestDto questionWithMultipleCorrectOptions(String questionText) {
        return QuestionRequestDto.builder()
            .question(questionText != null ? questionText : DEFAULT_QUESTION)
            .options(optionsWithMultipleCorrect(4))
            .build();
    }

    public static OptionRequestDto option(String optionText, Boolean isCorrect) {
        return OptionRequestDto.builder()
            .option(optionText != null ? optionText : DEFAULT_OPTION)
            .isCorrect(isCorrect != null ? isCorrect : true)
            .build();
    }

    public static List<OptionRequestDto> options(int count) {
        List<OptionRequestDto> options = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            options.add(option("Opción " + (i + 1), i == 0));
        }
        return options;
    }

    public static List<OptionRequestDto> optionsWithoutCorrect(int count) {
        List<OptionRequestDto> options = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            options.add(option("Opción " + (i + 1), false));
        }
        return options;
    }

    public static List<OptionRequestDto> optionsWithMultipleCorrect(int count) {
        List<OptionRequestDto> options = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            options.add(option("Opción " + (i + 1), true));
        }
        return options;
    }

    public static List<QuestionRequestDto> questions(int count) {
        List<QuestionRequestDto> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            questions.add(question("¿Pregunta " + (i + 1) + "?"));
        }
        return questions;
    }

    public static PreguntadosResponseDto preguntadosResponseDto(
        Long id,
        List<?> questions
    ) {
        return PreguntadosResponseDto.builder()
            .id(id)
            .name("Preguntados")
            .description("Descripción de la actividad de preguntados")
            .questions(questions != null ? (List) questions : List.of())
            .build();
    }

    public static PreguntadosResponseDto validPreguntadosResponseDto(Long id) {
        return preguntadosResponseDto(id, List.of());
    }

    public static Preguntados savedPreguntados(Long id, Subject subject) {
        return Preguntados.builder()
            .id(id)
            .name("Preguntados")
            .description("Descripción de la actividad de preguntados")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .maxTimePerQuestion(DEFAULT_MAX_TIME_PER_QUESTION)
            .questions(List.of())
            .build();
    }
}

