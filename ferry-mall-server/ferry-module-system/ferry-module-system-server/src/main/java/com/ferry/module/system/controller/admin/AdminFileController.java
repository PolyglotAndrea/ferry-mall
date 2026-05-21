package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.system.api.dto.FileResp;
import com.ferry.module.system.service.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/admin-api/system/file")
public class AdminFileController {

    private final FileStorageService fileStorageService;

    public AdminFileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public CommonResult<FileResp> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
            ? original.substring(original.lastIndexOf(".")) : "";
        String path = LocalDate.now() + "/" + UUID.randomUUID() + ext;

        FileResp resp = fileStorageService.upload(path, file.getInputStream(), file.getSize(), file.getContentType());
        return CommonResult.success(resp);
    }

    @GetMapping("/download")
    public void download(@RequestParam String path, HttpServletResponse response) throws Exception {
        try (InputStream is = fileStorageService.download(path)) {
            response.setContentType("application/octet-stream");
            String filename = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            is.transferTo(response.getOutputStream());
        }
    }

    @DeleteMapping("/{path}")
    public CommonResult<Boolean> delete(@PathVariable String path) {
        fileStorageService.delete(path);
        return CommonResult.success(true);
    }
}
