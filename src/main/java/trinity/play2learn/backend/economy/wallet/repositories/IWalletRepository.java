package trinity.play2learn.backend.economy.wallet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public interface IWalletRepository extends CrudRepository<Wallet, Long> {
    
    @Query(value = "SELECT * FROM wallet ORDER BY (balance + COALESCE(inverted_balance, 0)) DESC, id ASC LIMIT 10", nativeQuery = true)
    List<Wallet> findTop10OrderByTotalBalanceDesc();
    
    @Query("SELECT w FROM Wallet w WHERE w.student IN :students ORDER BY (w.balance + COALESCE(w.invertedBalance, 0)) DESC, w.id ASC LIMIT 10")
    List<Wallet> findTop10ByStudentsOrderByTotalBalanceDesc(List<Student> students);
    
    @Query("SELECT COUNT(w) + 1 FROM Wallet w WHERE (w.balance + COALESCE(w.invertedBalance, 0)) > (SELECT (w2.balance + COALESCE(w2.invertedBalance, 0)) FROM Wallet w2 WHERE w2.student = :student) OR ((w.balance + COALESCE(w.invertedBalance, 0)) = (SELECT (w2.balance + COALESCE(w2.invertedBalance, 0)) FROM Wallet w2 WHERE w2.student = :student) AND w.id < (SELECT w2.id FROM Wallet w2 WHERE w2.student = :student))")
    Long findPositionByStudent(Student student);
    
    @Query("SELECT COUNT(w) + 1 FROM Wallet w WHERE w.student IN :students AND ((w.balance + COALESCE(w.invertedBalance, 0)) > (SELECT (w2.balance + COALESCE(w2.invertedBalance, 0)) FROM Wallet w2 WHERE w2.student = :targetStudent) OR ((w.balance + COALESCE(w.invertedBalance, 0)) = (SELECT (w2.balance + COALESCE(w2.invertedBalance, 0)) FROM Wallet w2 WHERE w2.student = :targetStudent) AND w.id < (SELECT w2.id FROM Wallet w2 WHERE w2.student = :targetStudent)))")
    Long findPositionByStudentInStudentsList(Student targetStudent, List<Student> students);

    List<Wallet> findAll();

}
