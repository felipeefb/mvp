package com.felipe.belo.mvp.role;

import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.service.CurrentUserService;
import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.entity.Role;
import com.felipe.belo.mvp.role.mapper.RoleMapper;
import com.felipe.belo.mvp.role.repository.RoleRepository;
import com.felipe.belo.mvp.role.service.RoleService;
import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.utils.I18nConstants;
import com.felipe.belo.mvp.utils.permissions.Permissions;
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

    RoleMapper mapper = new com.felipe.belo.mvp.role.mapper.RoleMapperImpl();

    RoleService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentUserService = new FakeCurrentUserService();
        service = new RoleService(roleRepository, mapper, currentUserService);
    }

    @Test
    void create_WhenNameAlreadyExists_ThrowsConflict() {
        when(roleRepository.findByNormalizedName("Admins")).thenReturn(Optional.of(new Role()));

        assertThatThrownBy(() -> service.create(new CreateRoleDto("Admins", EnumSet.of(Permissions.ROLE_CREATE))))
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
            Role r = inv.getArgument(0);
            // simulate id set by DB
            return r;
        });

        RoleDto dto = service.create(new CreateRoleDto("Managers", EnumSet.of(Permissions.ROLE_LIST)));
        assertThat(dto.name()).isEqualTo("Managers");
        assertThat(dto.permissions()).containsExactly(Permissions.ROLE_LIST);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void update_WhenNotFound_ThrowsNotFound() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, new UpdateRoleDto("X", null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND)
                .extracting("status").isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_Success() {
        UUID id = UUID.randomUUID();
        Role existing = new Role();
        existing.setName("Old");
        // Simulate entity loaded from DB having the same id
        setPrivateField(existing, "id", id);
        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

        RoleDto dto = service.update(id, new UpdateRoleDto("New", EnumSet.of(Permissions.ROLE_UPDATE)));
        assertThat(dto.name()).isEqualTo("New");
        assertThat(dto.permissions()).containsExactly(Permissions.ROLE_UPDATE);
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

        RoleDto dto = service.findById(id);
        assertThat(dto.name()).isEqualTo("Admins");
        assertThat(dto.permissions()).containsExactly(Permissions.ROLE_READ);
    }

    @Test
    void delete_SetsSoftDeleteFields() {
        UUID id = UUID.randomUUID();
        Role role = new Role();
        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity(userId, "n", "e@x.com", "p", null, null, null);
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

        List<RoleListDto> list = service.findAll();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).name()).isEqualTo("A");
    }
    // --- helpers & stubs ---
    static class FakeCurrentUserService extends CurrentUserService {
        private Optional<UserEntity> current = Optional.empty();

        FakeCurrentUserService() { super(null); }

        void setCurrentUser(Optional<UserEntity> u) { this.current = u; }

        @Override
        public Optional<UserEntity> getCurrentUser() {
            return current;
        }
    }

    static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}