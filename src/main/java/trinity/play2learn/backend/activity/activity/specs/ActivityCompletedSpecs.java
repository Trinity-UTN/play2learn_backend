package trinity.play2learn.backend.activity.activity.specs;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public class ActivityCompletedSpecs {

    public static Specification<ActivityCompleted> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<ActivityCompleted> filterByTeacher(Teacher teacher) {
        return (root, query, cb) -> cb.equal(root.get("activity").get("subject").get("teacher"), teacher);
    }

    public static Specification<ActivityCompleted> filterByStudent(Student student) {
        return (root, query, cb) -> cb.equal(root.get("student"), student);
    }

    public static Specification<ActivityCompleted> filterByState(ActivityCompletedState state) {
        return (root, query, cb) -> cb.equal(root.get("state"), state);
    }

    public static Specification<ActivityCompleted> activityNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("activity").get("deletedAt"));
    }

    public static Specification<ActivityCompleted> activityBelongsToStudent(Student student) {
        return (root, query, cb) -> {
            Subquery<Long> studentInSubject = query.subquery(Long.class);
            Root<Subject> subjectRoot = studentInSubject.from(Subject.class);
            var students = subjectRoot.join("students");
            studentInSubject.select(cb.literal(1L));
            studentInSubject.where(
                    cb.equal(subjectRoot.get("id"), root.get("activity").get("subject").get("id")),
                    cb.equal(students.get("id"), student.getId()),
                    cb.isNull(subjectRoot.get("deletedAt")));

            return cb.and(
                    cb.isNull(root.get("activity").get("deletedAt")),
                    cb.exists(studentInSubject));
        };
    }

    public static Specification<ActivityCompleted> isLatestCompletionForStudent(Student student) {
        return (root, query, cb) -> {
            Subquery<LocalDateTime> maxCompleted = query.subquery(LocalDateTime.class);
            Root<ActivityCompleted> subRoot = maxCompleted.from(ActivityCompleted.class);
            maxCompleted.select(cb.greatest(subRoot.<LocalDateTime>get("completedAt")));
            maxCompleted.where(
                    cb.equal(subRoot.get("activity"), root.get("activity")),
                    cb.equal(subRoot.get("student"), student));

            return cb.and(
                    cb.equal(root.get("student"), student),
                    cb.equal(root.get("completedAt"), maxCompleted));
        };
    }

    public static Specification<ActivityCompleted> nameContains(String search) {
        return activityNameContains(search);
    }

    public static Specification<ActivityCompleted> activityNameContains(String search) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("activity").get("name")),
                "%" + search.toLowerCase() + "%");
    }

    public static Specification<ActivityCompleted> genericFilter(String field, String value) {
        return activityGenericFilter(field, value);
    }

    public static Specification<ActivityCompleted> activityGenericFilter(String field, String value) {
        switch (field) {
            case "subjectId":
                try {
                    return activityHasSubjectId(Long.valueOf(value));
                } catch (Exception e) {
                    return (root, query, cb) -> cb.conjunction();
                }
            case "status":
                return activityStatusFilter(value);
            case "difficulty":
                return activityDifficultyFilter(value);
            case "courseId":
                try {
                    return activityHasCourseId(Long.valueOf(value));
                } catch (Exception e) {
                    return (root, query, cb) -> cb.conjunction();
                }
            case "yearId":
                try {
                    return activityHasYearId(Long.valueOf(value));
                } catch (Exception e) {
                    return (root, query, cb) -> cb.conjunction();
                }
            default:
                return (root, query, cb) -> {
                    try {
                        return cb.equal(root.get(field), value);
                    } catch (IllegalArgumentException e) {
                        return cb.conjunction();
                    }
                };
        }
    }

    public static Specification<ActivityCompleted> activityHasSubjectId(Long subjectId) {
        return (root, query, cb) -> {
            if (subjectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("activity").get("subject").get("id"), subjectId);
        };
    }

    public static Specification<ActivityCompleted> activityHasCourseId(Long courseId) {
        return (root, query, cb) -> {
            if (courseId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("activity").get("subject").get("course").get("id"), courseId);
        };
    }

    public static Specification<ActivityCompleted> activityHasYearId(Long yearId) {
        return (root, query, cb) -> {
            if (yearId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("activity").get("subject").get("course").get("year").get("id"), yearId);
        };
    }

    public static Specification<ActivityCompleted> activityStatusFilter(String status) {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();
            var activity = root.get("activity");

            return switch (status.toUpperCase()) {
                case "CREATED" -> cb.greaterThan(activity.get("startDate"), now);
                case "PUBLISHED" -> cb.and(
                        cb.lessThanOrEqualTo(activity.get("startDate"), now),
                        cb.greaterThanOrEqualTo(activity.get("endDate"), now));
                case "EXPIRED" -> cb.lessThan(activity.get("endDate"), now);
                default -> cb.conjunction();
            };
        };
    }

    public static Specification<ActivityCompleted> activityDifficultyFilter(String value) {
        try {
            Difficulty difficulty = Difficulty.valueOf(value.toUpperCase());
            return (root, query, cb) -> cb.equal(root.get("activity").get("difficulty"), difficulty);
        } catch (Exception e) {
            return (root, query, cb) -> cb.conjunction();
        }
    }

}
