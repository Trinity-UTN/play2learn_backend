package trinity.play2learn.backend.configs.seed.simulation.services.internal;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.levels.ValueXp;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationDateValidator;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUpdateLevelService;

/**
 * Completa intentos simulados con timestamps explícitos, sin notificaciones.
 */
@Service
@RequiredArgsConstructor
public class SimulationActivityCompletionService {

    private static final int APPROVED_SCORE = 75;
    private static final int DISAPPROVED_SCORE = 40;

    private final IActivityCompletedRepository activityCompletedRepository;
    private final IActivityRepository activityRepository;
    private final IStudentRepository studentRepository;
    private final ITransactionGenerateService transactionGenerateService;
    private final IProfileUpdateLevelService profileUpdateLevelService;
    private final ISubjectRepository subjectRepository;

    public ActivityCompleted startAttempt(
        Activity activity,
        Student student,
        int remainingAttempts,
        LocalDateTime startedAt
    ) {
        Activity managedActivity = activityRepository.findById(activity.getId())
            .orElseThrow(() -> new NotFoundException("Actividad no encontrada: " + activity.getId()));
        Student managedStudent = studentRepository.findById(student.getId())
            .orElseThrow(() -> new NotFoundException("Estudiante no encontrado: " + student.getId()));

        SimulationDateValidator.validateAttemptDates(managedActivity.getStartDate(), startedAt, startedAt.plusMinutes(1));

        ActivityCompleted attempt = ActivityCompleted.builder()
            .activity(managedActivity)
            .student(managedStudent)
            .remainingAttempts(remainingAttempts)
            .state(ActivityCompletedState.IN_PROGRESS)
            .score(0)
            .correctAnswers(0)
            .incorrectAnswers(0)
            .unanswered(0)
            .build();

        attempt = activityCompletedRepository.save(attempt);
        attempt.setStartedAt(startedAt);
        return activityCompletedRepository.save(attempt);
    }

    public ActivityCompleted completeApproved(ActivityCompleted attempt, LocalDateTime completedAt) {
        attempt.setScore(APPROVED_SCORE);
        attempt.setCorrectAnswers(4);
        attempt.setIncorrectAnswers(1);
        attempt.setUnanswered(0);
        attempt.setState(ActivityCompletedState.APPROVED);
        attempt.setCompletedAt(completedAt);
        SimulationDateValidator.validateAttemptDates(
            attempt.getActivity().getStartDate(), attempt.getStartedAt(), completedAt
        );

        Double reward = calculateSimulationReward(attempt.getActivity());
        attempt.setReward(reward);

        transactionGenerateService.generate(
            TypeTransaction.RECOMPENSA,
            reward,
            "Recompensa simulada por actividad",
            TransactionActor.SISTEMA,
            TransactionActor.ESTUDIANTE,
            attempt.getStudent().getWallet(),
            null,
            attempt.getActivity(),
            null,
            null,
            null,
            null
        );

        applyXp(attempt.getActivity(), attempt.getStudent());
        return activityCompletedRepository.save(attempt);
    }

    public ActivityCompleted completeDisapproved(ActivityCompleted attempt, LocalDateTime completedAt) {
        attempt.setScore(DISAPPROVED_SCORE);
        attempt.setCorrectAnswers(1);
        attempt.setIncorrectAnswers(3);
        attempt.setUnanswered(1);
        attempt.setState(ActivityCompletedState.DISAPPROVED);
        attempt.setReward(0.0);
        attempt.setRemainingAttempts(Math.max(0, attempt.getRemainingAttempts() - 1));
        attempt.setCompletedAt(completedAt);
        SimulationDateValidator.validateAttemptDates(
            attempt.getActivity().getStartDate(), attempt.getStartedAt(), completedAt
        );
        return activityCompletedRepository.save(attempt);
    }

    private void applyXp(Activity activity, Student student) {
        ValueXp xp = switch (activity.getDifficulty() != null ? activity.getDifficulty() : Difficulty.FACIL) {
            case FACIL -> ValueXp.ACTIVITY_FACIL;
            case MEDIO -> ValueXp.ACTIVITY_MEDIO;
            case DIFICIL -> ValueXp.ACTIVITY_DIFICIL;
        };
        profileUpdateLevelService.execute(student.getProfile(), xp.getValue());
    }

    private Double calculateSimulationReward(Activity activity) {
        int studentCount = Math.max(1, subjectRepository.findStudentsBySubjectId(activity.getSubject().getId()).size());
        TypeReward typeReward = activity.getTypeReward() != null ? activity.getTypeReward() : TypeReward.EQUITATIVO;
        if (typeReward == TypeReward.EQUITATIVO) {
            return activity.getInitialBalance() / studentCount;
        }
        return activity.getInitialBalance() / studentCount;
    }
}
