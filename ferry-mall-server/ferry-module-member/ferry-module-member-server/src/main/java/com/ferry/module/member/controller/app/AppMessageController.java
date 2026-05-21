package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.module.member.dal.dataobject.MessageRecordDO;
import com.ferry.module.member.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app-api/member/message")
public class AppMessageController {

    private final MessageService messageService;

    public AppMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/list")
    public CommonResult<List<MessageRecordDO>> list() {
        return CommonResult.success(messageService.list(10001L));
    }

    @PostMapping("/{id}/read")
    public CommonResult<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return CommonResult.success(null);
    }

    @GetMapping("/unread-count")
    public CommonResult<Long> getUnreadCount() {
        return CommonResult.success(messageService.getUnreadCount(10001L));
    }
}
