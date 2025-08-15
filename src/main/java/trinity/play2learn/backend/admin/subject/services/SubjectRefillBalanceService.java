package trinity.play2learn.backend.admin.subject.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.IGenerateTransaccionService;

@Service
@AllArgsConstructor
public class SubjectRefillBalanceService implements ISubjectRefillBalanceService {

    private final ISubjectRepository subjectRepository;

    private final IGenerateTransaccionService generateTransaccionService;

    @Override
    public List<SubjectResponseDto> cu58RefillBalance() {

        Iterable <Subject> subjects = subjectRepository.findAllByDeletedAtIsNull();

        for (Subject subject : subjects) {
            if (subject.getInitialBalance() == 0) {
                subject.setInitialBalance(calculateInitialBalance(subject));  
            }

            Transaccion transaccion = generateTransaccionService.generate(
                TypeTransaccion.ASIGNACION,
                subject.getInitialBalance() - subject.getActualBalance(), 
                "Asignación de monedas mensual.", 
                ActorTransaccion.SISTEMA, 
                ActorTransaccion.SISTEMA, 
                null, 
                subject
            );
        }

        return SubjectMapper.toDtoList(subjectRepository.findAllByDeletedAtIsNull());
    }

    public Double calculateInitialBalance (Subject subject) {
        return 5000.0 * subject.getStudents().size();
    }
    
}
