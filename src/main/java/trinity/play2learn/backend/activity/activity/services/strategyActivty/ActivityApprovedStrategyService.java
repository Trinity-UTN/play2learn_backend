package trinity.play2learn.backend.activity.activity.services.strategyActivty;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@Service("APPROVED")
@AllArgsConstructor
public class ActivityApprovedStrategyService implements IActivityCompletedStrategyService {
    
    private final IActivityCompletedRepository activityCompletedRepository;
    
    private final Map<String, IActivityCalculateRewardStrategyService> activityCalculateRewardStrategyServiceMap;

    private final ITransactionGenerateService transactionGenerateService;


    @Override
    public ActivityCompletedResponseDto execute(ActivityCompleted activityCompleted) {

        IActivityCalculateRewardStrategyService rewardStrategyService = activityCalculateRewardStrategyServiceMap.get(
            activityCompleted.getActivity().getTypeReward().name()
        );

        Double reward = rewardStrategyService.execute(
            activityCompleted.getActivity()
        );

        transactionGenerateService.generate(
            TypeTransaction.RECOMPENSA,
            reward,
            "Recompensa por actividad completada",
            TransactionActor.SISTEMA, 
            TransactionActor.ESTUDIANTE,
            activityCompleted.getStudent().getWallet(),
            null,
            activityCompleted.getActivity(),
            null,
            null,
            null,
            null
        );
        
        activityCompleted.setState(ActivityCompletedState.APPROVED);

        activityCompleted.setReward(reward);

        activityCompleted.setCompletedAt(LocalDateTime.now());

        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
        
    }
    
    
}
