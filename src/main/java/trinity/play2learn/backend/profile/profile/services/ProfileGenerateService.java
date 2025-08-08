package trinity.play2learn.backend.profile.profile.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.profile.dtos.response.ProfileResponseDto;
import trinity.play2learn.backend.profile.profile.mappers.ProfileMapper;
import trinity.play2learn.backend.profile.profile.repositories.IProfileRepository;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileGenerateService;

@Service
@AllArgsConstructor
public class ProfileGenerateService implements IProfileGenerateService {

    private final IProfileRepository profileRepository;

    @Override
    public ProfileResponseDto cu52generateProfile(Student student) {
        
        return ProfileMapper.toDto(profileRepository.save(ProfileMapper.toModel(student)));
        
    }
    
}
