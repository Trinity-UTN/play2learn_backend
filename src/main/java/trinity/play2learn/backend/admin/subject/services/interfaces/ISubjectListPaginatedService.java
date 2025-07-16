package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.configs.response.PaginatedData;

public interface ISubjectListPaginatedService {
    
    PaginatedData<SubjectResponseDto> cu32ListSubjectsPaginated(
        int page, int size, String orderBy, String orderType, 
        String search, List<String> filters, List<String> filterValues
    );
}
