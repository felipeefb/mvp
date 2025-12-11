package com.felipe.belo.mvp.core.domain.page.dto;

import java.util.List;

public record PageableDto(
        int page,
        int size,
        Boolean includeDeleted,
        List<SearchFieldDTO> searchFields
) {
    public PageableDto {
        // valores default: page=0, size=20, includeDeleted=false se não informados
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (includeDeleted == null) includeDeleted = false;

    }
}