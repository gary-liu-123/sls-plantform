package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContractWorkOrderQueryByPhoneTest {

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void queryContractWorkOrderByPhone() throws Exception {
        JsonNode resp = serviceGoClient.queryByUniqueField("contractWorkOrder", "phone", "15651818750");
        System.out.println("=== 按手机号查询合同工单 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
