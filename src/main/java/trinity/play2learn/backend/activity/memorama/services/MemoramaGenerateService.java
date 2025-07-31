package trinity.play2learn.backend.activity.memorama.services;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.mappers.MemoramaMapper;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.CouplesMemorama;
import trinity.play2learn.backend.activity.memorama.repositories.IMemoramaRepository;
import trinity.play2learn.backend.activity.memorama.services.interfaces.IMemoramaGenerateService;
import trinity.play2learn.backend.activity.memorama.services.interfaces.ICouplesMemoramaGenerateService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

@Service
@AllArgsConstructor
public class MemoramaGenerateService implements IMemoramaGenerateService{

    private final ISubjectGetByIdService findSubjectByIdService;

    private final IMemoramaRepository memoramaRepository;

    private final ICouplesMemoramaGenerateService CouplesMemoramaGenerateService;

    @Override
    @Transactional
    public MemoramaResponseDto cu41GenerateMemorama(MemoramaRequestDto memoramaRequestDto) throws BadRequestException, IOException {
        /**
         * Que tengo que hacer:
         * - Buscar el subject por id 
         * - Generar un Memorama sacando los datos del dto
         * - Guardar el memorama
         * - Enviar las Coupless al service para guardarlas
         * - Setear las Coupless guardadas en el Memorama
         * - Mapear el memorama a un dto de respuesta
         * - Retornar 
         */
        Subject subject = findSubjectByIdService.findById(memoramaRequestDto.getSubjectId());
        
        Memorama memoramaToSave = MemoramaMapper.toModel(memoramaRequestDto, subject);

        Memorama memorama = memoramaRepository.save(memoramaToSave);

        List<CouplesMemorama> Coupless = CouplesMemoramaGenerateService.registerList(memoramaRequestDto.getCouples(), memorama);

        memorama.setCouples(Coupless);

        return MemoramaMapper.toDto (memoramaRepository.save(memorama));
    }

}
