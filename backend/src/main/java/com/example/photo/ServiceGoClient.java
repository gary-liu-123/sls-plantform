package com.example.photo;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class ServiceGoClient {
    private static final Logger log = LoggerFactory.getLogger(ServiceGoClient.class);

    private final String host;
    private final String email;
    private final String apiToken;
    private final RestTemplate restTemplate;

    public ServiceGoClient(@Value("${servicego.host}") String host,
                           @Value("${servicego.email}") String email,
                           @Value("${servicego.api-token}") String apiToken,
                           RestTemplateBuilder builder) {
        this.host = host;
        this.email = email;
        this.apiToken = apiToken;
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
    }

    public long queryDataIdByPhone(String objectApiName, String uniqueFieldApiName, String phone) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/data")
                .queryParam("objectApiName", objectApiName)
                .queryParam("uniqueFieldApiName", uniqueFieldApiName)
                .queryParam("uniqueFieldValue", phone)
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        log.info("ServiceGo query: {}", url);
        JsonNode root = restTemplate.getForObject(url, JsonNode.class);
        log.info("ServiceGo query response: {}", root);
        ensureBusinessOk(root);

        JsonNode id = root == null ? null : root.path("data").path("id");
        if (id == null || id.isMissingNode() || id.isNull()) {
            throw new IOException("响应中无 data.id：" + root);
        }
        return id.asLong();
    }

    public JsonNode uploadAttachment(long dataId, String objectApiName, String fieldApiName,
                                     String fileName, byte[] fileBytes, String contentType) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/fileField/attachments")
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("objectApiName", objectApiName);
        form.add("dataId", String.valueOf(dataId));
        form.add("fieldApiName", fieldApiName);
        form.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (contentType != null) {
            headers.set("X-File-Content-Type", contentType);
        }

        JsonNode resp = restTemplate.postForObject(url, new HttpEntity<>(form, headers), JsonNode.class);
        log.info("ServiceGo upload response: {}", resp);
        ensureBusinessOk(resp);
        return resp;
    }

    /**
     * 按 id 查工单详情。比 listAttachments 更稳——
     * 文档说的 /v1/fileField/attachments 在当前环境返回 text/html 不通，
     * 而 /api/v1/data 的响应里 attachment.attachmentList 已经包含全部附件。
     */
    public JsonNode queryDataById(long dataId, String objectApiName) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/data")
                .queryParam("objectApiName", objectApiName)
                .queryParam("id", dataId)
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        log.info("ServiceGo query by id: {}", url);
        JsonNode resp = restTemplate.getForObject(url, JsonNode.class);
        ensureBusinessOk(resp);
        return resp;
    }

    /**
     * 取指定字段最新一张附件的下载 URL（已带签名）。
     */
    public String latestAttachmentUrl(long dataId, String objectApiName, String fieldApiName) throws IOException {
        JsonNode resp = queryDataById(dataId, objectApiName);
        JsonNode fields = resp.path("data").path("fieldDataList");
        if (!fields.isArray()) {
            return null;
        }
        for (JsonNode f : fields) {
            if (!fieldApiName.equals(f.path("fieldApiName").asText())) continue;
            JsonNode list = f.path("attachment").path("attachmentList");
            if (!list.isArray() || list.isEmpty()) {
                list = f.path("picture").path("attachmentList"); // field_type_image
            }
            if (!list.isArray() || list.isEmpty()) return null;
            JsonNode last = list.get(list.size() - 1);
            String addr = last.path("docAddress").asText(null);
            if (addr == null || addr.isBlank()) return null;
            return addr.startsWith("http") ? addr : host + addr;
        }
        return null;
    }

    /**
     * 给下载链接补上 email/timestamp/sign，下载内部存储资源时 ServiceGo 需要这套鉴权。
     */
    public String signDownloadUrl(String url) {
        long ts = Instant.now().getEpochSecond();
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();
    }

    public JsonNode queryByUniqueField(String objectApiName, String uniqueFieldApiName, String uniqueFieldValue) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/data")
                .queryParam("objectApiName", objectApiName)
                .queryParam("uniqueFieldApiName", uniqueFieldApiName)
                .queryParam("uniqueFieldValue", uniqueFieldValue)
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        log.info("ServiceGo queryByUniqueField: {}", url);
        JsonNode resp = restTemplate.getForObject(url, JsonNode.class);
        log.info("ServiceGo queryByUniqueField response: {}", resp);
        ensureBusinessOk(resp);
        return resp;
    }

    public JsonNode queryList(String objectApiName, int filterId, int pageNum, int pageSize) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/datas")
                .queryParam("objectApiName", objectApiName)
                .queryParam("filterId", filterId)
                .queryParam("pageNum", pageNum)
                .queryParam("pageSize", pageSize)
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        log.info("ServiceGo queryList: {}", url);
        JsonNode resp = restTemplate.getForObject(url, JsonNode.class);
        log.info("ServiceGo queryList response: {}", resp);
        ensureBusinessOk(resp);
        return resp;
    }

    /**
     * 高级搜索：POST /api/v1/datas/search。
     * 通过 filterId + conditionList 多条件筛选记录，单选字段用 is/not，电话字段用 is_any/not_any。
     * judgeStrategy 默认 1（满足所有条件）。filterId 必填，需要在 ServiceGo 后台预配。
     */
    public JsonNode searchData(String objectApiName, int filterId, String conditionListJson,
                               int pageNum, int pageSize) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/datas/search")
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        String body = "{\"objectApiName\":\"" + objectApiName + "\""
                + ",\"filterId\":" + filterId
                + ",\"pageNum\":" + pageNum
                + ",\"pageSize\":" + pageSize
                + ",\"conditionList\":" + conditionListJson
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        log.info("ServiceGo searchData: {} body={}", url, body);
        JsonNode resp = restTemplate.postForObject(url, entity, JsonNode.class);
        log.info("ServiceGo searchData response: {}", resp);
        ensureBusinessOk(resp);
        return resp;
    }

    public JsonNode updateData(String objectApiName, long id, String fieldDataListJson) throws IOException {
        long ts = Instant.now().getEpochSecond();
        String url = UriComponentsBuilder.fromHttpUrl(host + "/api/v1/data")
                .queryParam("email", email)
                .queryParam("timestamp", ts)
                .queryParam("sign", signFor(ts))
                .build()
                .toUriString();

        String body = "{\"objectApiName\":\"" + objectApiName + "\",\"id\":" + id + ",\"fieldDataList\":" + fieldDataListJson + "}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        log.info("ServiceGo updateData: {}", url);
        JsonNode resp = restTemplate.exchange(url, org.springframework.http.HttpMethod.PUT, entity, JsonNode.class).getBody();
        log.info("ServiceGo updateData response: {}", resp);
        ensureBusinessOk(resp);
        return resp;
    }

    private String signFor(long timestamp) {
        return sha256(email + "&" + apiToken + "&" + timestamp);
    }

    private static void ensureBusinessOk(JsonNode body) throws IOException {
        if (body == null) {
            throw new IOException("ServiceGo 返回为空");
        }
        int code = body.path("code").asInt(200);
        if (code != 200) {
            throw new IOException("ServiceGo 业务错误 code=" + code + " body=" + body);
        }
    }

    private static String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
