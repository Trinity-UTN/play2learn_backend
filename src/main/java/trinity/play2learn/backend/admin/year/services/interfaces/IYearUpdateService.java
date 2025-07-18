package trinity.play2learn.backend.admin.year.services.interfaces;

import trinity.play2learn.backend.admin.year.dtos.YearUpdateRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;

public interface IYearUpdateService {
    
    public YearResponseDto cu10UpdateYear(Long id , YearUpdateRequestDto yearUpdateRequestDto);
    
}
