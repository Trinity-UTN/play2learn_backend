package trinity.play2learn.backend.activity.preguntados.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.preguntados.Mappers.PreguntadosMapper;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Preguntados;
import trinity.play2learn.backend.activity.preguntados.repositories.IPreguntadosRepository;
import trinity.play2learn.backend.activity.preguntados.services.interfaces.IPreguntadosGenerateService;
import trinity.play2learn.backend.activity.preguntados.services.interfaces.IPreguntadosValidateCorrectOptionService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.IFindSubjectByIdService;

@Service
@AllArgsConstructor
public class PreguntadosGenerateService implements IPreguntadosGenerateService{
    
    private final IPreguntadosRepository preguntadosRepository;
    private final IFindSubjectByIdService getSubjectByIdService;
    private final IPreguntadosValidateCorrectOptionService preguntadosValidateCorrectOptionService;

    @Transactional
    @Override
    public PreguntadosResponseDto cu40GeneratePreguntados(PreguntadosRequestDto preguntadosRequestDto) {
        
        Subject subject = getSubjectByIdService.findByIdOrThrowException(preguntadosRequestDto.getSubjectId()); 
        //Lanza un 404 si no encuentra la materia con el id proporcionado
        
        preguntadosRequestDto.getQuestions().forEach(q -> preguntadosValidateCorrectOptionService.validateOneCorrectOption(q)); 
        //Valido que una de las opciones sea correcta en cada pregunta

        Preguntados preguntados = PreguntadosMapper.toModel(preguntadosRequestDto, subject);
        
        return PreguntadosMapper.toDto(preguntadosRepository.save(preguntados));
    }

    
}
