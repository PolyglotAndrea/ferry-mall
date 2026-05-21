package com.ferry.module.system.service;

import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.api.dto.FileResp;
import com.ferry.module.system.config.FileStorageProperties;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(prefix = "ferry.storage", name = "type", havingValue = "minio", matchIfMissing = true)
public class MinioFileStorageService implements FileStorageService {

    private final FileStorageProperties properties;
    private MinioClient minioClient;

    public MinioFileStorageService(FileStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
            .endpoint(properties.getEndpoint())
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            .build();
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            }
        } catch (Exception e) {
            throw new FerryBusinessException(500, "MinIO 初始化失败: " + e.getMessage());
        }
    }

    @Override
    public FileResp upload(String path, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(path)
                .stream(inputStream, size, -1)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build());
            return new FileResp(path, getUrl(path), size, contentType);
        } catch (Exception e) {
            throw new FerryBusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream download(String path) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(path)
                .build());
        } catch (Exception e) {
            throw new FerryBusinessException(500, "文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String path) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(properties.getBucket())
                .object(path)
                .build());
        } catch (Exception e) {
            throw new FerryBusinessException(500, "文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String getUrl(String path) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(properties.getBucket())
                .object(path)
                .method(Method.GET)
                .expiry(7, TimeUnit.DAYS)
                .build());
        } catch (Exception e) {
            return properties.getEndpoint() + "/" + properties.getBucket() + "/" + path;
        }
    }
}
