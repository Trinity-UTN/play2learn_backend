package trinity.play2learn.backend.admin.teacher.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.configs.response.PaginatedData;

public interface ITeacherListPaginatedService {
    PaginatedData<TeacherResponseDto> cu26ListTeachersPaginated(int page, int pageSize, String orderBy, String orderType, String search, List<String> filters, List<String> filtersValues);
}
