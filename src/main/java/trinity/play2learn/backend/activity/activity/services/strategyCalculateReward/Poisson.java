package trinity.play2learn.backend.activity.activity.services.strategyCalculateReward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountByActivityAndState;


@Service("POISSON")
@AllArgsConstructor
public class Poisson implements IActivityCalculateRewardStrategyService {

    private final IActivityCountByActivityAndState activityCountByActivityAndState;
    
    @Override
    public Double execute(Activity activity) {
        
        int cantidadRealizaciones = activityCountByActivityAndState.execute(activity, ActivityCompletedState.APPROVED);
        int cantidadAlumnos = activity.getSubject().getStudents().size();
        double totalAmount = activity.getInitialBalance();

        // λ define dónde se concentra la mayor probabilidad
        int lambda = Math.max(1, activity.getAttempts() / 2);
        
        List<Double> distribution = calculateDistribution(cantidadAlumnos, totalAmount, lambda);

        // cantidadRealizaciones empieza en 1, entonces index = cantidadRealizaciones - 1
        int index = Math.min(cantidadRealizaciones, cantidadAlumnos - 1);

        return distribution.get(index);
    }

    public List<Double> calculateDistribution(int n, double totalAmount, double lambda) {
        PoissonDistribution poisson = new PoissonDistribution(lambda);

        double[] weights = new double[n];
        for (int i = 0; i < n; i++) {
            weights[i] = poisson.probability(i);
        }

        double totalWeight = Arrays.stream(weights).sum();

        List<Double> rewards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            rewards.add((weights[i] / totalWeight) * totalAmount);
        }

        return rewards;
    }
}
