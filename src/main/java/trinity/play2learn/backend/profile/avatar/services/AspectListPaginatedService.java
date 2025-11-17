package trinity.play2learn.backend.profile.avatar.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.avatar.models.Aspect;
import trinity.play2learn.backend.profile.avatar.repositories.IAspectRepository;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectListPaginatedService;
import trinity.play2learn.backend.profile.avatar.specs.AspectSpecs;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileContainsAspectService;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.utils.PaginationHelper;
import trinity.play2learn.backend.utils.PaginatorUtils;

@Service
@AllArgsConstructor
public class AspectListPaginatedService implements IAspectListPaginatedService {

    private final IAspectRepository aspectRepository;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IProfileContainsAspectService profileContainsAspectService;
    
    @Override
    @Transactional(readOnly = true)
    public PaginatedData<AspectResponseDto> cu76listAspectsPaginated(
        int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues, 
        User User
    ) {

        Student student = studentGetByEmailService.getByEmail(User.getEmail());
        
        Pageable pageable = PaginatorUtils.buildPageable(page, size, orderBy, orderType);

        // Base specs: solo no eliminados y disponibles
        Specification<Aspect> spec = Specification
            .where(AspectSpecs.notDeleted())
            .and(AspectSpecs.onlyAvailable());

        // Filtro por búsqueda textual
        if (search != null && !search.isBlank()) {
            spec = spec.and(AspectSpecs.nameContains(search));
        }

        // Filtros dinámicos adicionales
        if (filters != null && filterValues != null && filters.size() == filterValues.size()) {
            for (int i = 0; i < filters.size(); i++) {
                String campo = filters.get(i);
                String valor = filterValues.get(i);

                if (campo.equalsIgnoreCase("type")) {
                    spec = spec.and(AspectSpecs.typeEquals(valor));
                } else {
                    spec = spec.and(AspectSpecs.genericFilter(campo, valor));
                }
            }
        }

        Page<Aspect> pageResult = aspectRepository.findAll(spec, pageable);
        
        List<AspectResponseDto> dtos =  new ArrayList<>();

        for (Aspect aspect : pageResult.getContent()) {
            dtos.add(AspectMapper.toMarketDto(aspect, profileContainsAspectService.execute(student.getProfile(), aspect)));
        }

        return PaginationHelper.fromPage(pageResult, dtos);
    }
    
}
