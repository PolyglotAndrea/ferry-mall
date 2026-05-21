package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.system.api.dto.FileResp;
import com.ferry.module.system.dal.dataobject.SysFileDO;
import com.ferry.module.system.dal.mapper.SysFileMapper;
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
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/admin-api/system/file")
public class AdminFileController {

    private final FileStorageService fileStorageService;
    private final SysFileMapper sysFileMapper;

    public AdminFileController(FileStorageService fileStorageService, SysFileMapper sysFileMapper) {
        this.fileStorageService = fileStorageService;
        this.sysFileMapper = sysFileMapper;
    }

    @PostMapping("/upload")
    public CommonResult<FileResp> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
            ? original.substring(original.lastIndexOf(".")) : "";
        String path = LocalDate.now() + "/" + UUID.randomUUID() + ext;

        FileResp resp = fileStorageService.upload(path, file.getInputStream(), file.getSize(), file.getContentType());

        SysFileDO record = new SysFileDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setName(original != null ? original : path);
        record.setPath(path);
        record.setUrl(resp.url());
        record.setContentType(file.getContentType());
        record.setSize(file.getSize());
        record.setCreatedAt(LocalDateTime.now());
        sysFileMapper.insert(record);

        return CommonResult.success(resp);
    }

    @GetMapping("/page")
    public CommonResult<PageResult<SysFileDO>> page(PageParam pageParam) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysFileDO> page = sysFileMapper.selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysFileDO>()
                .eq(TenantContext.getTenantId() != null, SysFileDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(SysFileDO::getId));
        return CommonResult.success(PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize()));
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

    @DeleteMapping("/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        SysFileDO record = sysFileMapper.selectById(id);
        if (record != null) {
            fileStorageService.delete(record.getPath());
            sysFileMapper.deleteById(id);
        }
        return CommonResult.success(true);
    }
}
