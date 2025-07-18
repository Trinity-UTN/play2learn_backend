package trinity.play2learn.backend.admin.student.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentListPaginatedService;
import trinity.play2learn.backend.admin.student.specs.StudentSpects;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class StudentListPaginatedService implements IStudentListPaginatedService{

    private final IStudentRepository studentRepository;

    @Override
    public PaginatedData<StudentResponseDto> cu21ListPaginatedStudents(
        int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues
        ) {

        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Student> spec = Specification.where(null); // Quite restriction notDeleted

        if (search != null && !search.isBlank()) {
        spec = spec.and(StudentSpects.nameContains(search));
        }
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(StudentSpects.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<Student> pageResult = studentRepository.findAll(spec, pageable);

        List<StudentResponseDto> dtos = StudentMapper.toListDto(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
