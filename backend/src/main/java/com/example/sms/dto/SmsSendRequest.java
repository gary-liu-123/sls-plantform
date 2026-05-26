package com.example.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SmsSendRequest {
    @JsonProperty("Request")
    private RequestData requestData;

    public RequestData getRequest() {
        return requestData;
    }

    public void setRequest(RequestData requestData) {
        this.requestData = requestData;
    }

    public static class RequestData {
        @JsonProperty("Head")
        private Head head;

        @JsonProperty("List")
        private List<ListItem> list;

        public Head getHead() {
            return head;
        }

        public void setHead(Head head) {
            this.head = head;
        }

        public List<ListItem> getList() {
            return list;
        }

        public void setList(List<ListItem> list) {
            this.list = list;
        }
    }

    public static class Head {
        @JsonProperty("Consumer")
        private String consumer;

        public String getConsumer() {
            return consumer;
        }

        public void setConsumer(String consumer) {
            this.consumer = consumer;
        }
    }

    public static class ListItem {
        @JsonProperty("phone")
        private String phone;

        @JsonProperty("templateid")
        private String templateId;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }
}
