package trinity.play2learn.backend.activity.memorama.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.activity.activity.models.Dificulty;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.CouplesMemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.ValidateMemoramaDto;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

public class MemoramaRequestMapper {
    
    public static MemoramaRequestDto toDto (
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Dificulty dificulty,
        int maxTime,
        Long subjectId,
        int attempts,
        List<String> concepts, 
        List<MultipartFile> images,
        Double initialBalance
    ){
        if (concepts.size() != images.size()) {
            throw new BadRequestException("La cantidad de conceptos e imágenes no coincide");
        }
        List<CouplesMemoramaRequestDto> parejas = new ArrayList<>();

        for (int i = 0; i < concepts.size(); i++) {
            CouplesMemoramaRequestDto pareja = new CouplesMemoramaRequestDto();
            pareja.setConcept(concepts.get(i));
            pareja.setImage(images.get(i));
            parejas.add(pareja);
        }

        MemoramaRequestDto dto = new MemoramaRequestDto();
        dto.setDescription(description);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setDificulty(dificulty);
        dto.setMaxTime(maxTime);
        dto.setSubjectId(subjectId);
        dto.setAttempts(attempts);
        dto.setCouples(parejas);
        dto.setInitialBalance(initialBalance);
        dto.setActualBalance(initialBalance);

        //Antes valido el dto que cumpla con las restricciones impuestas
        ValidateMemoramaDto.validateDto(dto);

        return dto;
    }

    
}
