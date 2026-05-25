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
