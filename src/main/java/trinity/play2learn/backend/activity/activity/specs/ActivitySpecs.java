package trinity.play2learn.backend.activity.activity.specs;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.Difficulty;
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

}