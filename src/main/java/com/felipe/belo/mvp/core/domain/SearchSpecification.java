package com.felipe.belo.mvp.core.domain;

import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDto;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Base builder for JPA Specifications based on search criteria.
 */
public final class SearchSpecification {

    private SearchSpecification() {
    }

    /**
     * Builds a JPA specification from a search request and field mapper.
     *
     * @param request search request containing filters
     * @param mapper  mapper with allowed fields and aliases
     * @param <E>     entity type
     * @return specification representing filters
     */
    public static <E> Specification<E> build(SearchRequestDto request, SearchFieldMapper mapper) {
        return (Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<SearchFieldDto> filters = request == null ? List.of() : request.filters();
            Map<String, String> aliases = mapper == null ? Map.of() : mapper.fieldAliases();
            Set<String> allowed = mapper == null ? Set.of() : mapper.allowedFields();

            if (filters != null) {
                for (SearchFieldDto filter : filters) {
                    if (filter == null || filter.value() == null) {
                        continue;
                    }

                    String operation = normalizeOperation(filter.operation());
                    Path<?> fieldPath = resolveFieldPath(root, filter.field(), aliases, allowed);
                    if (fieldPath == null) {
                        continue;
                    }

                    Object value = convertValue(filter.value(), fieldPath.getJavaType());
                    String rawValue = filter.value();

                    switch (operation) {
                        case "contains" -> {
                            if (fieldPath.getJavaType() == String.class) {
                                predicates.add(cb.like(cb.lower(fieldPath.as(String.class)),
                                        "%" + rawValue.toLowerCase() + "%"));
                            }
                        }
                        case ">" -> predicates.add(buildGreaterThanPredicate(cb, fieldPath, value));
                        case "<" -> predicates.add(buildLessThanPredicate(cb, fieldPath, value));
                        default -> predicates.add(cb.equal(fieldPath, value));
                    }
                }
            }

            if (request == null || !request.includeDeletedOrDefault()) {
                Path<?> deletedAt = resolveFieldPath(root, "deletedAt", aliases, allowed);
                if (deletedAt != null) {
                    predicates.add(cb.isNull(deletedAt));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String normalizeOperation(String operation) {
        if (operation == null || operation.isBlank()) {
            return "equals";
        }
        String normalized = operation.trim().toLowerCase();
        if ("=".equals(normalized)) {
            return "equals";
        }
        if (":".equals(normalized)) {
            return "contains";
        }
        return normalized;
    }

    @SuppressWarnings("unchecked")
    private static Predicate buildGreaterThanPredicate(CriteriaBuilder cb, Path<?> fieldPath, Object value) {
        Class<?> type = fieldPath.getJavaType();
        if (Number.class.isAssignableFrom(type)) {
            return cb.gt(fieldPath.as(Number.class), (Number) value);
        }
        if (Comparable.class.isAssignableFrom(type) && value instanceof Comparable<?> comparable) {
            @SuppressWarnings("unchecked")
            Path<? extends Comparable<Object>> comparablePath = (Path<? extends Comparable<Object>>) fieldPath;
            return cb.greaterThan(comparablePath, (Comparable<Object>) comparable);
        }
        return cb.conjunction();
    }

    @SuppressWarnings("unchecked")
    private static Predicate buildLessThanPredicate(CriteriaBuilder cb, Path<?> fieldPath, Object value) {
        Class<?> type = fieldPath.getJavaType();
        if (Number.class.isAssignableFrom(type)) {
            return cb.lt(fieldPath.as(Number.class), (Number) value);
        }
        if (Comparable.class.isAssignableFrom(type) && value instanceof Comparable<?> comparable) {
            @SuppressWarnings("unchecked")
            Path<? extends Comparable<Object>> comparablePath = (Path<? extends Comparable<Object>>) fieldPath;
            return cb.lessThan(comparablePath, (Comparable<Object>) comparable);
        }
        return cb.conjunction();
    }

    private static <E> Path<?> resolveFieldPath(Root<E> root, String field, Map<String, String> aliases, Set<String> allowedFields) {
        if (field == null || field.isBlank()) {
            return null;
        }

        String resolved = aliases.getOrDefault(field, field);
        if (!allowedFields.isEmpty() && !allowedFields.contains(resolved)) {
            return null;
        }

        String[] parts = resolved.split("\\.");
        Path<?> path = root;
        try {
            for (String part : parts) {
                path = path.get(part);
            }
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return path;
    }

    private static Object convertValue(String valueStr, Class<?> targetType) {
        if (valueStr == null) {
            return null;
        }
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
        } else if (targetType == UUID.class) {
            return UUID.fromString(valueStr);
        }
        return valueStr;
    }
}
