package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntryWorkOrderUpdateTest {

    private static final long DATA_ID = 162352692L;

    @Autowired
    private ServiceGoClient serviceGoClient;

    @Test
    void updateEntryWorkOrder() throws Exception {
        String fieldDataListJson = """
                [
                    {
                        "fieldApiName": "foreignName",
                        "fieldTypeApiName": "field_type_single_line",
                        "fieldValue": "yangjunping-Gary5"
                    }
                ]
                """;

        JsonNode resp = serviceGoClient.updateData("entryWorkOrder", DATA_ID, fieldDataListJson);
        System.out.println("=== 更新入职工单 ===");
        System.out.println("Response: " + resp.toPrettyString());
    }
}
