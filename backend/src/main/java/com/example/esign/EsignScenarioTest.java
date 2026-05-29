package com.example.esign;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.esign.client.EsignClient;
import com.example.esign.config.EsignConfig;

import java.io.File;
import java.util.UUID;

/**
 * 赛力斯人事场景联调 — 按 doc/esign-test.md 流程
 * 1.  查内部组织详情 → organizationCode
 * 2.  查印章列表 → sealId + 用印人 userCode
 * 3.  查外部用户 → 不存在则新建(4), 存在则按需修改(5)
 * 4.  新建外部用户
 * 5.  修改外部用户
 * 6.1 生成上传文件链接
 * 6.2 上传文件
 * 6.3 发起签署 → signFlowId + signUrl
 * 9. 文件删除 (签署完成后)
 */
public class EsignScenarioTest {

    // ========== TODO: 替换为真实数据 ==========
    static final String LICENSE_NO         = "915001066608898456";    // 统一社会信用代码
    static final String EMP_NAME           = "Gary";
    static final String EMP_MOBILE         = "13621799196";
    static final String EMP_LICENSE_NO     = "342501199209196417";   // 身份证号
    static final String EMP_CUSTOM_ACCOUNT = "13621799196";        // 我们自己的员工记录key, 如手机号，客户系统账号
    static final String BUSINESS_TYPE_CODE = "d57044ecc471e12387838d1f5bbd78cf"; // 业务类型编码，对方给，不同系统不一样的值

    public static void main(String[] args) {
        EsignConfig config = new EsignConfig();
        config.setHost("https://esign-test.seres.cn");
        config.setProjectId("1000007");
        config.setProjectSecret("7l3cIaTfR4LiKwzQ");

        EsignClient client = new EsignClient(config);

        try {
            // ===== 1. 查询内部组织详情 =====
            System.out.println(">>> 1. 查询内部组织详情");
            JSONObject orgQuery = new JSONObject();
            orgQuery.put("licenseNo", LICENSE_NO);
            orgQuery.put("licenseType", "CREDIT_CODE");

            String orgResult = client.doPost("/manage/v1/innerOrganizations/detail", orgQuery);
            JSONArray orgList = JSONObject.parseObject(orgResult).getJSONArray("data");
            if (orgList == null || orgList.isEmpty()) throw new RuntimeException("组织不存在");
            JSONObject orgData = orgList.getJSONObject(0);
            String organizationCode = orgData.getString("organizationCode");
            System.out.println("orgList size : " + orgList.size());
            System.out.println("组织编码: " + organizationCode);
            System.out.println("userCode: " + orgData.getString("userCode"));

            // ===== 2. 查询印章列表 =====
            System.out.println("\n>>> 2. 查询企业授权印章列表");
            JSONObject sealQuery = new JSONObject();
            sealQuery.put("organizationCode", organizationCode);
            sealQuery.put("sealPattern", "1");            // 商密印章
            sealQuery.put("sealTypeCode", "COMMON-SEAL"); // 公章
            sealQuery.put("pageNo", 1);
            sealQuery.put("pageSize", 10);

            String sealResult = client.doPost("/seals/v1/sealcontrols/organizationSeals/list", sealQuery);
            JSONObject sealData = JSONObject.parseObject(sealResult).getJSONObject("data");
            JSONArray sealRecords = sealData.getJSONArray("records");
            if (sealRecords == null || sealRecords.isEmpty()) throw new RuntimeException("未找到公章");
            JSONObject sealRecord = sealRecords.getJSONObject(0);
            String sealId = sealRecord.getString("sealId");
            // 用印人
            JSONArray signers = sealRecord.getJSONArray("sealsignersInfos");
            String sealUserCode = (signers != null && !signers.isEmpty())
                    ? signers.getJSONObject(0).getString("userCode") : "";
            System.out.println("sealRecord size: " + sealRecord.size());
            System.out.println("印章ID: " + sealId);
            System.out.println("用印人: " + sealUserCode);

            // ===== 3. 查询外部用户 =====
            System.out.println("\n>>> 3. 查询外部用户详情");
            JSONObject userQuery = new JSONObject();
            userQuery.put("mobile", EMP_MOBILE);
            //userQuery.put("name", EMP_NAME);//不需要传

            String userResult = client.doPost("/manage/v1/outerUsers/detail", userQuery);
            System.out.println("/outerUsers/detail api return userResult = " + userResult);
            JSONArray userList = JSONObject.parseObject(userResult).getJSONArray("data");

            String userCode;
            if (userList == null || userList.isEmpty()) {
                // 4. 不存在 → 新建
                System.out.println("\n>>> 4. 新建外部用户");
                JSONObject newUser = new JSONObject();
                newUser.put("customAccountNo", EMP_CUSTOM_ACCOUNT);
                newUser.put("name", EMP_NAME);
                newUser.put("mobile", EMP_MOBILE);
                newUser.put("licenseNo", EMP_LICENSE_NO);
                newUser.put("licenseType", "ID_CARD");

                String createResult = client.doPostRaw("/manage/v1/outerUsers/create",
                        "[" + newUser.toJSONString() + "]");

                JSONObject createJson = JSONObject.parseObject(createResult);
                JSONArray successData = createJson.getJSONObject("data").getJSONArray("successData");
                userCode = successData.getJSONObject(0).getString("userCode");
                System.out.println("新建成功, userCode=" + userCode);
            } else {
                // 已存在 → 直接用
                userCode = userList.getJSONObject(0).getString("userCode");
                System.out.println("用户已存在, userCode=" + userCode);
            }
            // ===== 6.1 生成上传文件链接 =====
            System.out.println("\n>>> 6.1 生成上传文件链接");
            String requestID = UUID.randomUUID().toString().replace("-", "").toUpperCase();

            JSONObject urlBody = new JSONObject();
            urlBody.put("requestID", requestID);
            urlBody.put("type", 0);
            urlBody.put("expire", 300);

            String urlResult = client.doPost("/file/v1/generateUploadUrl", urlBody);
            String uploadUrl = JSONObject.parseObject(urlResult).getJSONObject("data").getString("url");
            // 替换内网IP为域名
            uploadUrl = uploadUrl.replaceFirst("https?://[^/]+", config.getHost());
            System.out.println("上传地址: " + uploadUrl);

            // ===== 6.2 上传文件 =====
            System.out.println("\n>>> 6.2 上传文件");
            File file = resolvePdf("pdf/test.pdf");
            System.out.println("待上传文件: " + file.getAbsolutePath());

            JSONObject uploadExtra = new JSONObject();
            uploadExtra.put("file", file.getName());
            String uploadResponse = client.uploadFileToUrl(uploadUrl, file, uploadExtra);
            String fileKey = JSONObject.parseObject(uploadResponse).getJSONObject("data").getString("fileKey");
            System.out.println("上传成功, fileKey=" + fileKey);

            // ===== 6.3 发起签署 =====
            System.out.println("\n>>> 6.3 发起签署");

            JSONObject startBody = new JSONObject();
            startBody.put("subject", "劳动合同签署-" + EMP_NAME);
            startBody.put("businessNo", "hr_" + System.currentTimeMillis());
            startBody.put("businessTypeCode", BUSINESS_TYPE_CODE);

            // 发起方
            JSONObject initiatorInfo = new JSONObject();
            initiatorInfo.put("organizationCode", organizationCode);
            initiatorInfo.put("userCode", sealUserCode);
            initiatorInfo.put("userType", 1);//固定传1
            startBody.put("initiatorInfo", initiatorInfo);

            // 签署文件
            JSONObject signFile = new JSONObject();
            signFile.put("fileKey", fileKey);
            signFile.put("fileOrder", 1);//签字的时候，签字文件顺序
            startBody.put("signFiles", java.util.Collections.singletonList(signFile));

            // 签署方 — 员工（用户，个人相对方）
            JSONObject signerInfo = new JSONObject();
            signerInfo.put("userCode", userCode);
            signerInfo.put("customAccountNo", EMP_CUSTOM_ACCOUNT);
            signerInfo.put("userType", 2);          // 2=相对方（外部用户）
            signerInfo.put("signNode", 1);          // 最小为1，默认传1
            signerInfo.put("signOrder", 1); //如果员工先签就1， 如果先盖章再签字就是2。
            signerInfo.put("autoSign", 0); //是否自动盖章 0：不自动盖章， 1：自动盖章
            signerInfo.put("signMode", 0);          // 0=顺序签
            startBody.put("signerInfos", java.util.Collections.singletonList(signerInfo));
            startBody.put("signNotifyUrl","");

            String startResult = client.doPost("/esign-signs/v1/signFlow/createAndStart", startBody);
            JSONObject startJson = JSONObject.parseObject(startResult);
            JSONObject startData = startJson.getJSONObject("data");
            String signFlowId = startData.getString("signFlowId");
            System.out.println("signFlowId: " + signFlowId);

            // 响应里直接返回签署链接
            JSONArray signUrlInfos = startData.getJSONArray("signUrlInfos");
            String signUrl = "";
            if (signUrlInfos != null && !signUrlInfos.isEmpty()) {
                signUrl = signUrlInfos.getJSONObject(0).getString("signUrl");
                // 内网IP替换为域名
                signUrl = signUrl.replaceFirst("https?://[^/]+", config.getHost());
                System.out.println("签署链接: " + signUrl);
            }

            // ===== 结果汇总 =====
            System.out.println("\n============================================");
            System.out.println("  organizationCode: " + organizationCode);
            System.out.println("  sealId:           " + sealId);
            System.out.println("  userCode:         " + userCode);
            System.out.println("  signFlowId:       " + signFlowId);
            System.out.println("  签署链接:         " + signUrl);
            System.out.println("============================================");
        } catch (Exception e) {
            System.err.println("流程失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 解析待上传 PDF。不依赖具体的工作目录：
     * 1. 依次尝试常见工作目录基准（backend/ 本身、项目根、上级目录）
     * 2. 兜底从 classpath（src/main/resources）读取，拷到临时文件
     * 这样无论从 IDEA（工作目录=项目根）还是命令行（工作目录=backend）启动都能找到。
     */
    private static File resolvePdf(String relativePath) throws Exception {
        String[] candidates = {
                relativePath,              // 工作目录就是 backend/
                "backend/" + relativePath, // 工作目录是项目根
                "../" + relativePath,      // 工作目录是某个子模块
        };
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists()) return f.getAbsoluteFile();
        }

        // classpath 兜底
        String name = relativePath.substring(relativePath.lastIndexOf('/') + 1);
        try (java.io.InputStream in = EsignScenarioTest.class.getClassLoader().getResourceAsStream(name)) {
            if (in != null) {
                File tmp = File.createTempFile("esign-", "-" + name);
                tmp.deleteOnExit();
                java.nio.file.Files.copy(in, tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return tmp;
            }
        }

        throw new RuntimeException("找不到文件 " + relativePath
                + "（当前工作目录=" + new File(".").getAbsolutePath() + "）");
    }

}
