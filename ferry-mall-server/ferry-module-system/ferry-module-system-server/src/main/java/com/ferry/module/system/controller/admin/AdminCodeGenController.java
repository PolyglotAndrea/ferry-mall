package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.system.service.CodeGenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-api/system/codegen")
public class AdminCodeGenController {

    private final CodeGenService codeGenService;

    public AdminCodeGenController(CodeGenService codeGenService) {
        this.codeGenService = codeGenService;
    }

    @GetMapping("/tables")
    @RequirePermission("system:codegen:list")
    public CommonResult<List<Map<String, Object>>> tables() {
        return CommonResult.success(codeGenService.listTables());
    }

    @GetMapping("/columns")
    @RequirePermission("system:codegen:list")
    public CommonResult<List<Map<String, Object>>> columns(@RequestParam String tableName) {
        return CommonResult.success(codeGenService.listColumns(tableName));
    }

    @GetMapping("/preview")
    @RequirePermission("system:codegen:preview")
    public CommonResult<String> preview(@RequestParam String tableName,
                                        @RequestParam(defaultValue = "com.ferry.module") String packageName) {
        return CommonResult.success(codeGenService.generateEntity(tableName, packageName));
    }
}
