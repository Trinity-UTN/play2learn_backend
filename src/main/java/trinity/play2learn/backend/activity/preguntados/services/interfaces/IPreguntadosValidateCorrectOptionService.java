package trinity.play2learn.backend.activity.preguntados.services.interfaces;

import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;

public interface IPreguntadosValidateCorrectOptionService {
    
    void validateOneCorrectOption(QuestionRequestDto questionDto);
}
