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
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@Service
@AllArgsConstructor
public class NoLudicaGenerateService implements INoLudicaGenerateService{

    private final ISubjectGetByIdService findSubjectByIdService;

    private final INoLudicaRepository noLudicaRepository;

    private final ITransactionGenerateService transactionGenerateService;

    @Transactional
    @Override
    public NoLudicaResponseDto cu45GenerateNoLudica(NoLudicaRequestDto dto) {
        
        Subject subject = findSubjectByIdService.findById(dto.getSubjectId());

        transactionGenerateService.generate (
            TypeTransaction.ACTIVIDAD,
            dto.getInitialBalance(),
            "Actividad No Ludica",
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject
        );

        return NoLudicaMapper.toDto(noLudicaRepository.save(NoLudicaMapper.toModel(dto, subject)));
    }
    
}
