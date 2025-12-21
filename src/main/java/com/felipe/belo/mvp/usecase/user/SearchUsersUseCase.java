package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import org.springframework.data.domain.Page;

public interface SearchUsersUseCase {
    Page<User> search(SearchRequestDTO request);
}
