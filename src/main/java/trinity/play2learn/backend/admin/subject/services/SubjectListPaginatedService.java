package trinity.play2learn.backend.admin.subject.services;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectPaginatedRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectListPaginatedService;
import trinity.play2learn.backend.admin.subject.specs.SubjectSpecs;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class SubjectListPaginatedService implements ISubjectListPaginatedService {
    
    private final ISubjectPaginatedRepository subjectRepository;

    @Override
    public PaginatedData<SubjectResponseDto> cu32ListSubjectsPaginated(
        int page, 
        int size, 
        String orderBy, 
        String orderType,
        String search, 
        List<String> filters, 
        List<String> filterValues) {
        
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);
        Specification<Subject> spec = Specification.where(SubjectSpecs.notDeleted());
        
        if (search != null && !search.isBlank()) {
        spec = spec.and(SubjectSpecs.nameContains(search));
        }
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                spec = spec.and(SubjectSpecs.genericFilter(filters.get(i), filterValues.get(i)));
            }
        }

        Page<Subject> pageResult = subjectRepository.findAll(spec, pageable);

        List<SubjectResponseDto> dtos = SubjectMapper.toDtoList(pageResult.getContent());

        return PaginationHelper.fromPage(pageResult, dtos);
    }
}
