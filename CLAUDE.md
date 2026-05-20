# H5 Photo Upload Project

## Project Overview

A minimal H5 application for photo upload with camera capture and gallery selection. Backend receives images and logs file metadata.

## Tech Stack

- **Backend:** Java 21 + Spring Boot 3.2.0 + Maven
- **Frontend:** React 18 + Vite 5

## Project Structure

```
sls-plantform/
├── start_all.sh                # Start all services script
├── backend/                    # Spring Boot backend
│   ├── pom.xml
│   └── src/main/java/com/example/photo/
│       ├── PhotoApplication.java
│       └── PhotoController.java
├── frontend/                  # React Vite frontend
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.jsx
│       └── App.jsx
├── logs/                       # Service logs
├── docs/superpowers/          # Design and plan docs
│   ├── specs/
│   └── plans/
└── CLAUDE.md                  # This file
```

## Ports

- **Backend:** 8283 (http://localhost:8283)
- **Frontend:** 5173 (http://localhost:5173)

## Running

### Start All Services (Recommended)

```bash
bash start_all.sh
```

This script will:
1. Kill any existing services on ports 5173 and 8283
2. Start the Spring Boot backend on port 8283
3. Start the React frontend on port 5173
4. Wait for services to be ready
5. Display access URLs

### IntelliJ IDEA

1. **Open Project Structure**: File → Project Structure → Modules
2. **Import Module**: Click + → Import Module → Select `backend` folder
3. **Run**: Click the green run button next to `PhotoApplication.main()`

## API

### POST /api/upload

Upload a photo file.

**Request:**
- Content-Type: `multipart/form-data`
- Field: `file`

**Response:**
```json
{
  "success": true,
  "fileName": "photo.jpg",
  "size": 1234567
}
```

**Backend logs:**
```
INFO Uploaded file: photo.jpg, size: 1234567 bytes
```

## Access from Mobile

Find your computer's IP address and access:
- Frontend: `http://{your-ip}:5173`

## Notes

- No persistent storage - files are logged and discarded
- Camera button uses `capture="environment"` attribute (mobile only)
- Gallery button works on all devices
- Backend port 8283 may already have another service running - check before starting