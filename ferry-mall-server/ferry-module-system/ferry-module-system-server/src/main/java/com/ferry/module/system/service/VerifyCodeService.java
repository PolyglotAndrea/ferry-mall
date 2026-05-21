package com.ferry.module.system.service;

import com.ferry.framework.redis.config.RedisKeyspace;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerifyCodeService {

    private static final String KEY_PREFIX = "verify_code:";
    private final StringRedisTemplate redisTemplate;

    public VerifyCodeService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generate(String mobile) {
        String code = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(KEY_PREFIX + mobile, code, 5, TimeUnit.MINUTES);
        return code;
    }

    public boolean verify(String mobile, String code) {
        String stored = redisTemplate.opsForValue().get(KEY_PREFIX + mobile);
        if (stored == null) {
            return false;
        }
        boolean matched = stored.equals(code);
        if (matched) {
            redisTemplate.delete(KEY_PREFIX + mobile);
        }
        return matched;
    }
}
