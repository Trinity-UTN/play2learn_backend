package trinity.play2learn.backend.profile.ranking.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithRewardTotalDto;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingFindCoinsDtoService;

@Service
public class RankingFindCoinsDtoService implements IRankingFindCoinsDtoService {

    @Override
    public StudentWithRewardTotalDto findDtoByStudent(List<StudentWithRewardTotalDto> list, Student targetStudent) {
    return list.stream()
        // Filtramos por el ID del estudiante
        .filter(dto -> dto.getStudent().equals(targetStudent))
        .findFirst()
        .orElse(null);
}
}
