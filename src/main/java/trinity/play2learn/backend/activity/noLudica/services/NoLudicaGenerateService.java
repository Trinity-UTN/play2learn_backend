package trinity.play2learn.backend.activity.noLudica.services;


import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.mappers.NoLudicaMapper;
import trinity.play2learn.backend.activity.noLudica.repositories.INoLudicaRepository;
import trinity.play2learn.backend.activity.noLudica.services.interfaces.INoLudicaGenerateService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.IFindSubjectByIdService;

@Service
@AllArgsConstructor
public class NoLudicaGenerateService implements INoLudicaGenerateService{

    private final IFindSubjectByIdService findSubjectByIdService;

    private final INoLudicaRepository noLudicaRepository;

    @Override
    public NoLudicaResponseDto cu45GenerateNoLudica(NoLudicaRequestDto dto) {
        
        Subject subject = findSubjectByIdService.findByIdOrThrowException(dto.getSubjectId());

        return NoLudicaMapper.toDto(noLudicaRepository.save(NoLudicaMapper.toModel(dto, subject)));
    }
    
}
