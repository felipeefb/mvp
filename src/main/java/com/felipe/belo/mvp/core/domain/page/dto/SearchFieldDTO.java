package com.felipe.belo.mvp.core.domain.page.dto;

public record SearchFieldDTO(
        String field,
        String operation,
        String value
) {}