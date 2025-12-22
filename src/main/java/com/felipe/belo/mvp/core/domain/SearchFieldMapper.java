package com.felipe.belo.mvp.core.domain;

import java.util.Map;
import java.util.Set;

/**
 * Exposes metadata for search filtering: which fields are allowed and any alias mapping.
 */
public interface SearchFieldMapper {
    /**
     * Returns the set of field paths that can be filtered.
     *
     * @return allowed field names
     */
    Set<String> allowedFields();

    /**
     * Returns aliases mapping external field names to actual entity paths.
     *
     * @return alias map
     */
    Map<String, String> fieldAliases();
}
