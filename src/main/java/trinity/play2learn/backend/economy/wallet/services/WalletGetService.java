package trinity.play2learn.backend.economy.wallet.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.transaction.mappers.TransactionMapper;
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGetLastTransaccionsService;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
import trinity.play2learn.backend.economy.wallet.mappers.WalletMapper;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletGetService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class WalletGetService implements IWalletGetService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final ITransactionGetLastTransaccionsService transactionGetLastTransaccionsService;
    
    
    @Override
    @Transactional(readOnly = true)
    public WalletCompleteResponseDto cu70GetWallet(User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());
        
        List<Transaction> transactions = transactionGetLastTransaccionsService.execute(student.getWallet());
    
        return WalletMapper.toCompleteDto(student.getWallet(), TransactionMapper.toDtoList(transactions));
    }
    
}
