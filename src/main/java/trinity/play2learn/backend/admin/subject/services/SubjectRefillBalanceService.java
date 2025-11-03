package trinity.play2learn.backend.admin.subject.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@Service
@AllArgsConstructor
public class SubjectRefillBalanceService implements ISubjectRefillBalanceService {

    private final ISubjectRepository subjectRepository;

    private final ITransactionGenerateService generateTransactionService;

    @Override
    public List<SubjectResponseDto> cu58RefillBalance() {

        Iterable <Subject> subjects = subjectRepository.findAllByDeletedAtIsNull();

        for (Subject subject : subjects) {
            if (subject.getInitialBalance() == 0) {
                subject.setInitialBalance(calculateInitialBalance(subject));  
            }

            if (subject.getInitialBalance() - subject.getActualBalance() <= 0.0) {
                continue;
            }

            generateTransactionService.generate(
                TypeTransaction.ASIGNACION,
                subject.getInitialBalance() - subject.getActualBalance(), 
                "Asignación de monedas mensual.", 
                TransactionActor.SISTEMA, 
                TransactionActor.SISTEMA, 
                null, 
                subject,
                null,
                null,
                null,
                null
            );
        }

        return SubjectMapper.toDtoList(subjectRepository.findAllByDeletedAtIsNull());
    }

    public Double calculateInitialBalance (Subject subject) {
        return 5000.0 * subject.getStudents().size();
    }
    
}
