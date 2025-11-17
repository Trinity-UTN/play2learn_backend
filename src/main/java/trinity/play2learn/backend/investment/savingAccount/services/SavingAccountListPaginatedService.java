package trinity.play2learn.backend.investment.savingAccount.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.savingAccount.dtos.response.SavingAccountResponseDto;
import trinity.play2learn.backend.investment.savingAccount.mappers.SavingAccountMapper;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountListPaginatedService;
import trinity.play2learn.backend.investment.savingAccount.specs.SavingAccountSpecs;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User; 
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class SavingAccountListPaginatedService implements ISavingAccountListPaginatedService {
    
    private final ISavingAccountRepository savingAccountRepository;

    private final IStudentGetByEmailService studentGetByEmailService;

    @Override
    public PaginatedData<SavingAccountResponseDto> cu106listPaginatedSavingAccounts(
        int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues,
        User user
    ) {

        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<SavingAccount> spec = Specification.where(SavingAccountSpecs.notDeleted()).and(SavingAccountSpecs.hasWallet(wallet));

        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(SavingAccountSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<SavingAccount> pageResult = savingAccountRepository.findAll(spec, pageable);

        List<SavingAccountResponseDto> dtos = SavingAccountMapper.toDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
