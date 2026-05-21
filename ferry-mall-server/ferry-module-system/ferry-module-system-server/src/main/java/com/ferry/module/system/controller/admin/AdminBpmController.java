package com.ferry.module.system.controller.admin;

import com.ferry.framework.web.annotation.RequirePermission;
import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.service.ProcessInstanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-api/bpm")
public class AdminBpmController {

    private final ProcessInstanceService processInstanceService;

    public AdminBpmController(ProcessInstanceService processInstanceService) {
        this.processInstanceService = processInstanceService;
    }

    @PostMapping("/deploy")
    @RequirePermission("bpm:deploy")
    public CommonResult<String> deploy(@RequestParam String name, HttpServletRequest request) throws Exception {
        try (InputStream is = request.getInputStream()) {
            return CommonResult.success(processInstanceService.deploy(name, is));
        }
    }

    @GetMapping("/definitions")
    @RequirePermission("bpm:definition:list")
    public CommonResult<List<ProcessDefinition>> definitions() {
        return CommonResult.success(processInstanceService.listDefinitions());
    }

    @PostMapping("/start/{key}")
    @RequirePermission("bpm:instance:start")
    public CommonResult<String> start(@PathVariable String key, @RequestBody Map<String, Object> variables) {
        return CommonResult.success(processInstanceService.start(key, variables));
    }

    @GetMapping("/tasks")
    @RequirePermission("bpm:task:list")
    public CommonResult<List<Map<String, Object>>> tasks(@RequestParam Long userId) {
        return CommonResult.success(processInstanceService.myTasks(userId));
    }

    @PostMapping("/task/{taskId}/complete")
    @RequirePermission("bpm:task:complete")
    public CommonResult<Boolean> complete(@PathVariable String taskId, @RequestBody Map<String, Object> variables) {
        processInstanceService.completeTask(taskId, variables);
        return CommonResult.success(true);
    }

    @GetMapping("/history/{processInstanceId}")
    @RequirePermission("bpm:history:list")
    public CommonResult<List<Map<String, Object>>> history(@PathVariable String processInstanceId) {
        return CommonResult.success(processInstanceService.history(processInstanceId));
    }
}
