package com.felipe.belo.mvp.core.domain.page.request;

import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDTO;

import java.util.List;

public record SearchRequestDTO(List<SearchFieldDTO> filters,
                               Integer page,
                               Integer size,
                               String sort,
                               Boolean includeDeleted
) {
    public SearchRequestDTO {
        if (page == null) page = 0;
        if (size == null) size = 20;
        if (includeDeleted == null) includeDeleted = false;
    }
}