package trinity.play2learn.backend.activity.memorama.services.interfaces;

import java.io.IOException;
import java.util.List;

import trinity.play2learn.backend.activity.memorama.dtos.ParejaMemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.ParejaMemorama;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

public interface IParejaMemoramaGenerateService {
    
    public ParejaMemorama register (ParejaMemoramaRequestDto dto, Memorama memorama) throws IOException, BadRequestException;

    public List<ParejaMemorama> registerList (List<ParejaMemoramaRequestDto> dtos, Memorama memorama) throws BadRequestException, IOException;

}
