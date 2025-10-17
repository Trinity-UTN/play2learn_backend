package trinity.play2learn.backend.benefits.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitPaginatedRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListByTeacherPaginatedService;
import trinity.play2learn.backend.benefits.specs.BenefitSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BenefitListByTeacherPaginatedService implements IBenefitListByTeacherPaginatedService {

    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IBenefitPaginatedRepository benefitRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<BenefitResponseDto> cu56ListBenefitsPaginated(
            User user,
            int page,
            int size,
            String orderBy,
            String orderType,
            String search,
            List<String> filters,
            List<String> filterValues) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        Specification<Benefit> spec = Specification.where(BenefitSpecs.notDeleted());

        if (search != null && !search.isBlank()) {
            spec = spec.and(BenefitSpecs.nameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(BenefitSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        //Filtro por docente
        spec = spec.and((root, query, cb) -> cb.equal(root.get("subject").get("teacher"), teacher));

        Page<Benefit> pageResult = benefitRepository.findAll(spec, pageable);

        List<BenefitResponseDto> dtos = BenefitMapper.toListDto(pageResult.getContent());
        
        return PaginationHelper.fromPage(pageResult , dtos);
    }
}
