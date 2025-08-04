package trinity.play2learn.backend.profile.avatar.services;

import java.io.IOException;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.imgBB.dtos.ImgBBUploadResultDTO;
import trinity.play2learn.backend.configs.imgBB.services.ImageUploadService;
import trinity.play2learn.backend.profile.avatar.dtos.request.AspectRegisterRequestDto;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectExistByName;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectRegisterService;

@Service
@AllArgsConstructor
public class AspectRegisterService implements IAspectRegisterService {

    private final IAspectExistByName aspectExistByName;

    private final ImageUploadService imageUploadService;

    private final IAspectRepository aspectRepository;

    @Override
    public AspectResponseDto cu47registerAspect(AspectRegisterRequestDto dto) throws IOException {

        if (aspectExistByName.validate(dto.getName())) {
            throw new ConflictException("Ya existe un aspecto con el mismo nombre");
        }

        ImgBBUploadResultDTO imagenUpload = imageUploadService.uploadImage(dto.getImage());

        Aspect aspectToSave = AspectMapper.toModel(dto, imagenUpload.getImageUrl());

        return AspectMapper.toDto(aspectRepository.save(aspectToSave));
    }
    
}
