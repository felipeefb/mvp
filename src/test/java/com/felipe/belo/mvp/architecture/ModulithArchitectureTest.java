package com.felipe.belo.mvp.architecture;

import com.felipe.belo.mvp.MvpApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies Spring Modulith boundaries: modules must only use exports of other modules.
 */
class ModulithArchitectureTest {

    @Test
    void modulesRespectNamedInterfaces() {
        ApplicationModules.of(MvpApplication.class).verify();
    }
}
