package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;

public interface IStockListPaginatedService {
    
    public PaginatedData<StockResponseDto> cu87ListPaginatedStock (
        int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues
    );
    
}
