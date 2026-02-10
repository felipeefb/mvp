package com.felipe.belo.mvp.application.config.initial_data;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.role.port.RoleRepository;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private PasswordEncoder encoder;
    private DataInitializer initializer;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        userRepository = mock(UserRepository.class);
        encoder = new PasswordEncoder() {
            @Override public String encode(CharSequence rawPassword) { return "enc-" + rawPassword; }
            @Override public boolean matches(CharSequence rawPassword, String encodedPassword) { return encodedPassword.equals("enc-" + rawPassword); }
        };
        initializer = new DataInitializer();
    }

    @Test
    void seedsRolesAndUsersWhenMissing() throws Exception {
        when(roleRepository.findByNormalizedName("SUPER_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByNormalizedName("USER")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });
        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        initializer.initDatabase(userRepository, roleRepository, encoder).run();

        verify(roleRepository, times(2)).save(any(Role.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void skipsExistingUsersAndRoles() throws Exception {
        Role existingAdmin = new Role(UUID.randomUUID(), "SUPER_ADMIN", new HashSet<>());
        when(roleRepository.findByNormalizedName("SUPER_ADMIN")).thenReturn(Optional.of(existingAdmin));
        when(roleRepository.findByNormalizedName("USER")).thenReturn(Optional.of(new Role(UUID.randomUUID(), "USER", Set.of(Permissions.USER_LIST))));
        when(userRepository.findByEmailIgnoreCase("admin@admin.com")).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmailIgnoreCase("user@user.com")).thenReturn(Optional.of(new User()));

        initializer.initDatabase(userRepository, roleRepository, encoder).run();

        verify(roleRepository, never()).save(any(Role.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
