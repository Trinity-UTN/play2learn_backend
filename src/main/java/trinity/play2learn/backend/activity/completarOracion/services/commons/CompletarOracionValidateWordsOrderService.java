package trinity.play2learn.backend.activity.completarOracion.services.commons;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.SentenceCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.WordCompletarOracionRequestDto;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionValidateWordsOrderService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

@Service
@AllArgsConstructor
public class CompletarOracionValidateWordsOrderService implements ICompletarOracionValidateWordsOrderService{

    //Este metodo valida los orders de las palabras de una oracion
    @Override
    public void validateWordsOrder(SentenceCompletarOracionRequestDto sentence) {
        List<WordCompletarOracionRequestDto> words = sentence.getWords();
        int wordsSize = words.size();

        List<Integer> orders = words.stream().map(WordCompletarOracionRequestDto::getWordOrder).toList();

        Set<Integer> uniqueOrders = new HashSet<>(orders); //Crea una lista de orders eliminando los duplicados

        //No se pueden repetir los orders
        if (uniqueOrders.size() != orders.size()) {
            throw new BadRequestException("The words in sentence must have unique orders.");
        }

        //Si algun orden es mayor o igual a la cantidad de palabras o es menor a 0, esta fuera de rango
        if (orders.stream().anyMatch(o -> o >= wordsSize || o < 0)) { 
            throw new BadRequestException("The words in sentence must have orders between 0 and numbers of words.");
        }
    }
    
    
}
