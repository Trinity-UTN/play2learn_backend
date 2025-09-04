package trinity.play2learn.backend.activity.activity.services.strategyCalculateReward;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@Service ("EQUITATIVO")
@AllArgsConstructor
public class Equitativo implements IActivityCalculateRewardStrategyService{

    private final ITransactionGenerateService transactionGenerateService;
    
    @Override
    public Double execute(Activity activity) {

        return activity.getInitialBalance()/activity.getSubject().getStudents().size();

    }
    
}
