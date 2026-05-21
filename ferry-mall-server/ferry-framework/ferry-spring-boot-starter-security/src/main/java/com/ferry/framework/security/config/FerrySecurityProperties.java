package com.ferry.framework.security.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ferry.security")
public class FerrySecurityProperties {
    private boolean enabled;
    private String jwtSecret = "ferry-mall-secret-key-ferry-mall-secret-key";
    private List<String> permitAll = List.of("/admin-api/system/auth/login", "/app-api/member/auth/login", "/doc.html", "/v3/api-docs/**", "/swagger-ui/**", "/actuator/**");

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
    public List<String> getPermitAll() { return permitAll; }
    public void setPermitAll(List<String> permitAll) { this.permitAll = permitAll; }
}
