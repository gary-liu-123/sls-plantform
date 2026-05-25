package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntryWorkOrderQueryByPhoneTest {

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void queryEntryWorkOrderByPhone() throws Exception {
        JsonNode resp = serviceGoClient.queryByUniqueField("entryWorkOrder", "personalPhone", "15651818750");
        System.out.println("=== 按手机号查询入职工单 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
