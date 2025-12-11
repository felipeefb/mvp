package com.felipe.belo.mvp.core.domain;

import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDTO;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building JPA Specifications based on search criteria.
 */
public class SearchSpecification {
    /**
     * Creates a JPA Specification from a search request.
     *
     * @param <E>     the entity type
     * @param request the search request containing filters and criteria
     * @return a Specification that applies all search filters
     */
    public static <E> Specification<E> bySearchCriteria(SearchRequestDTO request) {
        return (Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.filters() == null || request.filters().isEmpty()) {
                if (Boolean.FALSE.equals(request.includeDeleted())) {
                    try {
                        Path<?> deletedAtField = root.get("deletedAt");
                        predicates.add(cb.isNull(deletedAtField));
                    } catch (IllegalArgumentException e) {
                        // Entity doesn't have deletedAt field
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            for (SearchFieldDTO filter : request.filters()) {
                String field = filter.field();
                String operation = filter.operation() != null
                        ? filter.operation().toLowerCase()
                        : "equals";
                String valueStr = filter.value();
                if (valueStr == null) continue;

                Path<Object> fieldPath;
                try {
                    fieldPath = root.get(field);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                Class<?> attrType = fieldPath.getJavaType();
                Object value = convertValue(valueStr, attrType);

                switch (operation) {
                    case "equals", "=" ->
                            predicates.add(cb.equal(fieldPath, value));
                    case "contains", ":" -> {
                        if (attrType == String.class) {
                            predicates.add(cb.like(cb.lower(fieldPath.as(String.class)),
                                    "%" + valueStr.toLowerCase() + "%"));
                        }
                    }
                    case ">" -> {
                        if (Number.class.isAssignableFrom(attrType)) {
                            predicates.add(cb.gt(root.get(field), (Number) value));
                        } else if (attrType == LocalDate.class || attrType == LocalDateTime.class) {
                            predicates.add(cb.greaterThan(root.get(field), (Comparable) value));
                        }
                    }
                    case "<" -> {
                        if (Number.class.isAssignableFrom(attrType)) {
                            predicates.add(cb.lt(root.get(field), (Number) value));
                        } else if (attrType == LocalDate.class || attrType == LocalDateTime.class) {
                            predicates.add(cb.lessThan(root.get(field), (Comparable) value));
                        }
                    }
                }
            }

            if (Boolean.FALSE.equals(request.includeDeleted())) {
                try {
                    Path<?> deletedAtField = root.get("deletedAt");
                    predicates.add(cb.isNull(deletedAtField));
                } catch (IllegalArgumentException e) {
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Converts a string value to the target type.
     *
     * @param valueStr the string value
     * @param targetType the target class type
     * @return the converted value
     */
    private static Object convertValue(String valueStr, Class<?> targetType) {
        if (targetType == String.class) {
            return valueStr;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(valueStr);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(valueStr);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(valueStr);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(valueStr);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(valueStr);
        } else if (targetType == LocalDate.class) {
            return LocalDate.parse(valueStr);
        } else if (targetType == LocalDateTime.class) {
            return LocalDateTime.parse(valueStr);
        }
        return valueStr;
    }
}