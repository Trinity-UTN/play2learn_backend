package trinity.play2learn.backend.benefits.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class BenefitGetByIdService implements IBenefitGetByIdService{
    
    private final IBenefitRepository benefitRepository;

    @Override
    public Benefit getById(Long id) {
        
        return benefitRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No existe un beneficio con el id proporcionado"));
    }

    
    
}
