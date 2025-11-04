package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.configs.response.PaginatedData;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitListUseRequestedPaginatedService {
    
    PaginatedData<BenefitPurchaseSimpleResponseDto> cu108ListUseRequestedPaginated(
        User user,
        int page, 
        int size, 
        String orderBy, 
        String orderType,
        String search, 
        List<String> filters, 
        List<String> filterValues
    );
}
