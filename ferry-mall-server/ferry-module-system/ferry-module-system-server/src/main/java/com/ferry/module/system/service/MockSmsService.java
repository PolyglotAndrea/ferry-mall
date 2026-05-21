package com.ferry.module.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "ferry.sms", name = "provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(MockSmsService.class);

    @Override
    public void sendVerifyCode(String mobile, String code) {
        log.info("[MOCK-SMS] 向 {} 发送验证码: {}", mobile, code);
    }
}
