package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContractWorkOrderFieldsTest {

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void queryContractWorkOrderFields() throws Exception {
        JsonNode resp = serviceGoClient.queryFields("contractWorkOrder");
        System.out.println("=== 查询合同工单字段定义 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
