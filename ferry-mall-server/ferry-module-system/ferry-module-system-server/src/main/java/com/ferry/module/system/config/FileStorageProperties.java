package com.ferry.module.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ferry.storage")
public class FileStorageProperties {
    private String type = "minio";
    private String endpoint = "http://localhost:9000";
    private String bucket = "ferry-mall";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String region = "us-east-1";
}
