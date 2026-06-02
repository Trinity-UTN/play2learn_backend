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
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityDidAllStudentsApproveService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.notification.models.NotificationType;
import trinity.play2learn.backend.notification.services.interfaces.INotificationCreateSingleWithTitleService;

@Service("APPROVED")
@AllArgsConstructor
public class ActivityApprovedStrategyService implements IActivityCompletedStrategyService {
    
    private final IActivityCompletedRepository activityCompletedRepository;
    private final Map<String, IActivityCalculateRewardStrategyService> activityCalculateRewardStrategyServiceMap;
    private final ITransactionGenerateService transactionGenerateService;
    private final INotificationCreateSingleWithTitleService notificationCreateSingleWithTitleService;
    private final IActivityDidAllStudentsApproveService activityDidAllStudentsApproveService;
    
    @Override
    public ActivityCompletedResponseDto execute(ActivityCompleted activityCompleted) {

        //Valido consistencia entre estado y score
        if (activityCompleted.getScore() < 60) {
            throw new ConflictException("La actividad no puede ser aprobada con un puntaje menor a 60.");
        }

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

        notificationCreateSingleWithTitleService.createSingleNotificationWithTitle(
            activityCompleted.getActivity().getSubject().getTeacher().getUser(),
            NotificationType.STUDENT_COMPLETE_ACTIVITY,
            "El estudiante " + activityCompleted.getStudent().getCompleteName() + " ha aprobado la actividad " + activityCompleted.getActivity().getName()
        );
        
        if (activityDidAllStudentsApproveService.didAllStudentsApprove(activityCompleted.getActivity())) {
            notificationCreateSingleWithTitleService.createSingleNotificationWithTitle(
            activityCompleted.getActivity().getSubject().getTeacher().getUser(),
            NotificationType.STUDENT_COMPLETE_ACTIVITY,
            "Todos los estudiantes aprobaron tu actividad " + activityCompleted.getActivity().getName()
            );
        }
        
        return ActivityCompletedMapper.toDto(activityCompletedRepository.save(activityCompleted));
        
    }
    
    
}
