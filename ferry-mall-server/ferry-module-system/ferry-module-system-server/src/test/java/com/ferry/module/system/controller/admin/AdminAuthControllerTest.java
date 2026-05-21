package com.ferry.module.system.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferry.module.system.api.dto.AdminLoginReq;
import com.ferry.module.system.service.AdminAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAuthControllerTest {

    private MockMvc mockMvc;
    private AdminAuthService adminAuthService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        adminAuthService = mock(AdminAuthService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminAuthController(adminAuthService)).build();
    }

    @Test
    void login_returnsToken() throws Exception {
        when(adminAuthService.login(any(com.ferry.module.system.api.dto.AdminLoginReq.class)))
            .thenReturn(new com.ferry.module.system.api.dto.AdminLoginResp("mock-token", "管理员"));

        AdminLoginReq req = new AdminLoginReq("admin", "ferry123");

        mockMvc.perform(post("/admin-api/system/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.accessToken").value("mock-token"));
    }
}
