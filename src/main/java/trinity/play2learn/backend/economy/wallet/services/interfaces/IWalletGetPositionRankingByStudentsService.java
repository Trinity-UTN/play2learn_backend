package trinity.play2learn.backend.economy.wallet.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IWalletGetPositionRankingByStudentsService {
    
    public Long execute(Student targetStudent, List<Student> students);
    
}
