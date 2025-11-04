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
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchasePaginatedRepository;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByIdService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitListPurchasesPaginatedService;
import trinity.play2learn.backend.benefits.specs.BenefitPurchasesSpecs;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class BenefitListPurchasesPaginatedService implements IBenefitListPurchasesPaginatedService {

    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IBenefitGetByIdService benefitGetByIdService;
    private final IBenefitPurchasePaginatedRepository benefitPurchasePaginatedRepository;
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<BenefitPurchaseSimpleResponseDto> cu101ListPurchasesPaginated(User user, Long benefitId, int page, int size,
            String orderBy, String orderType, String search, List<String> filters, List<String> filterValues) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        Benefit benefit = benefitGetByIdService.getById(benefitId);

        if (!benefit.getSubject().getTeacher().equals(teacher)) {
            throw new ConflictException(
                    "No se puede obtener las compras de este beneficio ya que no pertenece al docente.");
        }

        List<BenefitPurchase> benefitPurchases = benefitPurchaseRepository.findAllByBenefit(benefit);

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<BenefitPurchase> spec = Specification.where(BenefitPurchasesSpecs.notDeleted());

        //Filtra por nombre completo del estudiante que compro el beneficio
        if (search != null && !search.isBlank()) {
            spec = spec.and(BenefitPurchasesSpecs.studentFullNameContains(search));
        }

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(BenefitPurchasesSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        List<Long> benefitPurchasesIds = benefitPurchases.stream()
                .map(BenefitPurchase::getId)
                .toList();

        if (!benefitPurchasesIds.isEmpty()) {

            spec = spec.and((root, query, cb) -> root.get("id").in(benefitPurchasesIds));

        } else {

            return PaginationHelper.fromPage(Page.empty(pageable), List.of());
        }

        Page<BenefitPurchase> pageResult = benefitPurchasePaginatedRepository.findAll(spec, pageable);

        List<BenefitPurchaseSimpleResponseDto> dtos = BenefitPurchaseMapper.toSimpleDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }

}
