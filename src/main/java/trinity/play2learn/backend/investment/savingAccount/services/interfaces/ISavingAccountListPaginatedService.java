package trinity.play2learn.backend.investment.savingAccount.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ISavingAccountListPaginatedService {

    public PaginatedData<SavingAccountResponseDto> cu106listPaginatedSavingAccounts (
        int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues,
        User user
    );
    
}
