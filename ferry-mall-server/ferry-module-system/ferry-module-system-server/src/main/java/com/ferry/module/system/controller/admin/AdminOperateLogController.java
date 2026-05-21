package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.system.dal.dataobject.SysOperateLogDO;
import com.ferry.module.system.service.OperateLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin-api/system/operate-log")
public class AdminOperateLogController {

    private final OperateLogService operateLogService;

    public AdminOperateLogController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @GetMapping("/page")
    public CommonResult<PageResult<SysOperateLogDO>> page(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer result,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            PageParam pageParam) {
        return CommonResult.success(operateLogService.page(module, name, result, startTime, endTime, pageParam));
    }

    @PostMapping("/clean")
    public CommonResult<Integer> clean(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime before) {
        int count = operateLogService.cleanExpiredLogs(before);
        return CommonResult.success(count);
    }
}
