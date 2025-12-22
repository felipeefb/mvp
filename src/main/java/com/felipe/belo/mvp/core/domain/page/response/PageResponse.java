package com.felipe.belo.mvp.core.domain.page.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Standardized page response wrapper used by controllers.
 *
 * @param content       page content
 * @param page          current page index
 * @param size          page size
 * @param totalElements total number of elements
 * @param totalPages    total page count
 * @param first         whether this is the first page
 * @param last          whether this is the last page
 * @param <T>           element type
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    /**
     * Builds a response from a Spring Data page.
     *
     * @param page the source page
     * @param <T>  element type
     * @return mapped response
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
