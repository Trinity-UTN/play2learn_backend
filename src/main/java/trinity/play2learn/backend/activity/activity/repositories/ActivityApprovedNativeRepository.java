package trinity.play2learn.backend.activity.activity.repositories;

import static trinity.play2learn.backend.activity.activity.repositories.ActivityStudentNativeQuerySupport.LATEST_COMPLETION_CTE;
import static trinity.play2learn.backend.activity.activity.repositories.ActivityStudentNativeQuerySupport.STATE_APPROVED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import trinity.play2learn.backend.admin.student.models.Student;

@Repository
public class ActivityApprovedNativeRepository {

    private static final Set<String> ALLOWED_ORDER_COLUMNS = Set.of(
            "id", "name", "startdate", "enddate", "difficulty", "createdat");

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Long> findApprovedCompletionIds(
            Student student,
            Pageable pageable,
            String search,
            List<String> filters,
            List<String> filterValues) {

        Map<String, Object> params = new HashMap<>();
        params.put("studentId", student.getId());

        StringBuilder where = new StringBuilder("""
                FROM activity_completed ac
                INNER JOIN latest_completion lc ON lc.completion_id = ac.id AND lc.state = %d
                INNER JOIN activity a ON a.id = ac.activity_id AND a.deleted_at IS NULL
                INNER JOIN subjects s ON s.id = a.subject_id AND s.deleted_at IS NULL
                INNER JOIN subject_students ss ON ss.subject_id = s.id AND ss.student_id = :studentId
                WHERE ac.student_id = :studentId
                """.formatted(STATE_APPROVED));

        if (search != null && !search.isBlank()) {
            where.append(" AND LOWER(a.name) LIKE :search");
            params.put("search", "%" + search.toLowerCase() + "%");
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                appendFilter(where, params, filters.get(i), filterValues.get(i));
            }
        }

        String orderClause = buildOrderClause(pageable);
        String fromWhere = where.toString();

        String countSql = LATEST_COMPLETION_CTE + "SELECT COUNT(ac.id) " + fromWhere;
        Query countQuery = entityManager.createNativeQuery(countSql);
        params.forEach(countQuery::setParameter);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        if (total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        String dataSql = LATEST_COMPLETION_CTE + "SELECT ac.id " + fromWhere + orderClause;
        Query dataQuery = entityManager.createNativeQuery(dataSql);
        params.forEach(dataQuery::setParameter);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Long> ids = ((List<Number>) dataQuery.getResultList()).stream()
                .map(Number::longValue)
                .toList();

        return new PageImpl<>(ids, pageable, total);
    }

    private void appendFilter(StringBuilder where, Map<String, Object> params, String field, String value) {
        switch (field) {
            case "status" -> appendStatusFilter(where, value);
            case "subjectId" -> appendLongFilter(where, params, "subjectId", value,
                    " AND a.subject_id = :subjectId");
            case "difficulty" -> {
                where.append(" AND a.difficulty = :difficulty");
                params.put("difficulty", value.toUpperCase());
            }
            case "courseId" -> appendLongFilter(where, params, "courseId", value, """
                     AND EXISTS (
                        SELECT 1 FROM subjects s2
                        WHERE s2.id = a.subject_id AND s2.course_id = :courseId
                     )
                    """);
            case "yearId" -> appendLongFilter(where, params, "yearId", value, """
                     AND EXISTS (
                        SELECT 1 FROM subjects s2
                        INNER JOIN courses c ON c.id = s2.course_id
                        WHERE s2.id = a.subject_id AND c.year_id = :yearId
                     )
                    """);
            default -> {
                // filtros desconocidos se ignoran
            }
        }
    }

    private void appendLongFilter(StringBuilder where, Map<String, Object> params, String paramName, String value,
            String sql) {
        try {
            where.append(sql);
            params.put(paramName, Long.valueOf(value));
        } catch (NumberFormatException ignored) {
            // valor inválido: se ignora el filtro
        }
    }

    private void appendStatusFilter(StringBuilder where, String status) {
        switch (status.toUpperCase()) {
            case "CREATED" -> where.append(" AND a.start_date > CURRENT_TIMESTAMP");
            case "PUBLISHED" -> where.append(
                    " AND a.start_date <= CURRENT_TIMESTAMP AND a.end_date >= CURRENT_TIMESTAMP");
            case "EXPIRED" -> where.append(" AND a.end_date < CURRENT_TIMESTAMP");
            default -> {
                // sin filtro
            }
        }
    }

    private String buildOrderClause(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            return " ORDER BY a.id ASC";
        }

        StringBuilder order = new StringBuilder(" ORDER BY ");
        boolean first = true;
        for (Sort.Order sortOrder : sort) {
            if (!first) {
                order.append(", ");
            }
            first = false;
            String column = mapOrderColumn(sortOrder.getProperty());
            order.append(column).append(sortOrder.isAscending() ? " ASC" : " DESC");
        }
        return order.toString();
    }

    private String mapOrderColumn(String property) {
        String normalized = property.replace("_", "").toLowerCase();
        if (!ALLOWED_ORDER_COLUMNS.contains(normalized)) {
            return "a.id";
        }

        return switch (normalized) {
            case "name" -> "a.name";
            case "startdate" -> "a.start_date";
            case "enddate" -> "a.end_date";
            case "difficulty" -> "a.difficulty";
            case "createdat" -> "a.created_at";
            default -> "a.id";
        };
    }
}
