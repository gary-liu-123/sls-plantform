# Bulk Photo Upload - Vue 3 Migration Design

**Date**: 2026-05-28  
**Status**: Approved  
**Author**: Claude (brainstorming with user)

## Overview

Migrate the existing React-based single-photo upload page to Vue 3 and add bulk upload capability with validation constraints. All photos in a batch are associated with a single phone number and uploaded to ServiceGo work orders via the existing `/api/upload` endpoint.

## Goals

1. Replace React 18 with Vue 3 (composition API + `<script setup>`)
2. Support bulk photo selection (up to 5,000 files per batch)
3. Enforce photo validation rules before upload
4. Prevent duplicate uploads via client-side deduplication
5. Preserve existing single-photo "拍照" flow

## Non-Goals

- Master file validation (no master file in this system)
- Filename-to-phone mapping (all photos share one phone number)
- Backend changes (existing `/api/upload` API unchanged)

## Framework Migration

### Dependencies

**Remove**:
- `react` 18.2.0
- `react-dom` 18.2.0
- `@vitejs/plugin-react` ^4.0.0

**Add**:
- `vue` ^3.4.0
- `@vitejs/plugin-vue` ^5.0.0

No other dependencies needed — validation uses browser native APIs (`crypto.subtle`, `FileReader`, `Image`).

### File Changes

| File | Action | Details |
|------|--------|---------|
| `package.json` | Modify | Replace React deps with Vue deps |
| `vite.config.js` | Modify | Change plugin from `@vitejs/plugin-react` to `@vitejs/plugin-vue` |
| `index.html` | Modify | Script entry: `/src/main.jsx` → `/src/main.js` |
| `src/main.jsx` | Delete | React entry point |
| `src/App.jsx` | Delete | React component |
| `src/main.js` | Create | Vue entry: `createApp(App).mount('#root')` |
| `src/App.vue` | Create | Vue SFC with composition API |
| `src/composables/usePhotoQueue.js` | Create | Photo list state + validation + dedup |
| `src/composables/useBatchUpload.js` | Create | Concurrent upload scheduler |
| `src/utils/validate.js` | Create | Validation rules (pure functions) |
| `src/utils/hash.js` | Create | SHA-256 file hashing |

`start_all.sh` unchanged — still runs `npm run dev` on port 5173.

## UI Flow

### Layout

```
┌─────────────────────────────────────────────┐
│ 手机号: [_______________]                    │
│ [拍照] [批量选择照片] [开始上传]              │
│ ─────────────────────────────────────────── │
│ 进度: 已完成 X / 总数 Y (成功 A, 失败 B)      │
│ [清除已上传记录]                             │
│ ─────────────────────────────────────────── │
│ [缩略图] file1.jpg  120KB  180×240  ✓ 通过   │
│ [缩略图] file2.jpg  3.1MB  -        ✗ 超2MB  │
│ [缩略图] file3.jpg  80KB   200×300  ⚠ 推荐尺寸│
│ [缩略图] file4.jpg  90KB   180×240  ● 已上传  │
│ ...                                          │
└─────────────────────────────────────────────┘
```

### User Journey

1. **Input phone number** (required, shared by all photos in batch)
2. **Select photos**:
   - **拍照** (single): Opens camera, uploads immediately (original flow, no queue)
   - **批量选择照片**: `<input type="file" multiple accept="image/jpeg">`, adds to queue
3. **Validation runs** automatically after file selection:
   - Each file checked against all rules (see Validation Rules section)
   - Results displayed in list with color-coded status
4. **Review list**: User sees thumbnails, sizes, dimensions, validation status
5. **Click "开始上传"**: Uploads all `pass` and `warn` items (skips `fail` and `skipped`)
6. **Progress updates**: Each item's status changes to `uploading` → `uploaded` / `failed`
7. **Retry failures**: Failed items show a "重试" button

## Validation Rules

Each file gets one of three final states: `pass` / `warn` / `fail`.  
Only `fail` items are blocked from upload; `warn` items upload with a warning message.

| # | Rule | Check Method | Fail Action |
|---|------|--------------|-------------|
| 1 | JPEG format | File header magic number `FF D8 FF` (first 3 bytes) | **fail** |
| 2 | Not zip / PGP | Blacklist magic numbers: `50 4B 03 04` (zip), `99 01` (PGP) | **fail** |
| 3 | Size ≤ 2MB | `file.size <= 2*1024*1024` | **fail** |
| 4 | Recommended size ≤ 100K | `file.size <= 100*1024` | **warn** ("建议 ≤100K") |
| 5 | Dimensions 180×240 | `new Image()` + `naturalWidth/naturalHeight` | **warn** ("推荐 180×240，当前 W×H") |
| 6 | Batch limit ≤ 5000 | `files.length + existingList.length` on selection | Excess files discarded + red banner |

### Implementation Notes

- **Rules 1+2**: Single `File.slice(0,8).arrayBuffer()` read, check first 8 bytes
- **Rule 5**: Decode image to `Image` object (slow). Run in batches of 50 via `Promise.all` to avoid memory spike
- **Rule 6**: Enforced at selection time. Top banner: "单批最多 5000 张，已忽略 K 张"

### Status Colors

- `pass`: Green ✓
- `warn`: Yellow ⚠
- `fail`: Red ✗
- `skipped`: Gray ● (deduplicated)
- `uploaded`: Gray ● (success)
- `uploading`: Blue spinner

## Deduplication ("Existing photos do not need to be reloaded")

### Strategy

Client-side deduplication using SHA-256 hash + localStorage.

### Flow

1. **On file selection**: Compute SHA-256 of each file (`crypto.subtle.digest`)
2. **Check localStorage**: Key = `uploadedPhotos:<phone>`, value = JSON array of sha256 strings
3. **If hash exists** in current phone's set → mark as `skipped`, display "已上传过，跳过"
4. **On successful upload**: Append sha256 to localStorage set
5. **Clear button**: "清除已上传记录" link clears localStorage for current phone (handles device/user changes)

### Limitations

- **Client-side only**: Backend `/api/upload` unchanged. If user switches devices/browsers, dedup resets.
- **Per-phone isolation**: Different phones have separate dedup sets.

## Batch Upload

### Concurrency

- **Parallel requests**: 3 (balance between speed and backend load)
- **Queue**: All `status !== 'fail' && !uploaded` items
- **Per-request**: `POST /api/upload` with `file` + `phone` (same as single-photo flow)

### Progress Tracking

- **Top banner**: "已完成 X / 总数 Y（成功 A, 失败 B）"
- **Per-item status**: `uploading` → `uploaded` / `failed` (real-time update)
- **Final summary**: "上传完成：成功 A 张，失败 B 张"

### Error Handling

- **Failed items**: Remain in list with red status + error message
- **Retry**: Each failed item gets a "重试" button (re-uploads that single file)
- **Network errors**: Caught and displayed per-item (no global retry)

## File Structure & Responsibilities

```
frontend/
├── src/
│   ├── main.js                     Vue app entry
│   ├── App.vue                     Top-level layout (UI only, no business logic)
│   ├── composables/
│   │   ├── usePhotoQueue.js        Reactive list + add/remove + validation + dedup
│   │   └── useBatchUpload.js       Concurrent upload scheduler + progress
│   └── utils/
│       ├── validate.js             Pure validation functions (JPEG/size/dimensions)
│       └── hash.js                 SHA-256 file hashing
```

### Responsibilities

| File | Purpose | Inputs | Outputs | Side Effects |
|------|---------|--------|---------|--------------|
| `validate.js` | Validation rules | `File` | `{ status, message, width, height }` | None (pure) |
| `hash.js` | File hashing | `File` | `sha256` hex string | None (pure) |
| `usePhotoQueue.js` | List state | Files to add | Reactive list, add/remove/clear methods | localStorage read/write |
| `useBatchUpload.js` | Upload scheduler | Queue + phone | Progress state, start/retry methods | Fetch `/api/upload` |
| `App.vue` | UI orchestration | User interactions | Rendered UI | Calls composables |

### Design Principles

- **Single responsibility**: Each file has one clear purpose
- **Pure functions**: `validate.js` and `hash.js` have no side effects (easy to test)
- **Composables**: Encapsulate reactive state + logic, expose clean APIs
- **No prop drilling**: Composables accessed directly in `App.vue` (no need for props/events)

## LocalStorage Schema

**Key**: `uploadedPhotos:<phone>`  
**Value**: JSON array of SHA-256 hex strings

**Example**:
```json
{
  "uploadedPhotos:15651818750": [
    "a3c5f1...",
    "b7d2e9...",
    ...
  ]
}
```

**Operations**:
- **Read**: On file selection, parse array and check if new file's hash exists
- **Write**: On successful upload, append hash and save
- **Clear**: "清除已上传记录" button deletes key for current phone

## Backend Integration

### No Changes Required

- Existing `/api/upload` endpoint unchanged
- Each photo uploaded individually with `file` + `phone` (same as single-photo flow)
- Backend doesn't know/care if frontend is doing bulk or single upload

### API Contract

```
POST /api/upload
Content-Type: multipart/form-data

file: <File>
phone: <string>

Response:
{
  "previewUrl": "/api/attachment?dataId=X&field=validIdCard"
}
```

## Preserved Functionality

### Single-Photo "拍照" Flow

- **Button**: "拍照" (unchanged)
- **Behavior**: Opens camera (`capture="environment"`), uploads immediately on capture
- **No queue**: Does not add to batch list, does not run dedup
- **Same as before**: Calls `/api/upload` once, shows preview on success

This ensures existing mobile users see no change in the single-photo flow.

## Edge Cases

| Scenario | Behavior |
|----------|----------|
| No phone number entered | Disable "批量选择照片" and "开始上传" buttons |
| Select 0 files | No-op, list unchanged |
| Select > 5000 files | Accept first 5000, discard rest, show red banner |
| All files fail validation | "开始上传" button disabled, show message |
| Network error during upload | Mark item as `failed`, show error, allow retry |
| User changes phone mid-batch | Warn: "手机号已变更，当前列表将清空" (prevent mixing phones) |
| localStorage full | Catch quota error, show warning, continue upload (dedup disabled) |
| Duplicate file in same selection | Both added to list, both marked `skipped` if hash matches localStorage |

## Testing Strategy

### Manual Testing Checklist

1. **Framework migration**: Verify Vue app loads, no console errors
2. **Single photo**: "拍照" button still works (original flow)
3. **Bulk selection**: Select 10 files, verify all appear in list
4. **Validation**:
   - Upload a PNG → should fail (not JPEG)
   - Upload a 3MB JPEG → should fail (>2MB)
   - Upload a 150KB JPEG → should warn (>100K)
   - Upload a 180×240 JPEG → should pass
   - Upload a 500×500 JPEG → should warn (wrong dimensions)
5. **Deduplication**:
   - Upload file A → success
   - Re-select file A → should show "已上传过，跳过"
   - Click "清除已上传记录" → re-select file A → should not skip
6. **Batch upload**:
   - Select 20 files (mix of pass/warn/fail) → click "开始上传"
   - Verify only pass+warn items upload
   - Verify progress updates in real-time
7. **5000 limit**: Select 5001 files → verify red banner, only 5000 in list
8. **Retry**: Disconnect network, upload → verify failures → reconnect → click "重试" → verify success

### Unit Testing (Optional)

- `validate.js`: Test each rule with fixture files
- `hash.js`: Test SHA-256 output matches known hashes
- `usePhotoQueue.js`: Test add/remove/dedup logic
- `useBatchUpload.js`: Mock fetch, test concurrency + progress

## Migration Steps

1. **Install Vue**: `npm install vue@^3.4.0 @vitejs/plugin-vue@^5.0.0`
2. **Uninstall React**: `npm uninstall react react-dom @vitejs/plugin-react`
3. **Update config**: Modify `vite.config.js` and `index.html`
4. **Create Vue files**: `main.js`, `App.vue`, composables, utils
5. **Delete React files**: `main.jsx`, `App.jsx`
6. **Test**: Run `npm run dev`, verify app loads
7. **Commit**: "Migrate to Vue 3 + add bulk upload with validation"

## Open Questions

None — all requirements clarified during brainstorming.

## Appendix: Validation Rule Sources

From user-provided spec:

> Photo Files  
> One file for each photo, file name must match name in master file *(ignored — no master file)*  
> Must be in JPEG file format  
> Cannot be "zipped" or PGP-encrypted  
> Maximum file size: 2MB (recommended file size: 100K)  
> Dimensions: 180 pixels wide by 240 pixel high preferred  
> There is a limit of 5,000 photo files that can be processed in a single pass  
> Existing photos do not need to be reloaded when loading new photos
