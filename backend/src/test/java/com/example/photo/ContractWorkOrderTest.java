package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContractWorkOrderTest {

    private static final int FILTER_ID = 241618;

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void queryContractWorkOrderList() throws Exception {
        JsonNode resp = serviceGoClient.queryList("contractWorkOrder", FILTER_ID, 1, 10);
        System.out.println("=== 合同工单列表查询 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
