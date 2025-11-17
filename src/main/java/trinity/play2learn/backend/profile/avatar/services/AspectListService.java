package trinity.play2learn.backend.profile.avatar.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectListService;

@Service
@AllArgsConstructor
public class AspectListService implements IAspectListService {

    private final IAspectRepository aspectRepository;
    
    @Override
    public List<AspectResponseDto> cu48listAspects() {
        return AspectMapper.toDtoList(aspectRepository.findAllByDeletedAtIsNullOrderByTypeAscNameAsc());
    }
    
}
