package com.ferry.module.member.controller.app;

import com.ferry.framework.web.core.CommonResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/app-api/member/bind")
public class AppMemberBindController {

    private static final String SMS_CODE_KEY = "sms:code:";
    private static final long SMS_CODE_EXPIRE_MINUTES = 5;

    private final StringRedisTemplate redisTemplate;
    private final MemberUserMapper memberUserMapper;

    public AppMemberBindController(StringRedisTemplate redisTemplate,
                                   MemberUserMapper memberUserMapper) {
        this.redisTemplate = redisTemplate;
        this.memberUserMapper = memberUserMapper;
    }

    @PostMapping("/send-code")
    public CommonResult<Void> sendCode(@RequestParam String mobile) {
        String code = String.valueOf((int) (Math.random() * 9000 + 1000));
        String key = SMS_CODE_KEY + mobile;
        redisTemplate.opsForValue().set(key, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return CommonResult.success(null);
    }

    @PostMapping("/phone")
    public CommonResult<Void> bindPhone(@RequestBody BindPhoneReq req) {
        String key = SMS_CODE_KEY + req.mobile();
        String cachedCode = redisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(req.code())) {
            throw new FerryBusinessException(400, "验证码错误或已过期");
        }
        MemberUserDO member = memberUserMapper.selectById(10001L);
        if (member == null) {
            throw new FerryBusinessException(404, "会员不存在");
        }
        member.setMobile(req.mobile());
        memberUserMapper.updateById(member);
        redisTemplate.delete(key);
        return CommonResult.success(null);
    }

    public record BindPhoneReq(String mobile, String code) {
    }
}
