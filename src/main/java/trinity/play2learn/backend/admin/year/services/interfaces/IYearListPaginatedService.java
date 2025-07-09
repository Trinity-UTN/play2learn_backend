package trinity.play2learn.backend.admin.year.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.configs.response.PaginatedData;

public interface IYearListPaginatedService {
    
    public PaginatedData<YearResponseDto> cu12PaginatedListYears (
        int page,
        int size,
        String orderBy,
        String orderType,
        String search,
        List<String> filters,
        List<String> filterValues
    );

}
