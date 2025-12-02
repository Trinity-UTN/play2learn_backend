package trinity.play2learn.backend.economy.wallet.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetPositionRankingService;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class WalletGetPositionRankingService implements IWalletGetPositionRankingService {
    
    private final IWalletRepository walletRepository;

    @Override
    public Long execute(Student student) {
        return walletRepository.findPositionByStudent(student);
    }
    
}
