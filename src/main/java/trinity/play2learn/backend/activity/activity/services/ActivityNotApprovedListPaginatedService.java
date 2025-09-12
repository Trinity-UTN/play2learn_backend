package trinity.play2learn.backend.activity.activity.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityPaginatedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterNotApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityNotApprovedListPaginatedService;
import trinity.play2learn.backend.activity.activity.specs.ActivitySpecs;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class ActivityNotApprovedListPaginatedService implements IActivityNotApprovedListPaginatedService {

    private final IActivityPaginatedRepository activityRepository;
    private final IActivityCreateNotApprovedDtosService activityCreateNotApprovedDtosService;
    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetByStudentService activityGetByStudentService;
    private final IActivityFilterNotApprovedService activityFilterNotApprovedService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<ActivityStudentNotApprovedResponseDto> cu66listNotApprovedActivitiesPaginated(
            int page,
            int size,
            String orderBy,
            String orderType,
            String search,
            List<String> filters,
            List<String> filterValues,
            User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        // Obtengo todas las actividades del estudiante segun las materias a las que
        // esta asignado
        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        // Filtro por las no aprobadas
        List<Activity> notApprovedActivities = activityFilterNotApprovedService.filterByNotApproved(activities,
                student);

        // Creo una lista con los ids de las actividades no aprobadas del estudiante
        List<Long> activityIds = notApprovedActivities.stream()
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
                spec = spec.and(ActivitySpecs.genericFilter(field, value));
            }
        }

        // Restricción por estudiante: solo actividades dentro de la lista obtenida
        if (!activityIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("id").in(activityIds));
        } else {
            // Si el estudiante no tiene actividades, devolvemos un Page vacío
            return PaginationHelper.fromPage(Page.empty(pageable), List.of());
        }

        Page<Activity> pageResult = activityRepository.findAll(spec, pageable);

        List<ActivityStudentNotApprovedResponseDto> dtos = activityCreateNotApprovedDtosService
                .createNotApprovedDtos(pageResult.getContent(), student);

        return PaginationHelper.fromPage(pageResult, dtos);
    }

}
