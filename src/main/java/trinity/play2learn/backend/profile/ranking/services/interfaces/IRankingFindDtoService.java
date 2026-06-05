package trinity.play2learn.backend.profile.ranking.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;

public interface IRankingFindDtoService {

    StudentWithTotalDto findDtoByStudent(List<StudentWithTotalDto> dtoList, Student targetStudent);

}
