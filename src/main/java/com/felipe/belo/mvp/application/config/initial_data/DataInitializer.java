package com.felipe.belo.mvp.application.config.initial_data;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.role.port.RoleRepository;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Development-time data initializer for seeding roles and users.
 */
@Configuration
public class DataInitializer {

    /**
     * Default constructor.
     */
    public DataInitializer() {}

    /**
     * Seeds the database with initial roles and users if they do not exist.
     *
     * @param userRepository repository to manage users
     * @param roleRepository repository to manage roles
     * @param passwordEncoder encoder to hash passwords
     * @return a command-line runner that performs the seeding at startup
     */
    @Bean
    @Transactional
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          RoleRepository roleRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = createRoleIfNotFound(roleRepository, "SUPER_ADMIN", new HashSet<>(List.of(Permissions.values())));
            Role userRole = createRoleIfNotFound(roleRepository, "USER", new HashSet<>(List.of(Permissions.USER_LIST, Permissions.ROLE_LIST)));

            createUserIfNotFound(userRepository, "admin", "admin@admin.com", "admin", adminRole, passwordEncoder);
            createUserIfNotFound(userRepository, "user", "user@user.com", "user", userRole, passwordEncoder);

        };
    }

    /**
     * Creates a role if it does not exist.
     *
     * @param roleRepository repository to query and save
     * @param name role name
     * @param permissions permissions to assign
     * @return the existing or newly created role
     */
    private Role createRoleIfNotFound(RoleRepository roleRepository, String name, Set<Permissions> permissions) {
        return roleRepository.findByNormalizedName(name).orElseGet(() -> {
            Role role = new Role(null, name, permissions);
            return roleRepository.save(role);
        });
    }

    /**
     * Creates a user with the given data if it does not exist.
     *
     * @param userRepository repository to query and save
     * @param name display name
     * @param email e-mail address
     * @param password raw password
     * @param role role to assign
     * @param passwordEncoder encoder for hashing the password
     */
    private void createUserIfNotFound(UserRepository userRepository, String name, String email, String password, Role role, PasswordEncoder passwordEncoder) {
        if (userRepository.findByEmailIgnoreCase(email).isEmpty()) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        }
    }
}
