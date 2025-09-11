package trinity.play2learn.backend.activity.activity.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityApprovedListPaginatedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.specs.ActivitySpecs;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityApprovedListPaginatedService implements IActivityApprovedListPaginatedService {

    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetByStudentService activityGetByStudentService;
    private final IActivityFilterApprovedService activityFilterApprovedService;
    private final IActivityPaginatedRepository activityPaginatedRepository;
    private final IActivityCreateApprovedDtosService activityCreateApprovedDtosService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityStudentApprovedResponseDto> cu69ListApprovedActivitiesPaginated(int page, int size,
            String orderBy, String orderType, String search, List<String> filters, List<String> filterValues,
            User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        List<Activity> approvedActivities = activityFilterApprovedService.filterByApproved(activities, student);

        // Creo una lista con los ids de las actividades no aprobadas del estudiante
        List<Long> activityIds = approvedActivities.stream()
                .map(Activity::getId)
                .toList();

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Activity> spec = Specification.where(ActivitySpecs.notDeleted());

        if (search != null && !search.isBlank()) {
            spec = spec.and(ActivitySpecs.nameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String field = filters.get(i);
                String value = filterValues.get(i);

                // Si el filtro es de materia, lo agrego a la spec, sino filtra normalmente
                if ("subjectId".equals(field)) {
                    spec = spec.and(ActivitySpecs.hasSubjectId(Long.valueOf(value)));
                } else {

                    spec = spec.and(ActivitySpecs.genericFilter(field, value));
                }
            }
        }

        // Restricción por estudiante: solo actividades dentro de la lista obtenida
        if (!activityIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("id").in(activityIds));
        } else {
            // Si el estudiante no tiene actividades, devolvemos un Page vacío
            return PaginationHelper.fromPage(Page.empty(pageable), List.of());
        }

        Page<Activity> pageResult = activityPaginatedRepository.findAll(spec, pageable);

        List<ActivityStudentApprovedResponseDto> dtos = activityCreateApprovedDtosService
                .createApprovedDtos(pageResult.getContent(), student);

        return PaginationHelper.fromPage(pageResult, dtos);

    }

}
