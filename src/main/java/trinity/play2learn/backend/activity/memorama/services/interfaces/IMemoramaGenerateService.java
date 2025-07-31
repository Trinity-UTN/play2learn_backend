package trinity.play2learn.backend.activity.memorama.services.interfaces;

import java.io.IOException;

import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

public interface IMemoramaGenerateService {

    public MemoramaResponseDto cu41GenerateMemorama (MemoramaRequestDto memoramaRequestDto) throws BadRequestException, IOException;
    
} 