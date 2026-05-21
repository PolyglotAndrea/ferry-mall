package com.ferry.module.system.service;

public interface SmsService {

    void sendVerifyCode(String mobile, String code);
}
