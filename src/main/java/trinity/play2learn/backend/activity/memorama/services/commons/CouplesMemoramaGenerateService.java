package trinity.play2learn.backend.activity.memorama.services.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.memorama.dtos.CouplesMemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.mappers.CouplesMemoramaMapper;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.CouplesMemorama;
import trinity.play2learn.backend.activity.memorama.repositories.ICouplesMemoramaRepository;
import trinity.play2learn.backend.activity.memorama.services.interfaces.ICouplesMemoramaGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.imgBB.dtos.ImgBBUploadResultDTO;
import trinity.play2learn.backend.configs.imgBB.services.ImageUploadService;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Service
@AllArgsConstructor
public class CouplesMemoramaGenerateService implements ICouplesMemoramaGenerateService{

    private final ICouplesMemoramaRepository CouplesMemoramaRepository;

    private final ImageUploadService imageUploadService;

    @Override
    @Transactional
    public CouplesMemorama register(CouplesMemoramaRequestDto dto, Memorama memorama) throws IOException, BadRequestException {
        /* 
         * Primero valido que se haya mandado una imagen y concepto
         * Luego subo la imagen a imgBB
         * Con el url de la imagen y el concepto creo una Couples
         * Luego guardo la Couples en la base de datos
         * Finalmente retorno la Couples guardada
         */
        if (dto.getImage() == null || dto.getConcept() == null) {
            throw new BadRequestException(ValidationMessages.NOT_NULL_IMAGE_CONCEPT);
        }

        ImgBBUploadResultDTO imagenUpload = imageUploadService.uploadImage(dto.getImage());

        CouplesMemorama CouplesMemoramaToSave = CouplesMemoramaMapper.toModel(imagenUpload.getImageUrl(), dto.getConcept(), memorama);

        return CouplesMemoramaRepository.save(CouplesMemoramaToSave);
    }

    @Override
    public List<CouplesMemorama> registerList(List<CouplesMemoramaRequestDto> dtos, Memorama memorama) throws BadRequestException, IOException {
        List<CouplesMemorama> Coupless = new ArrayList<CouplesMemorama>();
        
        for (CouplesMemoramaRequestDto dto : dtos) {
            Coupless.add(this.register(dto, memorama));
        }
        
        return Coupless;
    }
    
}
