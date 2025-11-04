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
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitPurchaseMapper;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchasePaginatedRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListUsedByStudentPaginatedService;
import trinity.play2learn.backend.benefits.specs.BenefitPurchasesSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class BenefitListUsedByStudentPaginatedService implements IBenefitListUsedByStudentPaginatedService {
    
    private final IBenefitPurchasePaginatedRepository benefitPurchasePaginatedRepository;
    private final IStudentGetByEmailService studentGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<BenefitPurchasedUsedResponseDto> cu109ListUsedPaginated(User user, int page, int size,
            String orderBy, String orderType, String search, List<String> filters, List<String> filterValues) {
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        Specification<BenefitPurchase> spec = Specification.where(BenefitPurchasesSpecs.notDeleted());

        //Filtro por estudiante
        spec = spec.and((root, query, cb) -> cb.equal(root.get("student"), student));

        //Filtro por usado 
        spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), BenefitPurchaseState.USED));

        //Filtra por nombre del beneficio
        if (search != null && !search.isBlank()) {
            spec = spec.and(BenefitPurchasesSpecs.benefitNameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(BenefitPurchasesSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<BenefitPurchase> pageResult = benefitPurchasePaginatedRepository.findAll(spec, pageable);
                
        List<BenefitPurchasedUsedResponseDto> dtos = BenefitPurchaseMapper.toUsedDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
    
}
