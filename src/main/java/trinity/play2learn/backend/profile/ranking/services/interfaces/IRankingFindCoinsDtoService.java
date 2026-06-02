package trinity.play2learn.backend.profile.ranking.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithRewardTotalDto;

public interface IRankingFindCoinsDtoService {
    
    StudentWithRewardTotalDto findDtoByStudent(List<StudentWithRewardTotalDto> dtoList, Student targetStudent);
    
}
