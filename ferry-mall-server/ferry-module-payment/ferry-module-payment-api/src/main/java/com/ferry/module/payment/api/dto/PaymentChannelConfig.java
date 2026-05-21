package com.ferry.module.payment.api.dto;

public record PaymentChannelConfig(
    String appId,
    String merchantNo,
    String apiKey,
    String privateKey,
    String publicKey,
    String notifyUrl
) {}
