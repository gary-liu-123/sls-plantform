# H5 Photo Upload Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A minimal H5 page with camera capture and gallery upload that sends photos to a Spring Boot backend which logs file info.

**Architecture:** React + Vite frontend calls Spring Boot backend via HTTP. Camera uses HTML5 input capture attribute. Gallery uses standard file input. Image preview displays locally before upload.

**Tech Stack:** Java 21 + Spring Boot 3.x + Maven | React + Vite

---

## File Structure

```
sls-plantform/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/example/photo/
│       ├── PhotoApplication.java
│       └── PhotoController.java
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── App.jsx
│       └── main.jsx
└── docs/superpowers/plans/2026-05-20-h5-photo-upload-plan.md
```

---

## Task 1: Scaffold Spring Boot Backend

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/example/photo/PhotoApplication.java`
- Create: `backend/src/main/java/com/example/photo/PhotoController.java`

- [ ] **Step 1: Create backend directory structure**

```bash
mkdir -p backend/src/main/java/com/example/photo
mkdir -p backend/src/main/resources
```

- [ ] **Step 2: Write pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>photo-upload</artifactId>
    <version>1.0.0</version>
    <name>photo-upload</name>
    <description>H5 Photo Upload Backend</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Write PhotoApplication.java**

```java
package com.example.photo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhotoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhotoApplication.class, args);
    }
}
```

- [ ] **Step 4: Write PhotoController.java**

```java
package com.example.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PhotoController {

    private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

    @PostMapping("/upload")
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
```

- [ ] **Step 5: Configure port in application.properties**

Create: `backend/src/main/resources/application.properties`

```properties
server.port=8283
```

- [ ] **Step 6: Commit**

```bash
git add backend/pom.xml backend/src/main/java/com/example/photo/PhotoApplication.java backend/src/main/java/com/example/photo/PhotoController.java backend/src/main/resources/application.properties
git commit -m "feat: add Spring Boot backend for photo upload

- Spring Boot 3.2.0 with Java 21
- POST /api/upload endpoint accepting multipart file
- Logs fileName and size, returns JSON response
- Runs on port 8283

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 2: Scaffold React Vite Frontend

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.jsx`
- Create: `frontend/src/App.jsx`

- [ ] **Step 1: Create frontend directory structure**

```bash
mkdir -p frontend/src
```

- [ ] **Step 2: Write package.json**

```json
{
  "name": "photo-upload-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite --port 5173",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.2.1",
    "vite": "^5.0.8"
  }
}
```

- [ ] **Step 3: Write vite.config.js**

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true
  }
})
```

- [ ] **Step 4: Write index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>拍照上传</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

- [ ] **Step 5: Write main.jsx**

```jsx
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
```

- [ ] **Step 6: Write App.jsx**

```jsx
import React, { useState } from 'react'

const API_URL = 'http://localhost:8283/api/upload'

function App() {
  const [preview, setPreview] = useState(null)
  const [uploadStatus, setUploadStatus] = useState('')

  const handleFileChange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    // Show preview
    const reader = new FileReader()
    reader.onload = (ev) => setPreview(ev.target.result)
    reader.readAsDataURL(file)

    // Upload to backend
    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        body: formData
      })
      const result = await response.json()
      setUploadStatus(`上传成功: ${result.fileName} (${result.size} bytes)`)
    } catch (err) {
      setUploadStatus('上传失败: ' + err.message)
    }
  }

  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h1>拍照上传</h1>

      {preview && (
        <div style={{ margin: '20px 0' }}>
          <img src={preview} alt="Preview" style={{ maxWidth: '300px', border: '1px solid #ccc' }} />
        </div>
      )}

      <div style={{ margin: '20px 0' }}>
        <input
          type="file"
          accept="image/*"
          capture="environment"
          onChange={handleFileChange}
          id="cameraInput"
          style={{ display: 'none' }}
        />
        <button
          onClick={() => document.getElementById('cameraInput').click()}
          style={{ margin: '5px', padding: '10px 20px' }}
        >
          拍照
        </button>

        <input
          type="file"
          accept="image/*"
          onChange={handleFileChange}
          id="galleryInput"
          style={{ display: 'none' }}
        />
        <button
          onClick={() => document.getElementById('galleryInput').click()}
          style={{ margin: '5px', padding: '10px 20px' }}
        >
          照片上传
        </button>
      </div>

      {uploadStatus && <p>{uploadStatus}</p>}
    </div>
  )
}

export default App
```

- [ ] **Step 7: Commit**

```bash
git add frontend/package.json frontend/vite.config.js frontend/index.html frontend/src/main.jsx frontend/src/App.jsx
git commit -m "feat: add React Vite frontend for photo upload

- React 18 + Vite on port 5173
- Camera button: <input capture="environment"> for device camera
- Gallery button: standard file input for photo selection
- Displays image preview after selection
- Auto-uploads to backend /api/upload and shows status

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 3: Verify

- [ ] **Step 1: Build backend**

```bash
cd backend && mvn clean package -DskipTests
```

Expected: BUILD SUCCESS

- [ ] **Step 2: Start backend (background)**

```bash
cd backend && mvn spring-boot:run &
```

Wait for: "Started PhotoApplication in X seconds"

- [ ] **Step 3: Test backend upload endpoint**

```bash
curl -X POST -F "file=@/path/to/test.jpg" http://localhost:8283/api/upload
```

Expected: `{"success":true,"fileName":"test.jpg","size":12345}`

Check backend console logs: "Uploaded file: test.jpg, size: 12345 bytes"

- [ ] **Step 4: Install frontend dependencies**

```bash
cd frontend && npm install
```

- [ ] **Step 5: Start frontend dev server**

```bash
cd frontend && npm run dev
```

Open http://localhost:5173 in mobile browser

- [ ] **Step 6: Test camera capture**

Click "拍照" button → verify camera opens → take photo → verify preview displays → check backend logs

- [ ] **Step 7: Test gallery upload**

Click "照片上传" button → verify gallery opens → select photo → verify preview displays → check backend logs

- [ ] **Step 8: Commit final state**

```bash
git add -A && git commit -m "feat: complete h5 photo upload app

Backend: Spring Boot on port 8283, POST /api/upload logs file info
Frontend: React on port 5173, camera and gallery upload buttons

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Verification Checklist

- [ ] Backend starts on port 8283
- [ ] POST /api/upload accepts multipart file
- [ ] Backend logs: "Uploaded file: {name}, size: {size} bytes"
- [ ] Frontend starts on port 5173
- [ ] "拍照" button opens device camera
- [ ] "照片上传" button opens gallery
- [ ] Selected photo displays as preview
- [ ] Photo uploads to backend on selection
- [ ] Upload status shows on page