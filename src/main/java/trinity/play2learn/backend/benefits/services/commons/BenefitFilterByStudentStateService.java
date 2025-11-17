package trinity.play2learn.backend.benefits.services.commons;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterByStudentStateService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterStrategyService;

@Service
@AllArgsConstructor
public class BenefitFilterByStudentStateService implements IBenefitFilterByStudentStateService {
    
    private final Map<String , IBenefitFilterStrategyService> benefitFilterByStudentStateServiceMap; // <state, service>

    @Override
    public List<Benefit> filterByStudentState(List<Benefit> benefits, Student student,BenefitStudentState state) {
        
        return benefitFilterByStudentStateServiceMap.get(state.name()).filter(benefits, student);
    }
    

    
}
