package trinity.play2learn.backend.economy.wallet.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetPositionRankingByStudentsService;
import trinity.play2learn.backend.economy.wallet.repositories.IWalletRepository;
import trinity.play2learn.backend.admin.student.models.Student;
import java.util.List;

@Service
@AllArgsConstructor
public class WalletGetPositionRankingByStudentsService implements IWalletGetPositionRankingByStudentsService {
    
    private final IWalletRepository walletRepository;

    @Override
    public Long execute(Student targetStudent, List<Student> students) {
        return walletRepository.findPositionByStudentInStudentsList(targetStudent, students);
    }
    
}
