package com.ferry.module.member.controller.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferry.module.member.api.dto.MemberLoginReq;
import com.ferry.module.member.api.dto.MemberLoginResp;
import com.ferry.module.member.api.dto.MemberProfileResp;
import com.ferry.module.member.service.MemberAuthService;
import com.ferry.module.member.service.MemberIntegralService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppMemberControllerTest {

    private MockMvc mockMvc;
    private MemberAuthService memberAuthService;
    private MemberIntegralService memberIntegralService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        memberAuthService = mock(MemberAuthService.class);
        memberIntegralService = mock(MemberIntegralService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new AppMemberController(memberAuthService, memberIntegralService)).build();
    }

    @Test
    void login_returnsToken() throws Exception {
        when(memberAuthService.login(any())).thenReturn(new MemberLoginResp("token-123", 1L));

        mockMvc.perform(post("/app-api/member/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MemberLoginReq("wx_code"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.accessToken").value("token-123"));
    }

    @Test
    void profile_returnsMemberInfo() throws Exception {
        when(memberAuthService.profile()).thenReturn(new MemberProfileResp(1L, "用户", "https://a.com/1.png", 100));

        mockMvc.perform(get("/app-api/member/profile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.nickname").value("用户"));
    }
}
