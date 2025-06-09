package trinity.play2learn.backend.configs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PaginatedData<T> {
    private final List<T> results;
    private final int count;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
}