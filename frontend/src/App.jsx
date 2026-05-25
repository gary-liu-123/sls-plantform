import { useState } from 'react'

const API_BASE = window.location.hostname === 'localhost'
  ? 'http://localhost:8283'
  : `http://${window.location.hostname}:8283`

const UPLOAD_URL = `${API_BASE}/api/upload`

function App() {
  const [phone, setPhone] = useState('')
  const [preview, setPreview] = useState(null)
  const [previewSource, setPreviewSource] = useState('')
  const [uploadStatus, setUploadStatus] = useState('')
  const [debugInfo, setDebugInfo] = useState('')

  const handleFileChange = (event) => {
    const file = event.target.files[0]
    if (!file) {
      setDebugInfo('No file selected')
      return
    }

    if (!phone.trim()) {
      setUploadStatus('请先填写手机号')
      event.target.value = ''
      return
    }

    setDebugInfo(`API: ${UPLOAD_URL}\nPhone: ${phone}\nFile: ${file.name}, Size: ${file.size} bytes, Type: ${file.type}`)

    const reader = new FileReader()
    reader.onload = (e) => {
      setPreview(e.target.result)
      setPreviewSource('本地预览')
    }
    reader.onerror = (e) => {
      setDebugInfo(prev => prev + '\nFileReader error: ' + e)
    }
    reader.readAsDataURL(file)

    const formData = new FormData()
    formData.append('file', file)
    formData.append('phone', phone.trim())

    setUploadStatus('上传中...')

    fetch(UPLOAD_URL, {
      method: 'POST',
      body: formData
    })
      .then((res) => res.text().then(text => ({ ok: res.ok, text })))
      .then(({ ok, text }) => {
        setDebugInfo(prev => prev + '\nResponse: ' + text)
        if (!ok) {
          setUploadStatus('上传失败: ' + text)
          return
        }
        setUploadStatus('上传成功，正在获取服务器图片...')
        try {
          const data = JSON.parse(text)
          if (data.previewUrl) {
            setPreview(API_BASE + data.previewUrl)
            setPreviewSource('服务器图片')
            setUploadStatus('上传成功')
          } else {
            setUploadStatus('上传成功，但未返回 previewUrl')
          }
        } catch (e) {
          setUploadStatus('上传成功，解析响应失败: ' + e.message)
        }
      })
      .catch((err) => {
        setUploadStatus('上传失败: ' + err.message)
      })
  }

  return (
    <div style={{ padding: '16px' }}>
      <div style={{ marginBottom: '12px' }}>
        <label>手机号（用于定位入职工单）：</label>
        <input
          type="tel"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          placeholder="请输入手机号"
          style={{ marginLeft: '8px', padding: '4px 8px' }}
        />
      </div>

      <input
        type="file"
        id="cameraInput"
        accept="image/*"
        capture="environment"
        onChange={handleFileChange}
        style={{ display: 'none' }}
      />
      <input
        type="file"
        id="galleryInput"
        accept="image/*"
        onChange={handleFileChange}
        style={{ display: 'none' }}
      />

      <button onClick={() => document.getElementById('cameraInput').click()}>
        拍照
      </button>
      <button onClick={() => document.getElementById('galleryInput').click()} style={{ marginLeft: '8px' }}>
        照片上传
      </button>

      {preview && (
        <div style={{ marginTop: '16px' }}>
          {previewSource && <p style={{ fontSize: '12px', color: '#666', margin: '0 0 4px' }}>{previewSource}</p>}
          <img src={preview} alt="预览" style={{ maxWidth: '100%' }} />
        </div>
      )}

      {uploadStatus && <p style={{ marginTop: '8px' }}>{uploadStatus}</p>}
      {debugInfo && <pre style={{ fontSize: '10px', marginTop: '8px', color: '#666', whiteSpace: 'pre-wrap' }}>{debugInfo}</pre>}
    </div>
  )
}

export default App
