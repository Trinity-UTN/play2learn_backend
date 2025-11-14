package trinity.play2learn.backend.activity.activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.activity.ahorcado.models.Errors;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivityTestMother {

    public static final Long ACTIVITY_ID = 100L;
    public static final Long STUDENT_ID = 200L;
    public static final Long SUBJECT_ID = 300L;
    public static final String STUDENT_EMAIL = "student@example.com";
    public static final LocalDateTime START_DATE = LocalDateTime.now().plusDays(1);
    public static final LocalDateTime END_DATE = LocalDateTime.now().plusDays(7);
    public static final int MAX_TIME = 30;
    public static final int ATTEMPTS = 3;

    public static Ahorcado ahorcadoActivity(Long id) {
        Subject subject = Subject.builder()
            .id(SUBJECT_ID)
            .name("Matemáticas")
            .build();

        return Ahorcado.builder()
            .id(id)
            .name("Ahorcado")
            .description("Adivina la palabra")
            .startDate(START_DATE)
            .endDate(END_DATE)
            .createdAt(LocalDateTime.now())
            .difficulty(Difficulty.FACIL)
            .maxTime(MAX_TIME)
            .attempts(ATTEMPTS)
            .subject(subject)
            .actualBalance(100.0)
            .initialBalance(100.0)
            .word("palabra")
            .errorsPermited(Errors.TRES)
            .build();
    }

    public static Activity ahorcadoActivity(Long id, Subject subject) {
        return Ahorcado.builder()
            .id(id)
            .name("Ahorcado")
            .description("Adivina la palabra")
            .startDate(START_DATE)
            .endDate(END_DATE)
            .createdAt(LocalDateTime.now())
            .difficulty(Difficulty.FACIL)
            .maxTime(MAX_TIME)
            .attempts(ATTEMPTS)
            .subject(subject)
            .actualBalance(100.0)
            .initialBalance(100.0)
            .word("palabra")
            .errorsPermited(Errors.TRES)
            .build();
    }

    public static Student student(Long id, String email) {
        User user = User.builder()
            .id(id + 1000)
            .email(email)
            .password("hashed-password")
            .role(Role.ROLE_STUDENT)
            .build();

        return Student.builder()
            .id(id)
            .name("Juan")
            .lastname("Pérez")
            .dni("12345678")
            .user(user)
            .build();
    }

    public static User studentUser(Long id, String email) {
        return User.builder()
            .id(id)
            .email(email)
            .password("hashed-password")
            .role(Role.ROLE_STUDENT)
            .build();
    }

    public static ActivityCompletedRequestDto activityCompletedRequestDto(Long activityId, ActivityCompletedState state) {
        return ActivityCompletedRequestDto.builder()
            .activityId(activityId)
            .state(state)
            .build();
    }

    public static ActivityCompletedResponseDto activityCompletedResponseDto(
        Long id,
        Long activityId,
        ActivityCompletedState state,
        Double reward,
        Integer remainingAttempts
    ) {
        return ActivityCompletedResponseDto.builder()
            .id(id)
            .activityId(activityId)
            .state(state)
            .reward(reward)
            .remainingAttempts(remainingAttempts)
            .build();
    }

    public static ActivityCompleted activityCompleted(
        Long id,
        Activity activity,
        Student student,
        ActivityCompletedState state,
        Integer remainingAttempts
    ) {
        LocalDateTime startedAt = state == ActivityCompletedState.IN_PROGRESS
            ? LocalDateTime.now()
            : LocalDateTime.now().minusMinutes(5);

        ActivityCompleted activityCompleted = ActivityCompleted.builder()
            .id(id)
            .activity(activity)
            .student(student)
            .state(state)
            .remainingAttempts(remainingAttempts)
            .startedAt(startedAt)
            .build();

        if (state != ActivityCompletedState.IN_PROGRESS) {
            activityCompleted.setCompletedAt(LocalDateTime.now());
        }

        return activityCompleted;
    }

    public static Subject subject(Long id, String name) {
        return Subject.builder()
            .id(id)
            .name(name)
            .build();
    }

    // Helpers para tests de balance
    public static Activity activityWithBalance(Long id, Double balance) {
        Activity activity = ahorcadoActivity(id);
        activity.setActualBalance(balance);
        return activity;
    }

    // Helpers para tests de completado
    public static ActivityCompletedRequestDto approvedRequest(Long activityId) {
        return activityCompletedRequestDto(activityId, ActivityCompletedState.APPROVED);
    }

    public static ActivityCompletedRequestDto disapprovedRequest(Long activityId) {
        return activityCompletedRequestDto(activityId, ActivityCompletedState.DISAPPROVED);
    }

    // Helpers para tests de listado
    public static List<Activity> activitiesList(int count) {
        List<Activity> activities = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            activities.add(ahorcadoActivity((long) i));
        }
        return activities;
    }

    // Helpers para tests de Ahorcado
    public static Subject subjectWithTeacher(Long subjectId, String subjectName, trinity.play2learn.backend.admin.teacher.models.Teacher teacher) {
        trinity.play2learn.backend.admin.year.models.Year year = trinity.play2learn.backend.admin.year.models.Year.builder()
            .id(1L)
            .name("2024")
            .build();
        trinity.play2learn.backend.admin.course.models.Course course = trinity.play2learn.backend.admin.course.models.Course.builder()
            .id(1L)
            .name("Primero A")
            .year(year)
            .build();
        return Subject.builder()
            .id(subjectId)
            .name(subjectName)
            .teacher(teacher)
            .course(course)
            .optional(false)
            .build();
    }

    public static trinity.play2learn.backend.admin.teacher.models.Teacher teacher(Long id, String email) {
        User user = User.builder()
            .id(id + 1000)
            .email(email)
            .password("hashed-password")
            .role(Role.ROLE_TEACHER)
            .build();

        return trinity.play2learn.backend.admin.teacher.models.Teacher.builder()
            .id(id)
            .name("Carlos")
            .lastname("Gómez")
            .dni("87654321")
            .user(user)
            .build();
    }

    public static User teacherUser(Long id, String email) {
        return User.builder()
            .id(id)
            .email(email)
            .password("hashed-password")
            .role(Role.ROLE_TEACHER)
            .build();
    }
}

