package com.example.photo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PhotoController {

    @PostMapping("/api/upload")
    public ResponseEntity<Map<String, Object>> upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long size = file.getSize();
        System.out.println("Uploaded file: " + fileName + ", size: " + size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("fileName", fileName);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }
}