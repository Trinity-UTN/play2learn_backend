package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPaginatedRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitCreateStudentDtosService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterByStudentStateService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByStudentPaginatedService;
import trinity.play2learn.backend.benefits.specs.BenefitSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class BenefitListByStudentPaginatedService implements IBenefitListByStudentPaginatedService {

    private final IStudentGetByEmailService studentGetByEmailService;
    private final IBenefitGetByStudentService benefitGetByStudentService;
    private final IBenefitFilterByStudentStateService benefitFilterByStudentStateService;
    private final IBenefitPaginatedRepository benefitRepository;
    private final IBenefitCreateStudentDtosService benefitCreateStudentDtos;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<BenefitStudentResponseDto> cu80ListBenefitsByStudentPaginated(User user, int page, int size, String orderBy,
            String orderType, String search, List<String> filters, List<String> filterValues) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());
        
        List<Benefit> benefits = benefitGetByStudentService.getByStudent(student);

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Benefit> spec = Specification.where(BenefitSpecs.notDeleted());

        if (search != null && !search.isBlank()) {
            spec = spec.and(BenefitSpecs.nameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String field = filters.get(i);
                String value = filterValues.get(i);

                if (field.equals("state")) {

                    try {

                        BenefitStudentState benefitStudentState = BenefitStudentState.valueOf(value.toUpperCase());

                        benefits = benefitFilterByStudentStateService.filterByStudentState(benefits, student, benefitStudentState);

                    } catch (Exception e) {
                        continue;
                    }

                }else{
                    spec = spec.and(BenefitSpecs.genericFilter(field, value));
                }

            }
        }

        List<Long> benefitIds = benefits.stream()
                .map(Benefit::getId)
                .toList();

        if (!benefitIds.isEmpty()) {

            spec = spec.and((root, query, cb) -> root.get("id").in(benefitIds));

        } else {

            return PaginationHelper.fromPage(Page.empty(pageable), List.of());
        }

        Page<Benefit> pageResult = benefitRepository.findAll(spec, pageable);

        List<BenefitStudentResponseDto> benefitsDtos = benefitCreateStudentDtos.createBenefitStudentDtos(pageResult.getContent(), student);

        return PaginationHelper.fromPage(pageResult, benefitsDtos);

    }

}
