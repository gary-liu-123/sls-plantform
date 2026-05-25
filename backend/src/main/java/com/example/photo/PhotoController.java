package com.example.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PhotoController {
    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

    @Autowired
    private ServiceGoClient serviceGoClient;

    private final RestTemplate downloadClient = new RestTemplate();

    @Value("${servicego.entry-object-api-name}")
    private String entryObjectApiName;

    @Value("${servicego.entry-id-card-field}")
    private String entryIdCardField;

    @PostMapping("/api/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("phone") String phone) {
        Map<String, Object> resp = new HashMap<>();

        if (file == null || file.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "文件不能为空");
            return ResponseEntity.badRequest().body(resp);
        }
        if (phone == null || phone.isBlank()) {
            resp.put("success", false);
            resp.put("message", "手机号不能为空");
            return ResponseEntity.badRequest().body(resp);
        }

        String fileName = file.getOriginalFilename();
        log.info("Receive upload: phone={}, file={}, size={}", phone, fileName, file.getSize());

        try {
            long dataId = serviceGoClient.queryDataIdByPhone(entryObjectApiName, "personalPhone", phone.trim());
            log.info("Found dataId={} for phone={}", dataId, phone);

            serviceGoClient.uploadAttachment(
                    dataId, entryObjectApiName, entryIdCardField,
                    fileName, file.getBytes(), file.getContentType());

            // 加 ts 是为了防止前端缓存导致看到上一次的旧图
            String previewUrl = "/api/attachment?dataId=" + dataId
                    + "&field=" + entryIdCardField + "&ts=" + System.currentTimeMillis();

            resp.put("success", true);
            resp.put("dataId", dataId);
            resp.put("fieldApiName", entryIdCardField);
            resp.put("previewUrl", previewUrl);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Upload to ServiceGo failed", e);
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/api/attachment")
    public ResponseEntity<Resource> attachment(@RequestParam("dataId") long dataId,
                                               @RequestParam("field") String field) {
        try {
            String url = serviceGoClient.latestAttachmentUrl(dataId, entryObjectApiName, field);
            if (url == null) {
                return ResponseEntity.notFound().build();
            }
            String signedUrl = serviceGoClient.signDownloadUrl(url);
            log.info("Proxying attachment dataId={} field={} from {}", dataId, field, signedUrl);

            ResponseEntity<Resource> downstream = downloadClient.getForEntity(URI.create(signedUrl), Resource.class);
            HttpHeaders out = new HttpHeaders();
            MediaType contentType = downstream.getHeaders().getContentType();
            out.setContentType(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM);
            out.setCacheControl("no-store");
            return new ResponseEntity<>(downstream.getBody(), out, downstream.getStatusCode());
        } catch (Exception e) {
            log.error("Proxy attachment failed", e);
            return ResponseEntity.status(500).build();
        }
    }
}
