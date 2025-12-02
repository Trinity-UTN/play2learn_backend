package trinity.play2learn.backend.economy.wallet.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface IWalletGetRankingByStudentsService {
    
    List<Wallet> execute(List<Student> students);
    
}
