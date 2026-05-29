package com.example.esign.client;

import com.alibaba.fastjson2.JSONObject;
import com.example.esign.config.EsignConfig;
import com.example.esign.exception.EsignException;
import com.example.esign.util.SignUtils;
import okhttp3.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 电子签 API 客户端
 * 封装签名 + HTTP 调用，提供业务级方法
 */
@Component
public class EsignClient {

    private static final Logger log = LoggerFactory.getLogger(EsignClient.class);

    private final EsignConfig config;
    private final OkHttpClient httpClient;

    public EsignClient(EsignConfig config) {
        this.config = config;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String getHost() {
        return config.getHost();
    }

    // ==================== 通用 HTTP 方法 ====================

    /**
     * GET 请求 — 签名 query string
     */
    public String doGet(String pathWithQuery) {
        String url = config.getHost() + pathWithQuery;
        int idx = pathWithQuery.indexOf('?');
        String plaintext = (idx == -1) ? "" : pathWithQuery.substring(idx + 1);
        String signature = sign(plaintext);

        Request.Builder rb = new Request.Builder().url(url).get();
        addJsonHeaders(rb, signature);

        Request request = rb.build();
        logRequest("GET", url, request.headers(), null);
        return executeAndValidate(request, pathWithQuery);
    }
    /**
     * POST JSON 请求 — 签名 body 原文
     */
    public String doPost(String path, JSONObject body) {
        return doPostRaw(path, body.toJSONString());
    }

    /**
     * POST 原始 JSON 字符串，不校验 code（etl 接口用 success/status 格式）
     */
    public String doPostRaw(String path, String bodyStr) {
        String url = config.getHost() + path;
        String signature = sign(bodyStr);

        RequestBody requestBody = RequestBody.create(bodyStr,
                MediaType.parse("application/json; charset=UTF-8"));

        Request.Builder rb = new Request.Builder().url(url).post(requestBody);
        addJsonHeaders(rb, signature);

        Request request = rb.build();
        logRequest("POST", url, request.headers(), bodyStr);

        try (Response response = httpClient.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            log.info("[响应] HTTP {}: {}", response.code(), respBody);
            if (!response.isSuccessful()) {
                throw new EsignException(path, response.code(), respBody);
            }
            return respBody;
        } catch (IOException e) {
            throw new RuntimeException("请求执行异常, path=" + path, e);
        }
    }

    /**
     * 上传文件 — multipart/form-data
     * 签名明文 = extraBody 的 JSON 原文
     */
    public String uploadFile(String path, File file, JSONObject extraBody) {
        String url = config.getHost() + path;
        String bodyStr = extraBody.toJSONString();
        String signature = sign(bodyStr);

        MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String key : extraBody.keySet()) {
            Object val = extraBody.get(key);
            mb.addFormDataPart(key, val == null ? "" : val.toString());
        }
        mb.addFormDataPart("file", file.getName(),
                RequestBody.create(file, MediaType.parse("application/octet-stream")));

        Request.Builder rb = new Request.Builder().url(url).post(mb.build());
        rb.addHeader("X-timevale-project-id", config.getProjectId());
        rb.addHeader("X-timevale-signature", signature);
        rb.addHeader("Accept", "*/*");

        Request request = rb.build();
        logRequest("UPLOAD", url, request.headers(), bodyStr);
        return executeAndValidate(request, path);
    }
    /**
     * 上传文件到动态 URL (由 generateUploadUrl 返回)
     * 签名明文 = extraBody 的 JSON 原文，签名明文 query string 部分取 URL 中的 ?
     */
    public String uploadFileToUrl(String fullUrl, File file, JSONObject extraBody) {
        String bodyStr = extraBody.toJSONString();
        String signature = sign(bodyStr);

        MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (String key : extraBody.keySet()) {
            Object val = extraBody.get(key);
            mb.addFormDataPart(key, val == null ? "" : val.toString());
        }
        mb.addFormDataPart("file", file.getName(),
                RequestBody.create(file, MediaType.parse("application/octet-stream")));

        Request.Builder rb = new Request.Builder().url(fullUrl).post(mb.build());
        rb.addHeader("X-timevale-project-id", config.getProjectId());
        rb.addHeader("X-timevale-signature", signature);
        rb.addHeader("Accept", "*/*");

        Request request = rb.build();
        logRequest("UPLOAD", fullUrl, request.headers(), bodyStr);

        // 用 URL 中的 path 作为异常信息
        String path;
        try {
            path = new java.net.URL(fullUrl).getPath();
        } catch (Exception e) {
            path = fullUrl;
        }
        return executeAndValidate(request, path);
    }

    // ==================== 业务快捷方法 ====================

    /**
     * 生成上传文件链接
     * POST /file/v1/generateUploadUrl
     */
    public JSONObject generateUploadUrl(JSONObject body) {
        return JSONObject.parseObject(doPost("/file/v1/generateUploadUrl", body));
    }

    /**
     * 上传文件到指定链接 (uploadAndSpiltV2)
     * POST /file/v1/pdf/uploadAndSpiltV2
     */
    public JSONObject uploadAndSpiltV2(File file, JSONObject extraBody) {
        return JSONObject.parseObject(uploadFile("/file/v1/pdf/uploadAndSpiltV2", file, extraBody));
    }

    /**
     * 使用文件一步发起签署
     * POST /esign-signs/v1/signFlow/createAndStart
     */
    public JSONObject createAndStart(JSONObject body) {
        return JSONObject.parseObject(doPost("/esign-signs/v1/signFlow/createAndStart", body));
    }

    /**
     * 获取 AccessToken
     * POST /etl-integrate/v1/client/getAccessToken
     */
    public JSONObject getAccessToken(JSONObject body) {
        return JSONObject.parseObject(doPost("/etl-integrate/v1/client/getAccessToken", body));
    }

    /**
     * 获取 ssoToken
     * POST /etl-integrate/v1/client/getSsoToken
     */
    public JSONObject getSsoToken(JSONObject body) {
        return JSONObject.parseObject(doPost("/etl-integrate/v1/client/getSsoToken", body));
    }
    /**
     * 添加签署节点/签署方
     * POST /esign-signs/v1/signFlow/signers/add
     */
    public JSONObject addSigners(JSONObject body) {
        return JSONObject.parseObject(doPost("/esign-signs/v1/signFlow/signers/add", body));
    }

    /**
     * 结束签署流程
     * POST /esign-signs/v1/signFlow/finish
     */
    public JSONObject finishFlow(JSONObject body) {
        return JSONObject.parseObject(doPost("/esign-signs/v1/signFlow/finish", body));
    }

    /**
     * 删除文件
     * POST /esign-docs/v1/cus/file/delete
     */
    public JSONObject deleteFile(JSONObject body) {
        return JSONObject.parseObject(doPost("/esign-docs/v1/cus/file/delete", body));
    }

    /**
     * 获取签署链接
     * GET /esign-signs/v1/signFlow/signUrls?signFlowId=xxx
     */
    public JSONObject getSignUrls(String signFlowId) {
        return JSONObject.parseObject(
                doGet("/esign-signs/v1/signFlow/signUrls?signFlowId=" + signFlowId));
    }

    // ==================== 私有方法 ====================

    private String sign(String plaintext) {
        log.info("[签名] 明文: {}", plaintext);
        String signature = SignUtils.sign(plaintext, config.getProjectSecret());
        log.info("[签名] 结果: {}", signature);
        return signature;
    }

    private void addJsonHeaders(Request.Builder builder, String signature) {
        builder.addHeader("X-timevale-project-id", config.getProjectId());
        builder.addHeader("X-timevale-signature", signature);
        builder.addHeader("Accept", "*/*");
        builder.addHeader("Content-Type", "application/json; charset=UTF-8");
    }

    private String executeAndValidate(Request request, String path) {
        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            log.info("[响应] HTTP {}: {}", response.code(), body);

            if (!response.isSuccessful()) {
                throw new EsignException(path, response.code(), body);
            }

            JSONObject json = JSONObject.parseObject(body);
            int code = json.getIntValue("code");
            if (code != 0 && code != 200) {
                throw new EsignException(path, body);
            }

            return body;
        } catch (IOException e) {
            throw new RuntimeException("请求执行异常, path=" + path, e);
        }
    }

    private void logRequest(String method, String url, Headers headers, String body) {
        log.info("==== 请求开始 ====");
        log.info("[请求] {} {}", method, url);
        log.info("[请求头]");
        for (int i = 0; i < headers.size(); i++) {
            log.info("  {} : {}", headers.name(i), headers.value(i));
        }
        if (body != null) {
            log.info("[请求体] {}", body);
        }
        log.info("==== 请求结束 ====");
    }
}
