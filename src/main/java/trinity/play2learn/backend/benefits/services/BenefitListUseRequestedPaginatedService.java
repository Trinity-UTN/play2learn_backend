package trinity.play2learn.backend.benefits.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchasePaginatedRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUseRequestedPaginatedService;
import trinity.play2learn.backend.benefits.specs.BenefitPurchasesSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class BenefitListUseRequestedPaginatedService implements IBenefitListUseRequestedPaginatedService {

    private final IBenefitPurchasePaginatedRepository benefitPurchasePaginatedRepository;
    private final ITeacherGetByEmailService teacherGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<BenefitPurchaseSimpleResponseDto> cu108ListUseRequestedPaginated(User user, int page, int size,
            String orderBy, String orderType, String search, List<String> filters, List<String> filterValues) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        Specification<BenefitPurchase> spec = Specification.where(BenefitPurchasesSpecs.notDeleted());
        spec = spec.and(BenefitPurchasesSpecs.notExpired());

        //Filtro por docente
        spec = spec.and((root, query, cb) -> cb.equal(root.get("benefit").get("subject").get("teacher"), teacher));
        //Filtro por uso solicitado
        spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), BenefitPurchaseState.USE_REQUESTED));
        
        //Filtra por nombre completo del estudiante que compro el beneficio
        if (search != null && !search.isBlank()) {
            spec = spec.and(BenefitPurchasesSpecs.studentFullNameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(BenefitPurchasesSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<BenefitPurchase> pageResult = benefitPurchasePaginatedRepository.findAll(spec, pageable);
                
        List<BenefitPurchaseSimpleResponseDto> dtos = BenefitPurchaseMapper.toSimpleDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }

}
