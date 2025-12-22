package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import org.springframework.data.domain.Page;

/**
 * Use case for searching users with filters/pagination.
 */
public interface SearchUsersUseCase {
    /**
     * Executes a search.
     *
     * @param request search and paging parameters
     * @return paged users
     */
    Page<User> search(SearchRequestDto request);
}
