# H5 Photo Upload App - Design Spec

**Date:** 2026/05/20

## Overview

A minimal H5 application with two photo upload methods (camera capture and gallery upload). Backend receives uploaded images and logs file metadata.

## Tech Stack

- **Backend:** Java 21 + Spring Boot 3.x + Maven
- **Frontend:** React + Vite
- **Backend Port:** 8283
- **Frontend Port:** 5173

## Architecture

```
frontend (5173) --HTTP--> backend (8283)
```

## Backend Design

### Endpoint

| Method | Path | Content-Type | Description |
|--------|------|--------------|-------------|
| POST | /api/upload | multipart/form-data | Accept image upload |

### Request

- Field name: `file`
- Content-Type: `multipart/form-data`

### Response

```json
{
  "success": true,
  "fileName": "photo_123456.jpg",
  "size": 1234567
}
```

### Log Output

```
Uploaded file: photo_123456.jpg, size: 1234567 bytes
```

## Frontend Design

### Single Page Layout

```
+-------------------------+
|     [Camera Preview]    |
|       (if available)     |
+-------------------------+
|                         |
|   [  拍 照  ]           |
|                         |
|   [ 照片上传 ]           |
|                         |
+-------------------------+
```

### Components

1. **Image Preview Area** - Displays captured/selected photo
2. **拍照 Button** - Opens device camera via `<input capture="environment">`
3. **照片上传 Button** - Opens gallery via `<input type="file">`

### HTML Input Strategy

- Camera: `<input type="file" accept="image/*" capture="environment">`
- Gallery: `<input type="file" accept="image/*">`

### Upload Flow

1. User clicks button → triggers file input
2. User captures/selects photo → preview displays on page
3. JavaScript automatically POSTs to `/api/upload`
4. Show upload result (success/failure)

### API Call

```javascript
const formData = new FormData();
formData.append('file', fileBlob);

fetch('http://localhost:8283/api/upload', {
  method: 'POST',
  body: formData
});
```

## Project Structure

```
sls-plantform/
├── backend/
│   └── src/main/java/
│       └── com/example/photo/
│           ├── PhotoApplication.java
│           └── PhotoController.java
├── frontend/
│   └── src/
│       ├── App.jsx
│       └── main.jsx
├── pom.xml
└── docs/superpowers/specs/YYYY-MM-DD-h5-photo-upload-design.md
```

## Success Criteria

1. Camera button opens device camera and captures photo
2. Gallery button opens photo picker and selects photo
3. Selected photo displays as preview on page
4. Photo uploads to backend on selection
5. Backend logs filename and size for each upload
6. No persistent storage required