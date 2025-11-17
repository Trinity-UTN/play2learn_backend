package trinity.play2learn.backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginatorUtils {
    public static Pageable buildPageable(int page, int pageSize, String orderBy, String orderType) {
        Sort.Direction direction = "desc".equalsIgnoreCase(orderType)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, orderBy);
        return PageRequest.of(Math.max(page - 1, 0), pageSize, sort);
    }    
}
