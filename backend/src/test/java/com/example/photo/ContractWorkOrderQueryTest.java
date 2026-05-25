package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContractWorkOrderQueryTest {

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void queryContractWorkOrderById() throws Exception {
        JsonNode resp = serviceGoClient.queryDataById(162870171L, "contractWorkOrder");
        System.out.println("=== 按 ID 查询合同工单 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
