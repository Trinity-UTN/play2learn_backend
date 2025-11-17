package trinity.play2learn.backend.activity.memorama.services.interfaces;

import java.io.IOException;
import java.util.List;

import trinity.play2learn.backend.activity.memorama.dtos.CouplesMemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.activity.memorama.models.CouplesMemorama;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

public interface ICouplesMemoramaGenerateService {
    
    public CouplesMemorama register (CouplesMemoramaRequestDto dto, Memorama memorama) throws IOException, BadRequestException;

    public List<CouplesMemorama> registerList (List<CouplesMemoramaRequestDto> dtos, Memorama memorama) throws BadRequestException, IOException;

}
