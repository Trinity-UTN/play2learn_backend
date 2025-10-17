package trinity.play2learn.backend.profile.profile.services;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectGetByIdService;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileGetByIdService;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileSelectAspectService;

@Service
@AllArgsConstructor
public class ProfileSelectAspectService implements IProfileSelectAspectService {

    private final IProfileGetByIdService profileGetByIdService;

    private final IAspectGetByIdService aspectGetByIdService;

    private final IProfileRepository profileRepository;

    @Override
    public StudentResponseDto cu54selectAspect(Long profileId, Long aspectId) {

        Profile profile = profileGetByIdService.get(profileId);
        Aspect aspect = aspectGetByIdService.get(aspectId);

        if (!profile.getOwnedAspects().contains(aspect)) {
            throw new ConflictException("El aspecto no está en el inventario");
        }

        profile = aspect.getType().assign(profile, aspect);

        Profile updatedProfile = profileRepository.save(profile);

        return StudentMapper.toDto(updatedProfile.getStudent());
    }
    
}
