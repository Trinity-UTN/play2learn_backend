package trinity.play2learn.backend.activity.activity.services.strategyActivty;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityCompletedMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.admin.student.models.Student;
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
    public ActivityCompletedResponseDto execute(Activity activity, Student student, Integer remainingAttempts) {

        IActivityCalculateRewardStrategyService rewardStrategyService = activityCalculateRewardStrategyServiceMap.get(activity.getTypeReward().name());

        Double reward = rewardStrategyService.execute(activity);

        transactionGenerateService.generate(
            TypeTransaction.RECOMPENSA,
            reward,
            "Recompensa por actividad completada",
            TransactionActor.SISTEMA, 
            TransactionActor.ESTUDIANTE,
            student.getWallet(),
            null,
            activity
        );
        
        ActivityCompleted activityCompleted = ActivityCompletedMapper.toModel(activity, student, reward, remainingAttempts, ActivityCompletedState.APPROVED);

        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
        
    }
    
    
}
