package trinity.play2learn.backend.activity.memorama.mappers;

import org.springframework.stereotype.Component;

import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaRequestDto;
import trinity.play2learn.backend.activity.memorama.dtos.MemoramaResponseDto;
import trinity.play2learn.backend.activity.memorama.models.Memorama;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Component("memoramaMapper")
public class MemoramaMapper implements IActivityMapper {
    
    public static Memorama toModel (MemoramaRequestDto dto, Subject subject) {
        return Memorama.builder()
            .name("Memorama")
            .description(dto.getDescription())
            .dificulty(dto.getDificulty())
            .maxTime(dto.getMaxTime())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .attempts(dto.getAttempts())
            .subject(subject)
            .actualBalance(0.0)
            .initialBalance(dto.getInitialBalance())
            .typeReward((dto.getTypeReward() != null) ? dto.getTypeReward() : TypeReward.EQUITATIVO)
            .build();
    }

    public static MemoramaResponseDto toDto(Memorama memorama) {
        return MemoramaResponseDto.builder()
            .id(memorama.getId())
            .description(memorama.getDescription())
            .name("Memorama")
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

    @Override
    public ActivityResponseDto toActivityDto(Activity activity) {

        if (!(activity instanceof Memorama)) {
            throw new IllegalArgumentException("Expected Memorama, got: " + activity.getClass());
        }
        
        Memorama memorama = (Memorama) activity;
        return toDto(memorama);
    }
}
