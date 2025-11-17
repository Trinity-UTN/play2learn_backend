package trinity.play2learn.backend.activity.preguntados.services.commons;


import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;
import trinity.play2learn.backend.activity.preguntados.services.interfaces.IPreguntadosValidateCorrectOptionService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.messages.BadRequestExceptionMessages;

@Service
@AllArgsConstructor
public class PreguntadosValidateCorrectOptionService implements IPreguntadosValidateCorrectOptionService{
    
    //Valida que una de las opciones sea correcta
    @Override
    public void validateOneCorrectOption(QuestionRequestDto questionDto) {
        
        if (questionDto.getOptions().stream().filter(o -> o.getIsCorrect()).count() != 1) { 
            //Filtra las opciones que sean correctas y verifica que la cuenta no sea distinta de 1
                throw new BadRequestException(
                    BadRequestExceptionMessages.oneCorrectOptionPerQuestion(questionDto.getQuestion())
                );
            }
    }
    
    
}
