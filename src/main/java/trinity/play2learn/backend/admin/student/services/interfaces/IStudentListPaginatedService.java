package trinity.play2learn.backend.admin.student.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.configs.response.PaginatedData;

public interface IStudentListPaginatedService {
    
    public PaginatedData<StudentResponseDto> cu21ListPaginatedStudents (
        int page, int size, String orderBy, String orderType, 
        String search, List<String> filters, List<String> filterValues
    ); 
}
