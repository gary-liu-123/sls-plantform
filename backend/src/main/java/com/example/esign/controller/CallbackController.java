package com.example.esign.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 接收请求的文档
 * https://esign-test.seres.cn/doc-service-front/doc/gsl4cibmgigcxiwd
 *
 * 响应的文档
 * https://esign-test.seres.cn/doc-service-front/doc/oc6paupq64kbun6h
 *
 * 电子签签署流程回调入口。
 * 平台在签署流程状态变更时回调本接口；本类重点：
 * 在「签署方签署完成（签名/盖章）」与「签署流程完成」事件里打印文件日志。
 *
 * 注：callBackEnum 事件类型后续可能新增，这里只对需要的类型做处理，
 *     其余类型忽略，避免新增类型影响现有业务（按官方兼容性建议）。
 */
@RestController
@RequestMapping("/api/esign/callback")
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    /** callBackEnum -> Action 事件名称 */
    private static final Map<Integer, String> ACTIONS = Map.ofEntries(
            Map.entry(0, "SIGN_FLOW_START（签署环节开启）"),
            Map.entry(1, "SIGN_SIGNER_SIGNED（签署方签署完成）"),
            Map.entry(2, "SIGN_FLOW_FINISH（签署流程完成）"),
            Map.entry(3, "SIGN_FLOW_OVERTIME（签署流程过期）"),
            Map.entry(4, "SIGN_FLOW_EXPIRE_REMIND（签署截止前提醒）"),
            Map.entry(5, "SIGN_FLOW_REFUSE（签署方拒签）"),
            Map.entry(6, "SIGN_FLOW_CANCEL（签署流程作废）"),
            Map.entry(9, "SIGNED_FLOW_SIGN_FAILED（签署失败）"),
            Map.entry(11, "SIGN_FLOW_TRANSMISS（签署方转交）"),
            Map.entry(14, "SIGN_FLOW_CONTRACT_EXPIRE（合同到期提醒）"));

    /** 签署状态码 -> 含义 */
    private static final Map<String, String> FLOW_STATUS = Map.ofEntries(
            Map.entry("0", "草稿"), Map.entry("1", "签署中"), Map.entry("2", "完成"),
            Map.entry("3", "已过期"), Map.entry("4", "已拒签"), Map.entry("5", "已作废"),
            Map.entry("6", "作废中"), Map.entry("7", "填写中"), Map.entry("8", "填写完成"),
            Map.entry("14", "部分作废"));
    /**
     * 签署流程回调。平台以 POST + JSON Body 调用。
     * 始终返回 success，避免平台因失败重试风暴。
     */
    @PostMapping({"/signNotify", ""})
    public JSONObject signNotify(@RequestBody JSONObject payload) {
        try {
            int callBackEnum = payload.getIntValue("callBackEnum", -1);
            String callBackDesc = payload.getString("callBackDesc");
            String action = ACTIONS.getOrDefault(callBackEnum, "未知类型(" + callBackEnum + ")");

            JSONObject vo = payload.getJSONObject("callBackProcessVO");
            String processId = vo == null ? null : vo.getString("processId");
            String flowStatus = vo == null ? null : vo.getString("flowStatus");

            log.info("==== 收到电子签回调 ==== callBackEnum={} action={} desc={} processId={} flowStatus={}({})",
                    callBackEnum, action, callBackDesc, processId,
                    flowStatus, FLOW_STATUS.getOrDefault(flowStatus, "?"));

            // 仅处理与签名/盖章相关的事件，打印文件日志；其余类型忽略
            switch (callBackEnum) {
                case 1 -> // 签署方签署完成（签名/盖章动作）
                        logSignerSigned(vo);
                case 2 -> // 签署流程完成（全部签字盖章结束，落最终签署后文件）
                        logFlowFinish(vo);
                default ->
                        log.info("回调类型 {} 无需处理文件日志，忽略", action);
            }
        } catch (Exception e) {
            // 回调处理异常不能影响给平台的 200 应答
            log.error("处理电子签回调异常: {}", e.getMessage(), e);
        }

        // 按 e签宝要求应答：HTTP 200 + body code=0 表示回调消息推送成功
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("msg", "success");
        return resp;
    }
    /**
     * 签署方签署完成（callBackEnum=1）：打印本次完成签署的签署方及其文件。
     * 文件里包含本签署方刚盖的章 sealIdList、文件下载地址等。
     */
    private void logSignerSigned(JSONObject vo) {
        if (vo == null) return;

        // 优先用 currentCompletedSigner（本次回调由其签署行为触发）定位是谁签了
        JSONArray current = vo.getJSONArray("currentCompletedSigner");
        if (current != null && !current.isEmpty()) {
            for (int i = 0; i < current.size(); i++) {
                JSONObject s = current.getJSONObject(i);
                log.info("[签名/盖章完成] 触发签署方: userName={} userCode={} signerType={}({})",
                        s.getString("userName"), s.getString("userCode"),
                        s.getString("signerType"), signerTypeText(s.getString("signerType")));
            }
        }

        // 从 signerList 里找出已完成(signStatus=2)的签署方，打印其文件
        JSONArray signerList = vo.getJSONArray("signerList");
        if (signerList == null) return;
        for (int i = 0; i < signerList.size(); i++) {
            JSONObject signer = signerList.getJSONObject(i);
            if (!"2".equals(signer.getString("signStatus"))) continue; // 只关心已完成签署的
            log.info("[已完成签署方] userName={} userCode={} signerType={}({}) signDate={}",
                    signer.getString("userName"), signer.getString("userCode"),
                    signer.getString("signerType"), signerTypeText(signer.getString("signerType")),
                    signer.getString("signDate"));
            logSignFiles(signer.getJSONArray("signFileList"));
        }
    }

    /**
     * 签署流程完成（callBackEnum=2）：打印所有签署方的最终签署后文件。
     */
    private void logFlowFinish(JSONObject vo) {
        if (vo == null) return;
        log.info("[签署流程完成] processBeginTime={} processEndTime={}",
                vo.getString("processBeginTime"), vo.getString("processEndTime"));
        JSONArray signerList = vo.getJSONArray("signerList");
        if (signerList == null) return;
        for (int i = 0; i < signerList.size(); i++) {
            JSONObject signer = signerList.getJSONObject(i);
            log.info("[签署方] userName={} userCode={} signDate={}",
                    signer.getString("userName"), signer.getString("userCode"),
                    signer.getString("signDate"));
            logSignFiles(signer.getJSONArray("signFileList"));
        }
    }

    /**
     * 打印签署文件列表：原始文件、签署后文件、印章、下载地址。
     */
    private void logSignFiles(JSONArray signFileList) {
        if (signFileList == null || signFileList.isEmpty()) {
            log.info("    （无签署文件）");
            return;
        }
        for (int i = 0; i < signFileList.size(); i++) {
            JSONObject f = signFileList.getJSONObject(i);
            log.info("    文件[{}]: fileKey={}", i + 1, f.getString("fileKey"));
            if (f.containsKey("signedFileKey")) {
                log.info("        签署后文件 signedFileKey={}", f.getString("signedFileKey"));
            }
            JSONArray seals = f.getJSONArray("sealIdList");
            if (seals != null && !seals.isEmpty()) {
                log.info("        印章 sealIdList={}", seals.toJSONString());
            }
            logUrl("        原始文件下载", f.getString("downloadUrl"));
            logUrl("        签署后文件下载", f.getString("signDownloadUrl"));
        }
    }

    private void logUrl(String label, String url) {
        if (url == null || url.isBlank()) return;
        String fileName = extractFileName(url);
        if (fileName != null) {
            log.info("{}: {} （fileName={}）", label, url, fileName);
        } else {
            log.info("{}: {}", label, url);
        }
    }

    /** 从下载链接的 fileName 参数里取文件名（URL 解码）。 */
    private String extractFileName(String url) {
        int idx = url.indexOf("fileName=");
        if (idx < 0) return null;
        String raw = url.substring(idx + "fileName=".length());
        int amp = raw.indexOf('&');
        if (amp >= 0) raw = raw.substring(0, amp);
        try {
            return URLDecoder.decode(raw, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return raw;
        }
    }

    private String signerTypeText(String signerType) {
        if ("1".equals(signerType)) return "内部用户/企业";
        if ("2".equals(signerType)) return "相对方/外部用户";
        return "?";
    }
}
