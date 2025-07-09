package trinity.play2learn.backend.admin.year.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepositoryPaginated;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearListPaginatedService;
import trinity.play2learn.backend.admin.year.specs.YearSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class YearListPaginatedService implements IYearListPaginatedService {

    private final IYearRepositoryPaginated yearRepository;

    @Override
    public PaginatedData<YearResponseDto> cu12PaginatedListYears(
        int page, 
        int size, 
        String orderBy, 
        String orderType,
        String search, 
        List<String> filters, 
        List<String> filterValues) {
        
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Year> spec = Specification.where(YearSpecs.notDeleted());
        
        if (search != null && !search.isBlank()) {
        spec = spec.and(YearSpecs.nameContains(search));
        }
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(YearSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<Year> pageResult = yearRepository.findAll(spec, pageable);

        List<YearResponseDto> dtos = YearMapper.toListDto(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
