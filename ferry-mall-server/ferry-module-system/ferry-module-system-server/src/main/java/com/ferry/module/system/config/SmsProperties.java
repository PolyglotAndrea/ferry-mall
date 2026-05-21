package com.ferry.module.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ferry.sms")
public class SmsProperties {
    private String provider = "mock";
    private String accessKeyId;
    private String accessKeySecret;
    private String signName = "FerryMall";
    private String verifyCodeTemplate = "SMS_12345678";
}
