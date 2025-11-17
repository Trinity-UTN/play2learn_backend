package trinity.play2learn.backend.activity.arbolDeDecision;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ConsecuenceArbolDecisionRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.DecisionArbolDecisionRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.models.ArbolDeDecisionActivity;
import trinity.play2learn.backend.admin.subject.models.Subject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArbolDeDecisionTestMother {

    public static final String DEFAULT_INTRODUCTION = "Introducción al árbol de decisión";
    public static final String DEFAULT_DECISION_NAME = "Decisión";
    public static final String DEFAULT_CONTEXT = "Contexto de la decisión";
    public static final String DEFAULT_CONSEQUENCE_NAME = "Consecuencia";
    public static final String MAX_LENGTH_INTRODUCTION = "a".repeat(500);
    public static final String MAX_LENGTH_DECISION_NAME = "a".repeat(200);
    public static final String MAX_LENGTH_CONTEXT = "a".repeat(500);
    public static final Double DEFAULT_INITIAL_BALANCE = 100.0;

    public static ArbolDeDecisionActivityRequestDto arbolDeDecisionRequestDto(
        String introduction,
        List<DecisionArbolDecisionRequestDto> decisionTree
    ) {
        LocalDateTime now = LocalDateTime.now();
        return ArbolDeDecisionActivityRequestDto.builder()
            .description("Descripción del árbol de decisión")
            .startDate(now.plusDays(1))
            .endDate(now.plusDays(7))
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subjectId(ActivityTestMother.SUBJECT_ID)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .introduction(introduction)
            .decisionTree(decisionTree)
            .build();
    }

    public static ArbolDeDecisionActivityRequestDto validArbolDeDecisionRequestDto() {
        return arbolDeDecisionRequestDto(
            DEFAULT_INTRODUCTION,
            List.of(
                decisionWithConsequence("Decisión 1", "Aprobado", true),
                decisionWithConsequence("Decisión 2", "Desaprobado", false)
            )
        );
    }

    public static DecisionArbolDecisionRequestDto decisionWithConsequence(
        String name,
        String consequenceName,
        boolean approvesActivity
    ) {
        return DecisionArbolDecisionRequestDto.builder()
            .name(name)
            .context(DEFAULT_CONTEXT)
            .consecuence(consequence(consequenceName, approvesActivity))
            .options(new ArrayList<>())
            .build();
    }

    public static DecisionArbolDecisionRequestDto decisionWithOptions(String name, String context) {
        return DecisionArbolDecisionRequestDto.builder()
            .name(name)
            .context(context)
            .options(List.of(
                decisionWithConsequence("Opción 1", "Aprobado", true),
                decisionWithConsequence("Opción 2", "Desaprobado", false)
            ))
            .build();
    }

    public static DecisionArbolDecisionRequestDto decisionWithoutOptionsOrConsequence(String name) {
        return DecisionArbolDecisionRequestDto.builder()
            .name(name)
            .context(DEFAULT_CONTEXT)
            .options(new ArrayList<>())
            .consecuence(null)
            .build();
    }

    public static ConsecuenceArbolDecisionRequestDto consequence(String name, boolean approvesActivity) {
        return ConsecuenceArbolDecisionRequestDto.builder()
            .name(name)
            .approvesActivity(approvesActivity)
            .build();
    }

    public static ArbolDeDecisionActivityResponseDto arbolDeDecisionResponseDto(
        Long id,
        String introduction,
        List<?> decisionTree
    ) {
        return ArbolDeDecisionActivityResponseDto.builder()
            .id(id)
            .name("Arbol de decision")
            .description("Descripción del árbol de decisión")
            .introduction(introduction)
            .decisionTree(decisionTree != null ? (List) decisionTree : List.of())
            .build();
    }

    public static ArbolDeDecisionActivityResponseDto validArbolDeDecisionResponseDto(Long id) {
        return arbolDeDecisionResponseDto(id, DEFAULT_INTRODUCTION, List.of());
    }

    public static ArbolDeDecisionActivity savedArbolDeDecision(Long id, Subject subject, String introduction) {
        return ArbolDeDecisionActivity.builder()
            .id(id)
            .name("Arbol de decision")
            .description("Descripción del árbol de decisión")
            .startDate(ActivityTestMother.START_DATE)
            .endDate(ActivityTestMother.END_DATE)
            .difficulty(Difficulty.FACIL)
            .maxTime(30)
            .attempts(3)
            .subject(subject)
            .initialBalance(DEFAULT_INITIAL_BALANCE)
            .actualBalance(0.0)
            .introduction(introduction)
            .decisionTree(List.of())
            .build();
    }

    public static ArbolDeDecisionActivity savedArbolDeDecision(Long id, Subject subject) {
        return savedArbolDeDecision(id, subject, DEFAULT_INTRODUCTION);
    }
}

