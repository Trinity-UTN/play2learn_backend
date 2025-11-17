package trinity.play2learn.backend.investment.stock.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListPaginatedService;
import trinity.play2learn.backend.investment.stock.specs.StockSpecs;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class StockListPaginatedService implements IStockListPaginatedService {

    private final IStockRepository stockRepository;
    
    @Override
    public PaginatedData<StockResponseDto> cu87ListPaginatedStock(
        int page, 
        int size, 
        String orderBy, 
        String orderType,
        String search, 
        List<String> filters, 
        List<String> filterValues
    ) {
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Stock> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
        spec = spec.and(StockSpecs.nameContains(search));
        }
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(StockSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<Stock> pageResult = stockRepository.findAll(spec, pageable);

        List<StockResponseDto> dtos = StockMapper.toDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
