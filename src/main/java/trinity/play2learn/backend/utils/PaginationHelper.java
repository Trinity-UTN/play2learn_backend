package trinity.play2learn.backend.utils;

import java.util.List;

import org.springframework.data.domain.Page;

import trinity.play2learn.backend.configs.response.PaginatedData;

public class PaginationHelper {

    public static <E, D> PaginatedData<D> fromPage(Page<E> page, List<D> dtoList) {
        return PaginatedData.<D>builder()
                .results(dtoList)
                .count((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .build();
    }
}
