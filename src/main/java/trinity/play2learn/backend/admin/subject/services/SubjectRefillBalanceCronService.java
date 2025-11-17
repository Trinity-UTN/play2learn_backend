package trinity.play2learn.backend.admin.subject.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;

@Service
@AllArgsConstructor
public class SubjectRefillBalanceCronService {

    private final ISubjectRefillBalanceService subjectRefillBalanceService;

    /**
     * Este metodo se ejecuta el primer día de cada mes a la medianoche.
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void execute (){
        subjectRefillBalanceService.cu58RefillBalance();
    }
    
}
