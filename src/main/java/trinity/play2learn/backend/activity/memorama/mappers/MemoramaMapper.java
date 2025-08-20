package trinity.play2learn.backend.activity.memorama.mappers;

import trinity.play2learn.backend.activity.activity.models.TypeReward;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

public class MemoramaMapper {
    
    public static Memorama toModel (MemoramaRequestDto dto, Subject subject) {
        return Memorama.builder()
            .description(dto.getDescription())
            .dificulty(dto.getDificulty())
            .maxTime(dto.getMaxTime())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .attempts(dto.getAttempts())
            .subject(subject)
            .actualBalance(dto.getInitialBalance())
            .initialBalance(dto.getInitialBalance())
            .typeReward(TypeReward.EQUITATIVO)
            .build();
    }

    public static MemoramaResponseDto toDto(Memorama memorama) {
        return MemoramaResponseDto.builder()
            .id(memorama.getId())
            .description(memorama.getDescription())
            .name(memorama.getClass().getSimpleName())
            .dificulty(memorama.getDificulty())
            .maxTime(memorama.getMaxTime())
            .startDate(memorama.getStartDate())
            .endDate(memorama.getEndDate())
            .attempts(memorama.getAttempts())
            .subject(SubjectMapper.toSubjectDto(memorama.getSubject()))
            .couples(CouplesMemoramaMapper.toDtoList(memorama.getCouples())) 
            .initialBalance(memorama.getInitialBalance())
            .actualBalance(memorama.getActualBalance())
            .build();
    }
}
