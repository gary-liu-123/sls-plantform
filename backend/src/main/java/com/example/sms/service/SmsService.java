package com.example.sms.service;

import com.example.sms.config.SmsConfig;
import com.example.sms.dto.SmsSendRequest;
import com.example.sms.dto.SmsSendResponse;
import com.example.sms.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Shanghai"));

    private final RestClient restClient;
    private final SmsConfig smsConfig;

    private String cachedToken;
    private Instant tokenExpireTime;

    public SmsService(RestClient restClient, SmsConfig smsConfig) {
        this.restClient = restClient;
        this.smsConfig = smsConfig;
    }

    public String getToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpireTime)) {
            log.info("使用缓存的token，过期时间: {}", DATE_FORMATTER.format(tokenExpireTime));
            return cachedToken;
        }

        log.info("获取新token，接口地址: {}", smsConfig.getTokenUrl());

        TokenResponse response = restClient.post()
                .uri(smsConfig.getTokenUrl())
                .header("appkey", smsConfig.getAppKey())
                .header("appsecret", smsConfig.getAppSecret())
                .retrieve()
                .body(TokenResponse.class);

        log.info("Token响应: code={}, msgid={}, expire={}", response.getCode(), response.getMsgid(), response.getExpire());

        if ("200".equals(response.getCode())) {
            cachedToken = response.getToken();
            tokenExpireTime = Instant.ofEpochMilli(response.getExpire()).minusSeconds(300);
            log.info("Token已缓存，将在 {} 过期", DATE_FORMATTER.format(tokenExpireTime));
            return cachedToken;
        }

        throw new RuntimeException("获取token失败: " + response.getCode());
    }

    public SmsSendResponse sendSms(String phone, String templateId) {
        String token = getToken();

        SmsSendRequest request = new SmsSendRequest();
        SmsSendRequest.RequestData requestData = new SmsSendRequest.RequestData();

        SmsSendRequest.Head head = new SmsSendRequest.Head();
        head.setConsumer(smsConfig.getConsumer());
        requestData.setHead(head);

        SmsSendRequest.ListItem item = new SmsSendRequest.ListItem();
        item.setPhone(phone);
        item.setTemplateId(templateId);
        requestData.setList(java.util.List.of(item));

        request.setRequest(requestData);

        log.info("发送短信，手机号: {}, 模板ID: {}", phone, templateId);

        SmsSendResponse response = restClient.post()
                .uri(smsConfig.getSendUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("token", token)
                .body(request)
                .retrieve()
                .body(SmsSendResponse.class);

        log.info("短信发送响应: code={}, onlyKey={}, msgid={}",
                response.getCode(), response.getOnlyKey(), response.getMsgid());

        return response;
    }
}
