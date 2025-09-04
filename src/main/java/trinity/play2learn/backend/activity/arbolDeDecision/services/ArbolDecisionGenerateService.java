package trinity.play2learn.backend.activity.arbolDeDecision.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.mappers.ArbolDeDecisionMapper;
import trinity.play2learn.backend.activity.arbolDeDecision.models.ArbolDeDecisionActivity;
import trinity.play2learn.backend.activity.arbolDeDecision.repositories.IArbolDeDecisionRepository;
import trinity.play2learn.backend.activity.arbolDeDecision.services.interfaces.IArbolDecisionGenerateService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@Service
@AllArgsConstructor
public class ArbolDecisionGenerateService implements IArbolDecisionGenerateService{
    
    private final IArbolDeDecisionRepository arbolDeDecisionRepository;
    private final ISubjectGetByIdService subjectGetService;

    private final  ITransactionGenerateService transactionGenerateService;

    @Override
    @Transactional
    public ArbolDeDecisionActivityResponseDto cu46GenerateArbolDeDecisionActivity(ArbolDeDecisionActivityRequestDto activityDto) {
        
        Subject subject = subjectGetService.findById(activityDto.getSubjectId()); //Lanza un 404 si no encuentra la materia con el id proporcionado

        ArbolDeDecisionActivity activity = arbolDeDecisionRepository.save(ArbolDeDecisionMapper.toModel(activityDto, subject));

        transactionGenerateService.generate (
            TypeTransaction.ACTIVIDAD,
            activityDto.getInitialBalance(),
            "Actividad de árbol de decisión",
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject,
            activity
        );
            
        return ArbolDeDecisionMapper.toDto(activity);
    }

    
}
