package trinity.play2learn.backend.activity.completarOracion.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.mappers.CompletarOracionActivityMapper;
import trinity.play2learn.backend.activity.completarOracion.models.CompletarOracionActivity;
import trinity.play2learn.backend.activity.completarOracion.repositories.ICompletarOracionRepository;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionGenerateService;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionValidateWordMissingService;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionValidateWordsOrderService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class CompletarOracionGenerateService implements ICompletarOracionGenerateService {
    
    private final ICompletarOracionRepository completarOracionRepository;
    private final ISubjectGetByIdService getSubjectByIdService;
    private final ICompletarOracionValidateWordsOrderService completarOracionValidateWordsOrderService;
    private final ICompletarOracionValidateWordMissingService completarOracionValidateWordMissingService;
    private final ITransactionGenerateService transactionGenerateService;
    private final ITeacherGetByEmailService teacherGetByEmailService;
    
    @Transactional
    @Override
    public CompletarOracionActivityResponseDto cu42generateCompletarOracionActivity(CompletarOracionActivityRequestDto completarOracionActivityRequestDto, User user) {

        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());
        Subject subject = getSubjectByIdService.findById(completarOracionActivityRequestDto.getSubjectId()); //Lanza un 404 si no encuentra la materia con el id proporcionado

        if (!subject.getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no esta asignado a la materia");
        }
        
        //Si alguna validacion falla lanzo un 400
        completarOracionActivityRequestDto.getSentences().forEach(sentence -> {

           completarOracionValidateWordsOrderService.validateWordsOrder(sentence); 
            //Valido que cada oracion tenga ordenes de las palabras validos(Sin repetir y dentro del rango de palabras de la oracion)


           completarOracionValidateWordMissingService.validateAtLeastOneWordMissing(sentence);
           //Valido que cada oracion tenga al menos una palabra faltante
        });
        

        CompletarOracionActivity activity = CompletarOracionActivityMapper.toModel(completarOracionActivityRequestDto, subject);
        
        activity.buildCompleteSentences(); //Cada oracion arma su oracion completa en base al listado de palabras

        CompletarOracionActivity savedActivity = completarOracionRepository.save(activity);

        transactionGenerateService.generate (
            TypeTransaction.ACTIVIDAD,
            completarOracionActivityRequestDto.getInitialBalance(),
            "Actividad de ahorcado",
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject,
            savedActivity,
            null,
            null,
            null,
            null
        );

        return CompletarOracionActivityMapper.toDto(savedActivity); 

    }

    
}
