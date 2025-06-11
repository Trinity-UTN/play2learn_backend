package trinity.play2learn.backend.admin.year.services.interfaces;

import org.springframework.validation.BindingResult;

import trinity.play2learn.backend.admin.year.dtos.YearRequestDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;

public interface IYearRegisterService {

    public YearResponseDto cu7RegisterYear(YearRequestDto yearRequestDto, BindingResult result);

    
}
