package trinity.play2learn.backend.economy.wallet.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGetLastTransaccionsService;
import trinity.play2learn.backend.economy.wallet.dtos.response.MovementResponseDto;
import trinity.play2learn.backend.economy.wallet.mappers.MovementMapper;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetLastMovementsService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class WalletGetLastMovementsService implements IWalletGetLastMovementsService {
    
    private final IStudentGetByEmailService studentGetByEmailService;

    private final ITransactionGetLastTransaccionsService transactionGetLastTransaccionsService;

    @Override
    public List<MovementResponseDto> cu65GetLastMovements(User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        List<Transaction> transactions = transactionGetLastTransaccionsService.execute(student.getWallet());

        return MovementMapper.toDtoList(transactions);
    }
    
}
