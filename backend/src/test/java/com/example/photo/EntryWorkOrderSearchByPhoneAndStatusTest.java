package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用 POST /api/v1/datas/search 按多条件查询入职工单：
 *   personalPhone = 15651818750 AND orderStatus != "入职终止"
 *
 * 字段类型对应的操作符（来自接口规范）：
 *   - personalPhone（电话）：is_any / not_any / contains_any ...
 *   - orderStatus（单选）：is / not / is_any / not_any ...
 *
 * 注意：filterId 必填，需在 ServiceGo 后台为 entryWorkOrder 预配过滤器并把 ID 填到 FILTER_ID 常量。
 */
@SpringBootTest
class EntryWorkOrderSearchByPhoneAndStatusTest {

    /** TODO: 替换成 ServiceGo 后台为 entryWorkOrder 配置好的过滤器 ID */
    private static final int FILTER_ID = 0;

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void searchEntryWorkOrderByPhoneAndStatus() throws Exception {
        String conditionList = "["
                + "{\"fieldApiName\":\"personalPhone\",\"operator\":\"is_any\",\"value\":\"15651818750\"},"
                + "{\"fieldApiName\":\"orderStatus\",\"operator\":\"not\",\"value\":\"入职终止\"}"
                + "]";

        JsonNode resp = serviceGoClient.searchData("entryWorkOrder", FILTER_ID, conditionList, 1, 20);
        System.out.println("=== 入职工单 search: phone=15651818750 AND orderStatus != 入职终止 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
