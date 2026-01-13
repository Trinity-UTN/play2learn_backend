package trinity.play2learn.backend.activity.activity.specs;

import org.springframework.data.jpa.domain.Specification;

import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public class ActivityCompletedSpecs {

    // Filtro base: trae solo los que NO fueron eliminados lógicamente
    public static Specification<ActivityCompleted> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<ActivityCompleted> filterByTeacher(Teacher teacher) {
        return (root, query, cb) -> cb.equal(root.get("activity").get("subject").get("teacher"), teacher);
    }

    public static Specification<ActivityCompleted> filterByState(ActivityCompletedState state) {
        return (root, query, cb) -> cb.equal(root.get("state"), state);
    }

    // Filtro por búsqueda textual (ej: nombre LIKE %search%)
    public static Specification<ActivityCompleted> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    // Filtro dinámico: cualquier campo = valor exacto
    public static Specification<ActivityCompleted> genericFilter(String field, String value) {
        return (root, query, cb) -> {
            try {
                // Este get es dinámico, pero puede fallar si el campo no existe
                return cb.equal(root.get(field), value);
            } catch (IllegalArgumentException e) {
                // Esto es útil si querés ignorar filtros inválidos silenciosamente
                return cb.conjunction(); // no aplica ningún filtro
            }
        };

    }

}