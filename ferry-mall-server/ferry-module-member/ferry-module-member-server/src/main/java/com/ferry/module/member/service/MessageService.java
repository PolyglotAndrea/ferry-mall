package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MessageRecordDO;
import com.ferry.module.member.dal.mapper.MessageRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final MessageRecordMapper messageRecordMapper;

    public MessageService(MessageRecordMapper messageRecordMapper) {
        this.messageRecordMapper = messageRecordMapper;
    }

    public List<MessageRecordDO> list(Long memberId) {
        return messageRecordMapper.selectList(
            new LambdaQueryWrapper<MessageRecordDO>()
                .eq(MessageRecordDO::getMemberId, memberId)
                .eq(MessageRecordDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(MessageRecordDO::getCreatedAt));
    }

    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long messageId) {
        messageRecordMapper.update(null,
            new LambdaUpdateWrapper<MessageRecordDO>()
                .eq(MessageRecordDO::getId, messageId)
                .set(MessageRecordDO::getIsRead, 1));
    }

    public long getUnreadCount(Long memberId) {
        return messageRecordMapper.selectCount(
            new LambdaQueryWrapper<MessageRecordDO>()
                .eq(MessageRecordDO::getMemberId, memberId)
                .eq(MessageRecordDO::getTenantId, TenantContext.getTenantId())
                .eq(MessageRecordDO::getIsRead, 0));
    }
}
