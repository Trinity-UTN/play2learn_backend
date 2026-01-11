package trinity.play2learn.backend.profile.profile.mappers;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.configs.levels.ValueXp;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.profile.dtos.response.ProfileResponseDto;
import trinity.play2learn.backend.profile.profile.models.Profile;

public class ProfileMapper {

    public static ProfileResponseDto toDto (Profile profile){
        return ProfileResponseDto.builder()
            .id(profile.getId())
            .selectedBody( (profile.getSelectedBody() != null) ? AspectMapper.toDto(profile.getSelectedBody()) : null)
            .selectedShirt( (profile.getSelectedShirt() != null) ? AspectMapper.toDto(profile.getSelectedShirt()) : null)
            .selectedHat( (profile.getSelectedHat() != null) ? AspectMapper.toDto(profile.getSelectedHat()) : null)
            .ownedAspects(AspectMapper.toDtoList(profile.getOwnedAspects()))
            .level((profile.getCurrentLevel() != null) ? profile.getCurrentLevel() : 1L)
            .xp((profile.getCurrentXp() != null) ? profile.getCurrentXp() : 0L)
            .xpToNextLevel((profile.getCurrentLevel() != null) ? (profile.getCurrentLevel() * profile.getCurrentLevel() * ValueXp.BASE.getValue() - profile.getCurrentXp()) : ValueXp.BASE.getValue())
            .build();
    }

    public static Profile toModel (Student student) {
        return Profile.builder()
            .student(student)
            .build();
    }
    
}
