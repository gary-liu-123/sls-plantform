package com.example.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PhotoController {
    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

    @Autowired
    private ServiceGoClient serviceGoClient;

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

            String sgResp = serviceGoClient.uploadAttachment(
                    dataId, entryObjectApiName, entryIdCardField,
                    fileName, file.getBytes(), file.getContentType()).toString();

            resp.put("success", true);
            resp.put("dataId", dataId);
            resp.put("fieldApiName", entryIdCardField);
            resp.put("serviceGoResponse", sgResp);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Upload to ServiceGo failed", e);
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
