package trinity.play2learn.backend.investment.savingAccount.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.investment.savingAccount.models.SavingAccount;
import trinity.play2learn.backend.investment.savingAccount.repositories.ISavingAccountRepository;
import trinity.play2learn.backend.investment.savingAccount.services.interfaces.ISavingAccountFindByIdService;

@Service
@AllArgsConstructor
public class SavingAccountFindByIdService implements ISavingAccountFindByIdService{
    
    private final ISavingAccountRepository savingAccountRepository;

    @Override
    public SavingAccount execute(Long id) {
        return savingAccountRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
            () -> new NotFoundException("No existe una caja de ahorro con el id proporcionado")
        );
    }
    
}
