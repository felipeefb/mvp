package com.felipe.belo.mvp.application.permissions;

import com.felipe.belo.mvp.application.permissions.controller.PermissionsController;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PermissionsControllerTest {

    @Test
    void listGroupedPermissions_ReturnsGroups() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PermissionsController()).build();

        mockMvc.perform(get("/api/v1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ROLE").exists())
                .andExpect(jsonPath("$.USER").exists());
    }
}
