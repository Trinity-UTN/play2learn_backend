package trinity.play2learn.backend.economy.wallet.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetRankingByStudentsService;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class WalletGetRankingByStudentsService implements IWalletGetRankingByStudentsService {

    private final IWalletRepository walletRepository;

    @Override
    public List<Wallet> execute(List<Student> students) {
        return walletRepository.findTop10ByStudentsOrderByTotalBalanceDesc(students);
    }
    
}
