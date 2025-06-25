package trinity.play2learn.backend.admin.year.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;
import trinity.play2learn.backend.admin.year.mappers.YearMapper;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearListService;

@Service
@AllArgsConstructor
public class YearListService implements IYearListService {

    private final IYearRepository yearRepository;

    @Override
    public List<YearResponseDto> cu8ListYears() {
        Iterable<Year> iterableYears = yearRepository.findAllByDeletedAtIsNull();

        if (iterableYears == null) {
            return List.of();
        }
        
        return YearMapper.toListDto(iterableYears);
    }

}