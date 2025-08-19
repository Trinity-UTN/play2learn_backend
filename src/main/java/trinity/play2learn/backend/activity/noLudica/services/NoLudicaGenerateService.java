package trinity.play2learn.backend.activity.noLudica.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.mappers.NoLudicaMapper;
import trinity.play2learn.backend.activity.noLudica.repositories.INoLudicaRepository;
import trinity.play2learn.backend.activity.noLudica.services.interfaces.INoLudicaGenerateService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionGenerateService;

@Service
@AllArgsConstructor
public class NoLudicaGenerateService implements INoLudicaGenerateService{

    private final ISubjectGetByIdService findSubjectByIdService;

    private final INoLudicaRepository noLudicaRepository;

    private final ITransaccionGenerateService transaccionGenerateService;

    @Transactional
    @Override
    public NoLudicaResponseDto cu45GenerateNoLudica(NoLudicaRequestDto dto) {
        
        Subject subject = findSubjectByIdService.findById(dto.getSubjectId());

        transaccionGenerateService.generate (
            TypeTransaccion.ACTIVIDAD,
            dto.getInitialBalance(),
            "Actividad No Ludica",
            ActorTransaccion.SISTEMA,
            ActorTransaccion.SISTEMA,
            null,
            subject
        );

        return NoLudicaMapper.toDto(noLudicaRepository.save(NoLudicaMapper.toModel(dto, subject)));
    }
    
}
