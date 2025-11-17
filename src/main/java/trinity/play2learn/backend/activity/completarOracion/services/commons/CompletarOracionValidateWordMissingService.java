package trinity.play2learn.backend.activity.completarOracion.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionValidateWordMissingService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Service
@AllArgsConstructor
public class CompletarOracionValidateWordMissingService implements ICompletarOracionValidateWordMissingService{@Override
    public void validateAtLeastOneWordMissing(SentenceCompletarOracionRequestDto sentenceDto) {
        
        if (!sentenceDto.getWords().stream().anyMatch(w -> w.getIsMissing() == true)) {

            throw new BadRequestException(ValidationMessages.WORD_MISSING);
        };
    }
    
    
}
