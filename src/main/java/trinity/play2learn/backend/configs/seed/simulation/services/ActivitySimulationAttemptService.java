package trinity.play2learn.backend.configs.seed.simulation.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.seed.simulation.collector.SimulationDataCollector;
import trinity.play2learn.backend.configs.seed.simulation.config.SimulationProperties;
import trinity.play2learn.backend.configs.seed.simulation.services.internal.SimulationActivityCompletionService;
import trinity.play2learn.backend.configs.seed.simulation.utils.SimulationTimeline;

/**
 * Simula intentos de estudiantes con distribución aleatoria de aprobación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivitySimulationAttemptService {

    private final SimulationProperties properties;
    private final ISubjectRepository subjectRepository;
    private final IActivityRepository activityRepository;
    private final SimulationActivityCompletionService completionService;

    @Transactional
    public void simulate(SimulationTimeline timeline, SimulationDataCollector collector) {
        for (Long activityId : collector.getActivityIds()) {
            Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Actividad no encontrada: " + activityId));
            Subject subject = activity.getSubject();
            List<Student> students = subjectRepository.findStudentsBySubjectId(subject.getId());
            if (students.isEmpty()) {
                continue;
            }

            List<Student> participants = selectParticipants(students, timeline);
            for (Student student : participants) {
                simulateStudentAttempts(activity, student, timeline, collector);
            }
        }
    }

    private List<Student> selectParticipants(List<Student> students, SimulationTimeline timeline) {
        List<Student> shuffled = new ArrayList<>(students);
        Collections.shuffle(shuffled, timeline.random());
        int count = Math.max(1, (int) Math.round(students.size() * properties.getStudentsPerActivityRate()));
        count = Math.min(count, shuffled.size());
        return shuffled.subList(0, count);
    }

    private void simulateStudentAttempts(
        Activity activity,
        Student student,
        SimulationTimeline timeline,
        SimulationDataCollector collector
    ) {
        int maxAttempts = Math.max(1, activity.getAttempts());
        int passOnAttempt = resolvePassAttempt(timeline);
        LocalDateTime cursor = timeline.nextAfter(activity.getStartDate(), activity.getEndDate());

        for (int attemptNum = 1; attemptNum <= maxAttempts; attemptNum++) {
            SimulationTimeline.AttemptWindow window = timeline.resolveAttemptWindow(
                activity.getStartDate(), activity.getEndDate(), cursor
            );
            LocalDateTime startedAt = window.startedAt();
            LocalDateTime completedAt = window.completedAt();

            int remaining = maxAttempts - attemptNum + 1;
            ActivityCompleted attempt = completionService.startAttempt(activity, student, remaining, startedAt);
            collector.incrementAttempts();

            boolean isLastAttempt = attemptNum == maxAttempts;
            boolean shouldApprove = passOnAttempt > 0 && attemptNum == passOnAttempt;

            if (shouldApprove) {
                completionService.completeApproved(attempt, completedAt);
                collector.incrementApproved();
                return;
            }

            completionService.completeDisapproved(attempt, completedAt);
            collector.incrementDisapproved();

            if (passOnAttempt < 0 && isLastAttempt) {
                return;
            }

            cursor = timeline.nextAfter(completedAt, activity.getEndDate());
        }
    }

    /**
     * @return 1..maxAttempts para aprobar en ese intento, -1 para fallar todos
     */
    int resolvePassAttempt(SimulationTimeline timeline) {
        double roll = timeline.random().nextDouble();
        double t1 = properties.getPassOnAttempt1Rate();
        double t2 = t1 + properties.getPassOnAttempt2Rate();
        double t3 = t2 + properties.getPassOnAttempt3Rate();

        if (roll < t1) {
            return 1;
        }
        if (roll < t2) {
            return 2;
        }
        if (roll < t3) {
            return 3;
        }
        return -1;
    }
}
