package com.example.esign;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.esign.client.EsignClient;
import com.example.esign.config.EsignConfig;

/**
 *
 * https://esign-test.seres.cn/doc-service-front/doc/lhw1voet25f1dndb
 *
 * 盖章 —— 向已有签署流程追加「企业用印」签署方
 *
 * 场景：员工签署完成后，合同需要企业盖章。
 * 调用 /esign-signs/v1/signFlow/signers/add，给流程新增一个「内部企业」签署方，
 * 在指定文件的指定位置加盖公章（sealInfos 描述「签署方 ↔ 签章位置 ↔ 待签文件」的关系）。
 *
 * 依赖 EsignScenarioTest 跑完后拿到的数据：
 *   SIGN_FLOW_ID      —— 签署流程id（EsignScenarioTest 中调用的 createAndStart 返回的 signFlowId）
 *   ORGANIZATION_CODE —— 内部组织编码（场景步骤1）
 *   SEAL_USER_CODE    —— 用印人 userCode（场景步骤2 印章列表）
 *   SEAL_ID           —— 公章id（场景步骤2）
 *   FILE_KEY          —— 待盖章文件 fileKey（场景步骤6.2 上传 / 流程里的 signFiles）
 */
public class EsignAddSealTest {

    // ========== TODO: 替换为 EsignScenarioTest 跑出来的真实数据 ==========
    static final String SIGN_FLOW_ID      = "";   // 签署流程id
    static final String ORGANIZATION_CODE = "";   // 内部组织编码
    static final String SEAL_USER_CODE    = "";   // 用印人 userCode
    static final String SEAL_ID           = "印章ID";   // 公章id
    static final String FILE_KEY          = "";   // 待盖章文件 fileKey

    // ========== 盖章位置/方式（按需调整）==========
    static final String  SIGN_PAGE_NO = "1";      // 盖章页码，支持 "1,2,3" 或 "4-6"
    static final double  SIGN_POS_X   = 200.0;    // 盖章坐标 X
    static final double  SIGN_POS_Y   = 200.0;    // 盖章坐标 Y
    static final int     SIGN_NODE    = 2;        // 签署节点（须 >= 当前签署中节点；员工在节点1，公司盖章放节点2）
    static final boolean AUTO_SIGN    = true;     // 是否静默签（自动盖章）。企业用印通常自动盖章，需提前完成印章授权

    public static void main(String[] args) {
        EsignConfig config = new EsignConfig();
        config.setHost("https://esign-test.seres.cn");
        config.setProjectId("1000007");
        config.setProjectSecret("7l3cIaTfR4LiKwzQ");

        EsignClient client = new EsignClient(config);

        try {
            System.out.println(">>> 向签署流程追加企业用印签署方（盖章）");

            // 签章位置配置
            JSONObject signConfig = new JSONObject();
            signConfig.put("sealId", SEAL_ID);        // 指定使用的公章
            signConfig.put("pageNo", SIGN_PAGE_NO);
            signConfig.put("posX", SIGN_POS_X);
            signConfig.put("posY", SIGN_POS_Y);
            signConfig.put("signFieldType", "1");     // 签章域
            signConfig.put("freeMode", 0);

            // 待签署文件 ↔ 签章位置关系
            JSONObject sealInfo = new JSONObject();
            sealInfo.put("fileKey", FILE_KEY);
            sealInfo.put("signConfigs", java.util.Collections.singletonList(signConfig));

            // 签署方（内部企业 —— 公司盖章）
            JSONObject signerInfo = new JSONObject();
            signerInfo.put("userType", 1);                          // 1=内部
            signerInfo.put("userCode", SEAL_USER_CODE);             // 用印人
            signerInfo.put("organizationCode", ORGANIZATION_CODE);  // 企业编码 → 企业签署
            signerInfo.put("autoSign", AUTO_SIGN);                  // 是否静默签（自动盖章）
            signerInfo.put("signNode", SIGN_NODE);                  // 签署节点
            signerInfo.put("signMode", 0);                          // 0=顺序签
            signerInfo.put("signOrder", 1);                         // 节点内顺序
            signerInfo.put("sealInfos", java.util.Collections.singletonList(sealInfo));

            JSONObject body = new JSONObject();
            body.put("signFlowId", SIGN_FLOW_ID);
            body.put("businessNo", "");
            body.put("signerInfos", java.util.Collections.singletonList(signerInfo));

            JSONObject result = client.addSigners(body);
            System.out.println("接口返回: " + result.toJSONString());

            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                System.out.println("signFlowId: " + data.getString("signFlowId"));
                JSONArray signUrlInfos = data.getJSONArray("signUrlInfos");
                if (signUrlInfos != null) {
                    for (int i = 0; i < signUrlInfos.size(); i++) {
                        JSONObject info = signUrlInfos.getJSONObject(i);
                        String signUrl = info.getString("signUrl");
                        if (signUrl != null && !signUrl.isEmpty()) {
                            signUrl = signUrl.replaceFirst("https?://[^/]+", config.getHost());
                        }
                        System.out.println("  用印人: " + info.getString("userName")
                                + " signerId=" + info.getString("signerId")
                                + " signUrl=" + signUrl);
                    }
                }
            }

            System.out.println("\n============================================");
            System.out.println("  盖章签署方已追加, signFlowId=" + SIGN_FLOW_ID);
            System.out.println("  AUTO_SIGN(自动盖章)=" + AUTO_SIGN);
            System.out.println("============================================");

        } catch (Exception e) {
            System.err.println("追加盖章签署方失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 *
 signatureType

 string

 否

 body

 指定签署印章类型：PERSON-SEAL-个人印章、COMMON-SEAL-企业授权印章、LEGAL-PERSON-SEAL-法人章

 **********
 企业授权印章


 sealId

 string

 否

 body

 印章id，需要校验用印权限；如果sealTypeCode、signatureType有值，需同时校验印章类型是否一致。

 PDF文件可使用国际标准印章、中国标准印章进行签署；OFD文件使用中国标准印章进行签署


 ***********
 String sealId = sealRecord.getString("sealId");



 signType

 string

 是

 body

 签署类型: COMMON-SIGN : 普通（包含单页签、多页签)， EDGE-SIGN:骑缝，KEYWORD-SIGN:关键字，默认COMMON-SIGN

 说明：自由签签署区（freeMode=1），签署类型支持同时传COMMON-SIGN,EDGE-SIGN，英文逗号","分隔




 signType

 string

 是

 body

 签署类型: COMMON-SIGN : 普通（包含单页签、多页签)， EDGE-SIGN:骑缝，KEYWORD-SIGN:关键字，默认COMMON-SIGN

 说明：自由签签署区（freeMode=1），签署类型支持同时传COMMON-SIGN,EDGE-SIGN，英文逗号","分隔

 **********
 EDGE-SIGN+KEYWORD-SIGN

 keywordInfo（点击“+”展开详情）

 object

 否

 body

 关键字定位信息集合


 ***


 sealSignDatePositionInfo（点击“+”展开详情）

 object

 否

 body

 签章日期信息

 **要确认一下是否要这个日期


 */