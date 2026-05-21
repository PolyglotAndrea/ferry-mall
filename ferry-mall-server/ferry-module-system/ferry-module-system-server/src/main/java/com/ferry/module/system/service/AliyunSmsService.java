package com.ferry.module.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.config.SmsProperties;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.UUID;

@Service
@ConditionalOnProperty(prefix = "ferry.sms", name = "provider", havingValue = "aliyun")
public class AliyunSmsService implements SmsService {

    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String ACTION = "SendSms";
    private static final String VERSION = "2017-05-25";
    private static final String SIGN_METHOD = "HMAC-SHA1";
    private static final String FORMAT = "JSON";

    private final SmsProperties properties;

    public AliyunSmsService(SmsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void sendVerifyCode(String mobile, String code) {
        try {
            JSONObject templateParam = new JSONObject();
            templateParam.put("code", code);
            sendSms(mobile, properties.getVerifyCodeTemplate(), templateParam.toJSONString());
        } catch (Exception e) {
            throw new FerryBusinessException(500, "短信发送失败: " + e.getMessage());
        }
    }

    private void sendSms(String phoneNumbers, String templateCode, String templateParam) throws Exception {
        java.util.Map<String, String> params = new java.util.TreeMap<>();
        params.put("AccessKeyId", properties.getAccessKeyId());
        params.put("Action", ACTION);
        params.put("SignName", properties.getSignName());
        params.put("TemplateCode", templateCode);
        params.put("TemplateParam", templateParam);
        params.put("PhoneNumbers", phoneNumbers);
        params.put("Format", FORMAT);
        params.put("Version", VERSION);
        params.put("Timestamp", getTimestamp());
        params.put("SignatureMethod", SIGN_METHOD);
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", UUID.randomUUID().toString());

        String stringToSign = "GET&" + percentEncode("/") + "&" + percentEncode(buildQueryString(params));
        String signature = sign(stringToSign, properties.getAccessKeySecret() + "&");
        params.put("Signature", signature);

        String url = "https://" + DOMAIN + "/?" + buildQueryString(params);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int code = conn.getResponseCode();
        try (java.io.InputStream is = code == 200 ? conn.getInputStream() : conn.getErrorStream();
             java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONObject resp = JSON.parseObject(sb.toString());
            if (resp.containsKey("Code") && !"OK".equals(resp.getString("Code"))) {
                throw new RuntimeException(resp.getString("Message"));
            }
        }
    }

    private String buildQueryString(java.util.Map<String, String> params) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append("&");
            sb.append(percentEncode(entry.getKey())).append("=").append(percentEncode(entry.getValue()));
        }
        return sb.toString();
    }

    private String percentEncode(String value) throws Exception {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8)
            .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return sdf.format(new Date());
    }

    private String sign(String stringToSign, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return Base64.encodeBase64String(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
    }
}
