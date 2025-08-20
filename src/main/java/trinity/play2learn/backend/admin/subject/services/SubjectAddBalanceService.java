package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddBalanceService;

@Service
@AllArgsConstructor
public class SubjectAddBalanceService implements ISubjectAddBalanceService {

    private final ISubjectRepository subjectRepository;
    
    @Override
    public Subject execute(Subject subject, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        subject.setActualBalance(subject.getActualBalance() + amount);
        subject.setInitialBalance(subject.getActualBalance());

        return subjectRepository.save(subject);
    }
    
}
