package com.felipe.belo.mvp.core.domain.page.request;

import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates pagination, sorting, and filter criteria for searches.
 *
 * @param filters        list of field filters
 * @param page           page number (0-based)
 * @param size           page size
 * @param sort           sort expression ("field,dir")
 * @param includeDeleted whether to include soft-deleted records
 */
public record SearchRequestDto(List<SearchFieldDto> filters,
                               Integer page,
                               Integer size,
                               String sort,
                               Boolean includeDeleted
) {
    /**
     * Applies defaults to a search request.
     */
    public SearchRequestDto {
        filters = filters == null ? new ArrayList<>() : filters;
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
        includeDeleted = includeDeleted != null && includeDeleted;
    }

    /**
     * Computes page number with defaults applied.
     *
     * @return page or zero when missing/invalid
     */
    public int pageOrDefault() {
        if (page == null || page < 0) {
            return 0;
        }
        return page;
    }

    /**
     * Computes size with defaults applied.
     *
     * @return size or default 20 when missing/invalid
     */
    public int sizeOrDefault() {
        if (size == null || size < 1) {
            return 20;
        }
        return size;
    }

    /**
     * Determines whether soft-deleted records should be included.
     *
     * @return true when includeDeleted is explicitly true
     */
    public boolean includeDeletedOrDefault() {
        return Boolean.TRUE.equals(includeDeleted);
    }
}
