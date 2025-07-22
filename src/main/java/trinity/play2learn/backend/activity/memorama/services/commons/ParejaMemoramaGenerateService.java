package trinity.play2learn.backend.activity.memorama.services.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.memorama.dtos.ParejaMemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.mappers.ParejaMemoramaMapper;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.ParejaMemorama;
import trinity.play2learn.backend.activity.memorama.repositories.IParejaMemoramaRepository;
import trinity.play2learn.backend.activity.memorama.services.interfaces.IParejaMemoramaGenerateService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.configs.imgBB.dtos.ImgBBUploadResultDTO;
import trinity.play2learn.backend.configs.imgBB.services.ImageUploadService;

@Service
@AllArgsConstructor
public class ParejaMemoramaGenerateService implements IParejaMemoramaGenerateService{

    private final IParejaMemoramaRepository parejaMemoramaRepository;

    private final ImageUploadService imageUploadService;

    @Override
    @Transactional
    public ParejaMemorama register(ParejaMemoramaRequestDto dto, Memorama memorama) throws IOException, BadRequestException {
        /* 
         * Primero valido que se haya mandado una imagen y concepto
         * Luego subo la imagen a imgBB
         * Con el url de la imagen y el concepto creo una pareja
         * Luego guardo la pareja en la base de datos
         * Finalmente retorno la pareja guardada
         */
        if (dto.getImagen() == null || dto.getConcepto() == null) {
            throw new BadRequestException("Image and concept must not be null");
        }

        ImgBBUploadResultDTO imagenUpload = imageUploadService.uploadImage(dto.getImagen());

        ParejaMemorama parejaMemoramaToSave = ParejaMemoramaMapper.toModel(imagenUpload.getImageUrl(), dto.getConcepto(), memorama);

        return parejaMemoramaRepository.save(parejaMemoramaToSave);
    }

    @Override
    public List<ParejaMemorama> registerList(List<ParejaMemoramaRequestDto> dtos, Memorama memorama) throws BadRequestException, IOException {
        List<ParejaMemorama> parejas = new ArrayList<ParejaMemorama>();
        
        for (ParejaMemoramaRequestDto dto : dtos) {
            parejas.add(this.register(dto, memorama));
        }
        
        return parejas;
    }
    
}
