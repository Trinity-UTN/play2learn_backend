package trinity.play2learn.backend.profile.avatar.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IAspectListPaginatedService {
    
    public PaginatedData<AspectResponseDto> cu76listAspectsPaginated (int page, 
        int size, 
        String orderBy,
        String orderType, 
        String search, 
        List<String> filters, 
        List<String> filterValues,
        User User
    );
}
