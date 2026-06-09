package trinity.play2learn.backend.activity.activity.specs;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public class ActivitySpecs {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<Activity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Activity> filterByTeacher(Teacher teacher) {
        return (root, query, cb) -> cb.equal(root.get("subject").get("teacher"), teacher);
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<Activity> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto
    public static Specification<Activity> genericFilter(String field, String value) {

        switch (field) {
            case "subjectId":
                try {
                    return hasSubjectId(Long.valueOf(value));
                } catch (Exception e) { //Evita un 500 si el valor no es un numero
                    return (root, query, cb) -> cb.conjunction();
                }
                
            case "status":
                return statusFilter(value);

            case "difficulty":
                return difficultyFilter(value);
            case "courseId":
                try {
                    return hasCourseId(Long.valueOf(value));
                } catch (Exception e) { //Evita un 500 si el valor no es un numero
                    return (root, query, cb) -> cb.conjunction();
                }
            case "yearId":
                try {
                    return hasYearId(Long.valueOf(value));
                } catch (Exception e) { //Evita un 500 si el valor no es un numero
                    return (root, query, cb) -> cb.conjunction();
                }
        }

        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {

                return cb.conjunction(); // no aplica ningún filtro
            }
        };

    }

    public static Specification<Activity> hasSubjectId(Long subjectId) {
        return (root, query, cb) -> {
            if (subjectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subject").get("id"), subjectId);
        };
    }

    public static Specification<Activity> hasCourseId(Long courseId) {
        return (root, query, cb) -> {
            if (courseId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subject").get("course").get("id"), courseId);
        };
    }

    public static Specification<Activity> hasYearId(Long courseId) {
        return (root, query, cb) -> {
            if (courseId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("subject").get("course").get("year").get("id"), courseId);
        };
    }

    public static Specification<Activity> statusFilter(String status) {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();

            switch (status.toUpperCase()) {
                case "CREATED":
                    return cb.greaterThan(root.get("startDate"), now);

                case "PUBLISHED":
                    return cb.and(
                            cb.lessThanOrEqualTo(root.get("startDate"), now),
                            cb.greaterThanOrEqualTo(root.get("endDate"), now));

                case "EXPIRED":
                    return cb.lessThan(root.get("endDate"), now);

                default:
                    return cb.conjunction(); // si no matchea, no aplica filtro
            }
        };
    }

    public static Specification<Activity> difficultyFilter(String value){
        try {

            Difficulty difficulty = Difficulty.valueOf(value.toUpperCase());
            return (root, query, cb) -> cb.equal(root.get("difficulty"), difficulty);

        } catch (Exception e) {
            return (root, query, cb) -> cb.conjunction();
        }
    }

    public static Specification<Activity> belongsToStudent(Student student) {
        return (root, query, cb) -> {
            Subquery<Long> studentInSubject = query.subquery(Long.class);
            Root<Subject> subjectRoot = studentInSubject.from(Subject.class);
            var students = subjectRoot.join("students");
            studentInSubject.select(cb.literal(1L));
            studentInSubject.where(
                    cb.equal(subjectRoot.get("id"), root.get("subject").get("id")),
                    cb.equal(students.get("id"), student.getId()),
                    cb.isNull(subjectRoot.get("deletedAt")));

            return cb.and(
                    cb.isNull(root.get("deletedAt")),
                    cb.exists(studentInSubject));
        };
    }

    /**
     * Incluye actividades nunca intentadas y aquellas cuyo último intento no es
     * APPROVED ni PENDING.
     */
    public static Specification<Activity> notApprovedNorPendingForStudent(Student student) {
        return (root, query, cb) -> cb.not(cb.exists(latestApprovedOrPendingCompletion(cb, student, root, query)));
    }

    public static Specification<Activity> filterByDisapprovedForStudent(Student student, boolean disapproved) {
        return (root, query, cb) -> {
            Subquery<LocalDateTime> maxCompleted = latestCompletedAtSubquery(cb, student, root, query);

            Subquery<Integer> latestRemaining = query.subquery(Integer.class);
            Root<ActivityCompleted> acLatest = latestRemaining.from(ActivityCompleted.class);
            latestRemaining.select(acLatest.get("remainingAttempts"));
            latestRemaining.where(
                    cb.equal(acLatest.get("activity"), root),
                    cb.equal(acLatest.get("student"), student),
                    cb.equal(acLatest.get("completedAt"), maxCompleted));

            Subquery<Long> hasCompletion = query.subquery(Long.class);
            Root<ActivityCompleted> acExist = hasCompletion.from(ActivityCompleted.class);
            hasCompletion.select(cb.literal(1L));
            hasCompletion.where(
                    cb.equal(acExist.get("activity"), root),
                    cb.equal(acExist.get("student"), student));

            if (disapproved) {
                return cb.and(
                        cb.exists(hasCompletion),
                        cb.equal(latestRemaining, 0));
            }

            return cb.or(
                    cb.not(cb.exists(hasCompletion)),
                    cb.greaterThan(latestRemaining, 0));
        };
    }

    private static Subquery<Long> latestApprovedOrPendingCompletion(CriteriaBuilder cb, Student student,
            Root<Activity> root, CriteriaQuery<?> query) {
        Subquery<Long> latestIsApprovedOrPending = query.subquery(Long.class);
        Root<ActivityCompleted> acLatest = latestIsApprovedOrPending.from(ActivityCompleted.class);
        latestIsApprovedOrPending.select(cb.literal(1L));

        Subquery<LocalDateTime> maxCompleted = latestCompletedAtSubquery(cb, student, root, query);

        latestIsApprovedOrPending.where(
                cb.equal(acLatest.get("activity"), root),
                cb.equal(acLatest.get("student"), student),
                cb.equal(acLatest.get("completedAt"), maxCompleted),
                acLatest.get("state").in(ActivityCompletedState.APPROVED, ActivityCompletedState.PENDING));

        return latestIsApprovedOrPending;
    }

    private static Subquery<LocalDateTime> latestCompletedAtSubquery(CriteriaBuilder cb, Student student,
            Root<Activity> root, CriteriaQuery<?> query) {
        Subquery<LocalDateTime> maxCompleted = query.subquery(LocalDateTime.class);
        Root<ActivityCompleted> acMax = maxCompleted.from(ActivityCompleted.class);
        maxCompleted.select(cb.greatest(acMax.<LocalDateTime>get("completedAt")));
        maxCompleted.where(
                cb.equal(acMax.get("activity"), root),
                cb.equal(acMax.get("student"), student));
        return maxCompleted;
    }

}