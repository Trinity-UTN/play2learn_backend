package trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;

public interface IFixedTermDepositListPaginatedService {
    
    public PaginatedData<FixedTermDepositResponseDto> cu99ListPaginatedFixedTermDeposits (
        int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues
    );

}
