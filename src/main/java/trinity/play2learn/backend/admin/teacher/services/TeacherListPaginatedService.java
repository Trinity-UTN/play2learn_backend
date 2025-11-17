package trinity.play2learn.backend.admin.teacher.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherPaginatedRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherListPaginatedService;
import trinity.play2learn.backend.admin.teacher.specs.TeacherSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class TeacherListPaginatedService implements ITeacherListPaginatedService {
    
    private final ITeacherPaginatedRepository teacherRepository;

    @Override
    public PaginatedData<TeacherResponseDto> cu26ListTeachersPaginated(
        int page, 
        int size, 
        String orderBy, 
        String orderType,
        String search, 
        List<String> filters, 
        List<String> filterValues) {
        
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Teacher> spec = Specification.where(null); //Quite reestriccion notDeleted
        
        if (search != null && !search.isBlank()) {
        spec = spec.and(TeacherSpecs.nameOrLastnameContains(search));
        }
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(TeacherSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<Teacher> pageResult = teacherRepository.findAll(spec, pageable);

        List<TeacherResponseDto> dtos = TeacherMapper.toListDto(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }

}
