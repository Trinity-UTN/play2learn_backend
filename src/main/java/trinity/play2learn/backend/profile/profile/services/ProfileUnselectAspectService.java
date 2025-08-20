package trinity.play2learn.backend.profile.profile.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;
import trinity.play2learn.backend.profile.profile.dtos.request.UnselectAspectRequestDto;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileGetByIdService;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUnselectAspectService;

@Service
@AllArgsConstructor
public class ProfileUnselectAspectService implements IProfileUnselectAspectService {

    private final IProfileGetByIdService profileGetByIdService;

    private final IProfileRepository profileRepository;
    
    
    @Override
    public StudentResponseDto cu59unselectAspect(UnselectAspectRequestDto dto) {


        Profile profile = profileGetByIdService.get(dto.getProfileId());

        if (dto.getTypeAspect().equals(TypeAspect.CUERPO)){
            profile.setSelectedBody(null);
        } else if (dto.getTypeAspect().equals(TypeAspect.REMERA)){
            profile.setSelectedShirt(null);
        } else if (dto.getTypeAspect().equals(TypeAspect.SOMBRERO)){
            profile.setSelectedHat(null);
        }

        Profile updatedProfile = profileRepository.save(profile);

        return StudentMapper.toDto(updatedProfile.getStudent());
        
    }
    
}
