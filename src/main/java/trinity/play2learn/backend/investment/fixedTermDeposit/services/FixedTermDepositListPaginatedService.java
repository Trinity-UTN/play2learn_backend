package trinity.play2learn.backend.investment.fixedTermDeposit.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositListPaginatedService;
import trinity.play2learn.backend.investment.fixedTermDeposit.specs.FixedTermDepositSpecs;
import trinity.play2learn.backend.utils.PaginatorUtils;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.investment.fixedTermDeposit.mappers.FixedTermDepositMapper;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;

@Service
@AllArgsConstructor
public class FixedTermDepositListPaginatedService implements IFixedTermDepositListPaginatedService {
    
    private final IFixedTermDepositRepository fixedTermDepositRepository;
    
    @Override
    public PaginatedData<FixedTermDepositResponseDto> cu99ListPaginatedFixedTermDeposits (
        int page, 
        int size, 
        String orderBy, 
        String orderType,
        String search, 
        List<String> filters, 
        List<String> filterValues
    ) {
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<FixedTermDeposit> spec = Specification.where(null);

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String campo = filters.get(i);
                String valor = filterValues.get(i);

                switch (campo) {
                    case "fixedTermState":
                        spec = spec.and(FixedTermDepositSpecs.hasState(valor));
                        break;
                    case "fixedTermDays":
                        spec = spec.and(FixedTermDepositSpecs.hasDays(valor));
                        break;
                    default:
                        spec = spec.and(FixedTermDepositSpecs.genericFilter(campo, valor));
                        break;
                }
            }
        }

        Page<FixedTermDeposit> pageResult = fixedTermDepositRepository.findAll(spec, pageable);

        List<FixedTermDepositResponseDto> dtos = FixedTermDepositMapper.toDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
