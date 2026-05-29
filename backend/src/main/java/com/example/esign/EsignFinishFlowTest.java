package com.example.esign;

import com.alibaba.fastjson2.JSONObject;
import com.example.esign.client.EsignClient;
import com.example.esign.config.EsignConfig;

/**
 *
 * 结束签署流程 —— POST /esign-signs/v1/signFlow/finish
 *
 * 场景：所有签署/盖章节点都走完之后，调用本接口主动结束签署流程。
 * 结束后的签署流程不允许再添加/删除签章区域，也不允许撤销。
 *
 * 请求体（signFlowId / businessNo 至少传一个）：
 *   {
 *     "businessNo": "我是编码",
 *     "signFlowId": "我是流程id啊！"
 *   }
 *
 * 响应：
 *   {
 *     "data": { "businessNo": "", "signFlowId": "", "signFlowStatus": 0 },
 *     "message": "成功",
 *     "code": 200
 *   }
 *
 * 依赖 EsignScenarioTest 跑完后拿到的数据：
 *   SIGN_FLOW_ID —— 签署流程id（EsignScenarioTest 中 createAndStart 返回的 signFlowId）
 */
public class EsignFinishFlowTest {

    // ========== TODO: 替换为 EsignScenarioTest 跑出来的真实数据 ==========
    static final String SIGN_FLOW_ID = "";   // 签署流程id
    static final String BUSINESS_NO  = "";   // 第三方签署流程业务id（可选，与 signFlowId 二选一即可）

    public static void main(String[] args) {
        EsignConfig config = new EsignConfig();
        //yml 配置
        config.setHost("https://esign-test.seres.cn");
        config.setProjectId("1000007");
        config.setProjectSecret("7l3cIaTfR4LiKwzQ");

        EsignClient client = new EsignClient(config);

        try {
            System.out.println(">>> 结束签署流程, signFlowId=" + SIGN_FLOW_ID);

            JSONObject body = new JSONObject();
            if (SIGN_FLOW_ID != null && !SIGN_FLOW_ID.isEmpty()) {
                body.put("signFlowId", SIGN_FLOW_ID);
            }
            if (BUSINESS_NO != null && !BUSINESS_NO.isEmpty()) {
                body.put("businessNo", BUSINESS_NO);
            }

            JSONObject result = client.finishFlow(body);
            System.out.println("接口返回: " + result.toJSONString());

            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                System.out.println("signFlowId: " + data.getString("signFlowId"));
                System.out.println("businessNo: " + data.getString("businessNo"));
                System.out.println("signFlowStatus: " + data.getInteger("signFlowStatus"));
            }

            System.out.println("\n============================================");
            System.out.println("  签署流程已结束, signFlowId=" + SIGN_FLOW_ID);
            System.out.println("============================================");

        } catch (Exception e) {
            System.err.println("结束签署流程失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
