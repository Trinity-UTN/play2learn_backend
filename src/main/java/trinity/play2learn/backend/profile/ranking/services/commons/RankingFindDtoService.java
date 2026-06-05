package trinity.play2learn.backend.profile.ranking.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;
import trinity.play2learn.backend.profile.ranking.services.interfaces.IRankingFindDtoService;

@Service
public class RankingFindDtoService implements IRankingFindDtoService {

    @Override
    public StudentWithTotalDto findDtoByStudent(List<StudentWithTotalDto> list, Student targetStudent) {
        return list.stream()
                // Filtramos por el ID del estudiante
                .filter(dto -> dto.getStudent().equals(targetStudent))
                .findFirst()
                .orElse(null);
    }
}
