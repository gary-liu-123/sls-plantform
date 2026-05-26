package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntryWorkOrderFieldsTest {

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void queryEntryWorkOrderFields() throws Exception {
        JsonNode resp = serviceGoClient.queryFields("entryWorkOrder");
        System.out.println("=== 查询入职工单字段定义 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
