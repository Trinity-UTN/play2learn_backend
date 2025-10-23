package trinity.play2learn.backend.profile.profile.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectGetByIdService;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileAddAspectToInventoryService;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileGetByIdService;

@Service
@AllArgsConstructor
public class ProfileAddAspectToInventoryService implements IProfileAddAspectToInventoryService {
    
    private final IProfileGetByIdService profileGetByIdService;

    private final IAspectGetByIdService aspectGetByIdService;

    private final IProfileRepository profileRepository;

    private final ITransactionGenerateService generateTransactionService;

    
    @Override
    public StudentResponseDto cu53addAspectToInventory(Long aspectId, Long profileId) {
        Profile profile = profileGetByIdService.get(profileId);

        Aspect aspect = aspectGetByIdService.get(aspectId);

        if (profile.getOwnedAspects().contains(aspect)){
            throw new ConflictException("El aspecto ya esta en el inventario");
        }
        
        //Falta la logica de los puntos porque aun no esta implementado
        generateTransactionService.generate(
            TypeTransaction.COMPRA, 
            Double.valueOf(aspect.getPrice().doubleValue()), 
            "Compra de Aspecto", 
            TransactionActor.ESTUDIANTE, 
            TransactionActor.SISTEMA, 
            profile.getStudent().getWallet(), 
            null,
            null,
            null,
            null
        );

        profile.getOwnedAspects().add(aspect);

        profileRepository.save(profile);

        return StudentMapper.toDto(profile.getStudent());
    }
    
}
