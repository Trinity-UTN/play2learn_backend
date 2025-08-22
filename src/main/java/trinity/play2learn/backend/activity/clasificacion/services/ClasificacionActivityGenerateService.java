package trinity.play2learn.backend.activity.clasificacion.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.mappers.ClasificacionActivityMapper;
import trinity.play2learn.backend.activity.clasificacion.repositories.IClasificacionActivityRepository;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionGenerateService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateCategoriesNamesService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateConceptsNamesService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;

@Service
@AllArgsConstructor
public class ClasificacionActivityGenerateService implements IClasificacionGenerateService {
    
    private final IClasificacionActivityRepository clasificacionRepository;

    private final ISubjectGetByIdService subjectGetService;

    private final IClasificacionValidateCategoriesNamesService validateCategoriesNamesService;

    private final IClasificacionValidateConceptsNamesService validateConceptsNamesService;

    private final ITransactionGenerateService transactionGenerateService;

    @Override
    @Transactional
    public ClasificacionActivityResponseDto cu43GenerateClasificacionActivity(ClasificacionActivityRequestDto activityRequestDto) {
        
        //Lanza un 404 si no encuentra la materia con el id proporcionado
        Subject subject = subjectGetService.findById(activityRequestDto.getSubjectId());
        
        //Lanza un 400 si las categorias tienen nombre repetidos
        validateCategoriesNamesService.validateCategoriesNames(activityRequestDto);

        //Lanza un 400 si los conceptos de todas las categorias tienen nombres repetidos
        validateConceptsNamesService.validateDuplicateConceptsNames(activityRequestDto);

        transactionGenerateService.generate (
            TypeTransaction.ACTIVIDAD,
            activityRequestDto.getInitialBalance(),
            "Actividad de clasificación",
            TransactionActor.SISTEMA,
            TransactionActor.SISTEMA,
            null,
            subject
        );

        //Guarda la actividad en la base de datos y la retorno como response dto
        return ClasificacionActivityMapper.toDto(clasificacionRepository.save(ClasificacionActivityMapper.toModel(activityRequestDto, subject)));
    }
    
    
}
