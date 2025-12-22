package com.felipe.belo.mvp.core.domain.page.dto;

/**
 * Represents a single filter condition for searching.
 *
 * @param field     name of the field to filter
 * @param operation comparison operator (equals, contains, etc.)
 * @param value     string value to compare against
 */
public record SearchFieldDto(
        String field,
        String operation,
        String value
) {
}
