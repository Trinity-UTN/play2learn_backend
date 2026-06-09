package trinity.play2learn.backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginatorUtils {
    public static Pageable buildPageable(int page, int pageSize, String orderBy, String orderType) {
        return buildPageableWithSortPrefix(page, pageSize, orderBy, orderType, null);
    }

    public static Pageable buildPageableWithSortPrefix(int page, int pageSize, String orderBy, String orderType,
            String sortPrefix) {
        Sort.Direction direction = "desc".equalsIgnoreCase(orderType)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        String sortProperty = sortPrefix != null && !sortPrefix.isBlank()
                ? sortPrefix + "." + orderBy
                : orderBy;

        Sort sort = Sort.by(direction, sortProperty);
        return PageRequest.of(Math.max(page - 1, 0), pageSize, sort);
    }
}
