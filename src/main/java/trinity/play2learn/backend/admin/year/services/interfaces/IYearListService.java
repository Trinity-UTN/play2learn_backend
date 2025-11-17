package trinity.play2learn.backend.admin.year.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;

public interface IYearListService {

    
    /**
     * CU7 - Listar todos los años académicos.
     * 
     * @return Lista de años académicos.
     */
    List<YearResponseDto> cu8ListYears();
} 