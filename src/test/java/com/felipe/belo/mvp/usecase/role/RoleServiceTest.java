package com.felipe.belo.mvp.usecase.role;

import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.core.service.CurrentUserService;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import com.felipe.belo.mvp.usecase.role.port.RoleRepository;
import com.felipe.belo.mvp.usecase.role.service.RoleService;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    RoleRepository roleRepository;

    FakeCurrentUserService currentUserService;

    RoleService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentUserService = new FakeCurrentUserService();
        service = new RoleService(roleRepository, currentUserService);
    }

    @Test
    void create_WhenNameAlreadyExists_ThrowsConflict() {
        when(roleRepository.findByNormalizedName("Admins")).thenReturn(Optional.of(new Role()));

        assertThatThrownBy(() -> service.create(new CreateRoleCommand("Admins", EnumSet.of(Permissions.ROLE_CREATE))))
                .isInstanceOf(BusinessException.class)
                .hasMessage(I18nConstants.MESSAGE_ROLE_NAME_EXISTS)
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(roleRepository, never()).save(any());
    }

    @Test
    void create_Success() {
        when(roleRepository.findByNormalizedName("Managers")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            // simulate id set by DB
            return inv.<Role>getArgument(0);
        });

        Role role = service.create(new CreateRoleCommand("Managers", EnumSet.of(Permissions.ROLE_LIST)));
        assertThat(role.getName()).isEqualTo("Managers");
        assertThat(role.getPermissions()).containsExactly(Permissions.ROLE_LIST);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void update_WhenNotFound_ThrowsNotFound() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, new UpdateRoleCommand("X", null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND)
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_WhenNameAlreadyExists_ThrowsConflict() {
        UUID id = UUID.randomUUID();
        Role existing = new Role();
        existing.setId(id);
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));

        Role other = new Role();
        other.setId(UUID.randomUUID());
        when(roleRepository.findByNormalizedName("New")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> service.update(id, new UpdateRoleCommand("New", EnumSet.of(Permissions.ROLE_UPDATE))))
                .isInstanceOf(BusinessException.class)
                .hasMessage(I18nConstants.MESSAGE_ROLE_NAME_EXISTS)
                .extracting("status").isEqualTo(HttpStatus.CONFLICT);

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void update_Success() {
        UUID id = UUID.randomUUID();
        Role existing = new Role();
        existing.setName("Old");
        existing.setId(id);
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.findByNormalizedName("New")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

        Role role = service.update(id, new UpdateRoleCommand("New", EnumSet.of(Permissions.ROLE_UPDATE)));
        assertThat(role.getName()).isEqualTo("New");
        assertThat(role.getPermissions()).containsExactly(Permissions.ROLE_UPDATE);
        verify(roleRepository).save(existing);
    }

    @Test
    void findById_WhenNotFound_ThrowsNotFound() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(BusinessException.class)
                .hasMessage(I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND);
    }

    @Test
    void findById_Success() {
        UUID id = UUID.randomUUID();
        Role role = new Role();
        role.setName("Admins");
        role.setPermissions(EnumSet.of(Permissions.ROLE_READ));
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        Role result = service.findById(id);
        assertThat(result.getName()).isEqualTo("Admins");
        assertThat(result.getPermissions()).containsExactly(Permissions.ROLE_READ);
    }

    @Test
    void delete_SetsSoftDeleteFields() {
        UUID id = UUID.randomUUID();
        Role role = new Role();
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "n", "e@x.com", "p", null);
        currentUserService.setCurrentUser(Optional.of(user));

        service.delete(id);

        verify(roleRepository).save(any(Role.class));
        assertThat(role.getDeletedAt()).isNotNull();
        assertThat(role.getDeletedBy()).isEqualTo(userId);
    }

    @Test
    void findAll_DelegatesToRepository() {
        Role r1 = new Role();
        r1.setName("A");
        when(roleRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(r1));

        List<Role> list = service.findAll();
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getName()).isEqualTo("A");
    }
    // --- helpers & stubs ---
    static class FakeCurrentUserService extends CurrentUserService {
        private Optional<User> current = Optional.empty();

        FakeCurrentUserService() { super(null); }

        void setCurrentUser(Optional<User> u) { this.current = u; }

        @Override
        public Optional<User> getCurrentUser() {
            return current;
        }
    }
}