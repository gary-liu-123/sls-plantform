package com.example.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmsSendResponse {
    @JsonProperty("code")
    private String code;

    @JsonProperty("onlyKey")
    private String onlyKey;

    @JsonProperty("msgid")
    private String msgid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOnlyKey() {
        return onlyKey;
    }

    public void setOnlyKey(String onlyKey) {
        this.onlyKey = onlyKey;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
