package com.ferry.module.member.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ferry.wx.miniapp")
public class WxMiniappProperties {
    private String appId;
    private String secret;
    private boolean mock = true;
}
