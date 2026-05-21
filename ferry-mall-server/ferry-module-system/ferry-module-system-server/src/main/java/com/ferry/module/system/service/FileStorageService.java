package com.ferry.module.system.service;

import com.ferry.module.system.api.dto.FileResp;

import java.io.InputStream;

public interface FileStorageService {

    FileResp upload(String path, InputStream inputStream, long size, String contentType);

    InputStream download(String path);

    void delete(String path);

    String getUrl(String path);
}
