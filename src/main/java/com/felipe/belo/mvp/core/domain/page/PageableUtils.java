package com.felipe.belo.mvp.core.domain.page;

import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Helper methods to build pageable and sorting objects from request DTOs.
 */
public final class PageableUtils {

    private PageableUtils() {
    }

    /**
     * Builds a {@link Pageable} from a search request applying defaults.
     *
     * @param searchRequest search parameters
     * @return pageable instance
     */
    public static Pageable from(SearchRequestDto searchRequest) {
        int page = searchRequest == null ? 0 : searchRequest.pageOrDefault();
        int size = searchRequest == null ? 20 : searchRequest.sizeOrDefault();
        Sort sort = parseSort(searchRequest == null ? null : searchRequest.sort());
        return PageRequest.of(page, size, sort);
    }

    /**
     * Parses a sort expression in the form "field,direction".
     *
     * @param sortValue raw sort string
     * @return sort configuration
     */
    public static Sort parseSort(String sortValue) {
        if (sortValue == null || sortValue.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sortValue.split(",", 2);
        String field = parts[0].trim();
        if (field.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && !parts[1].isBlank()) {
            direction = Sort.Direction.fromString(parts[1].trim());
        }
        return Sort.by(direction, field);
    }
}
