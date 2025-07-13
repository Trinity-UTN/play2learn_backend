package trinity.play2learn.backend.admin.year.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.services.commons.YearGetByIdService;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearGetService;

@Service
@AllArgsConstructor
public class YearGetService implements IYearGetService {

    private final YearGetByIdService yearGetByIdService;

    @Override
    public YearResponseDto cu13GetYear(Long id) {
        return YearMapper.toDto(yearGetByIdService.get(id));
    }
    
}
