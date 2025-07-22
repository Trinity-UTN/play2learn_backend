package trinity.play2learn.backend.activity.preguntados.Mappers;

import java.util.List;

import trinity.play2learn.backend.activity.preguntados.dtos.request.QuestionRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.QuestionResponseDto;
import trinity.play2learn.backend.activity.preguntados.models.Question;

public class QuestionMapper {
    
    public static Question toModel(QuestionRequestDto questionDto) {
        Question question = Question.builder()
            .question(questionDto.getQuestion())
            .correctAnswer(questionDto.getCorrectAnswer())
            .build();
        
        question.setOptions(OptionMapper.toModelList(questionDto.getOptions())); //Relaciono las opciones con la pregunta
        return question;
    }

    public static List<Question> toModelList(List<QuestionRequestDto> questionDtos) {
        return questionDtos
            .stream()
            .map(QuestionMapper::toModel)
            .toList();
    }

    public static QuestionResponseDto toDto(Question question) {
        return QuestionResponseDto.builder()
            .question(question.getQuestion())
            .correctAnswer(question.getCorrectAnswer())
            .options(OptionMapper.toStringList(question.getOptions())) //Lo muestra como un listado de Strings
            .build();
    }

    public static List<QuestionResponseDto> toDtoList(List<Question> questions) {
        return questions
            .stream()
            .map(QuestionMapper::toDto)
            .toList();
    }
}
