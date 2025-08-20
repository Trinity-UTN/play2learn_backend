package trinity.play2learn.backend.activity.ahorcado.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trinity.play2learn.backend.activity.ahorcado.repositories.IAhorcadoRepository;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.mappers.AhorcadoMapper;
import trinity.play2learn.backend.activity.ahorcado.models.Ahorcado;
import trinity.play2learn.backend.activity.ahorcado.services.interfaces.IAhorcadoGenerateService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionGenerateService;

@Service
@AllArgsConstructor
public class AhorcadoGenerateService implements IAhorcadoGenerateService {

    private final IAhorcadoRepository ahorcadoRepository;

    private final ISubjectGetByIdService getSubjectByIdService;

    private final ITransaccionGenerateService transaccionGenerateService;

    @Transactional
    @Override
    public AhorcadoResponseDto cu39GenerateAhorcado(AhorcadoRequestDto ahorcadoDto) {

        Subject subject = getSubjectByIdService.findById(ahorcadoDto.getSubjectId()); //Lanza un 404 si no encuentra la materia con el id proporcionado

        Ahorcado ahorcadoActivity = AhorcadoMapper.toModel(ahorcadoDto, subject);

        transaccionGenerateService.generate (
            TypeTransaccion.ACTIVIDAD,
            ahorcadoActivity.getInitialBalance(),
            "Actividad de ahorcado",
            ActorTransaccion.SISTEMA,
            ActorTransaccion.SISTEMA,
            null,
            subject
        );

        return AhorcadoMapper.toDto(ahorcadoRepository.save(ahorcadoActivity));
    }
    
    
}
