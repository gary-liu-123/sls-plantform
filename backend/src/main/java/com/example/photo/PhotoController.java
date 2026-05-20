package com.example.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PhotoController {
    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

    @PostMapping("/api/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long size = file.getSize();
        log.info("Uploaded file: {}, size: {} bytes", fileName, size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fileName", fileName);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }
}