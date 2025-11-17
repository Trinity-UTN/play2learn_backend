package trinity.play2learn.backend.activity.completarOracion.services.interfaces;

import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;

public interface ICompletarOracionValidateWordsOrderService {
    
    void validateWordsOrder(SentenceCompletarOracionRequestDto sentence);
}
