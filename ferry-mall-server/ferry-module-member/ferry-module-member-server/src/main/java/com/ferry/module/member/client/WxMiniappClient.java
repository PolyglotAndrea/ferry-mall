package com.ferry.module.member.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.member.config.WxMiniappProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WxMiniappClient {

    private static final String JSCODE2SESSION_URL =
        "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code";

    private final WxMiniappProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WxMiniappClient(WxMiniappProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public JsCode2SessionResp jscode2session(String code) {
        if (properties.isMock()) {
            return new JsCode2SessionResp("mock_openid_" + code, "mock_session_key_" + code, null);
        }
        ResponseEntity<String> resp = restTemplate.getForEntity(
            JSCODE2SESSION_URL, String.class,
            properties.getAppId(), properties.getSecret(), code);
        try {
            JsonNode node = objectMapper.readTree(resp.getBody());
            if (node.has("errcode") && node.get("errcode").asInt() != 0) {
                throw new FerryBusinessException(500, "微信登录失败: " + node.get("errmsg").asText());
            }
            return new JsCode2SessionResp(
                node.get("openid").asText(),
                node.get("session_key").asText(),
                node.has("unionid") ? node.get("unionid").asText() : null);
        } catch (Exception e) {
            throw new FerryBusinessException(500, "微信响应解析失败");
        }
    }

    public record JsCode2SessionResp(String openid, String sessionKey, String unionid) {}
}
