package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用 POST /api/v1/datas/search 按多条件查询合同工单：
 *   personalPhone = 15651818750 AND contractStatus != "合同办理完成"
 *
 * 字段类型对应的操作符（来自接口规范）：
 *   - personalPhone（电话）：is_any / not_any / contains_any ...
 *   - contractStatus（单选）：is / not / is_any / not_any ...
 *
 * filterId 复用 ContractWorkOrderTest 已验证过的 241618。
 */
@SpringBootTest
class ContractWorkOrderSearchByPhoneAndStatusTest {

    private static final int FILTER_ID = 241618;

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void searchContractWorkOrderByPhoneAndStatus() throws Exception {
        String conditionList = "["
                + "{\"fieldApiName\":\"Phone\",\"operator\":\"is_any\",\"value\":\"15651818750\"},"
                + "{\"fieldApiName\":\"contractStatus\",\"operator\":\"not\",\"value\":\"合同办理完成\"}"
                + "]";

        JsonNode resp = serviceGoClient.searchData("contractWorkOrder", FILTER_ID, conditionList, 1, 20);
        System.out.println("=== 合同工单 search: phone=15651818750 AND contractStatus != 合同办理完成 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
