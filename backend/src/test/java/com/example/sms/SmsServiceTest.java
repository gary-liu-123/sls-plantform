package com.example.sms;

import com.example.photo.PhotoApplication;
import com.example.sms.config.SmsConfig;
import com.example.sms.dto.SmsSendResponse;
import com.example.sms.service.SmsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PhotoApplication.class)
@DisplayName("短信服务测试")
public class SmsServiceTest {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceTest.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private SmsConfig smsConfig;

    @Test
    @DisplayName("测试发送短信")
    void testSendSms() {
        log.info("执行：发送短信测试");
        log.info("Token接口: {}", smsConfig.getTokenUrl());
        log.info("发送接口: {}", smsConfig.getSendUrl());

        String phone = "13621799196|key=1";
        String templateId = "00037625";

        try {
            SmsSendResponse response = smsService.sendSms(phone, templateId);

            assertNotNull(response, "响应不应为空");
            assertNotNull(response.getCode(), "响应code不应为空");

            log.info("响应Code: {}", response.getCode());
            log.info("响应OnlyKey: {}", response.getOnlyKey());
            log.info("响应Msgid: {}", response.getMsgid());

            if ("0".equals(response.getCode())) {
                log.info("短信发送成功");
            } else {
                log.warn("短信发送失败: {}", response.getMsgid());
            }

        } catch (Exception e) {
            log.error("发送短信异常: {}", e.getMessage());
            fail("发送短信异常: " + e.getMessage());
        }
    }
}
