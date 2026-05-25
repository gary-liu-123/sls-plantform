import { useState } from 'react'

const API_URL = window.location.hostname === 'localhost'
  ? 'http://localhost:8283/api/upload'
  : `http://${window.location.hostname}:8283/api/upload`

function App() {
  const [phone, setPhone] = useState('')
  const [preview, setPreview] = useState(null)
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

    setDebugInfo(`API: ${API_URL}\nPhone: ${phone}\nFile: ${file.name}, Size: ${file.size} bytes, Type: ${file.type}`)

    const reader = new FileReader()
    reader.onload = (e) => {
      setPreview(e.target.result)
    }
    reader.onerror = (e) => {
      setDebugInfo(prev => prev + '\nFileReader error: ' + e)
    }
    reader.readAsDataURL(file)

    const formData = new FormData()
    formData.append('file', file)
    formData.append('phone', phone.trim())

    setUploadStatus('上传中...')

    fetch(API_URL, {
      method: 'POST',
      body: formData
    })
      .then((res) => {
        return res.text().then(text => ({ ok: res.ok, text }))
      })
      .then(({ ok, text }) => {
        if (ok) {
          setUploadStatus('上传成功')
          setDebugInfo(prev => prev + '\nResponse: ' + text)
        } else {
          setUploadStatus('上传失败: ' + text)
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

      {preview && <img src={preview} alt="预览" style={{ maxWidth: '100%', marginTop: '16px' }} />}

      {uploadStatus && <p style={{ marginTop: '8px' }}>{uploadStatus}</p>}
      {debugInfo && <pre style={{ fontSize: '10px', marginTop: '8px', color: '#666', whiteSpace: 'pre-wrap' }}>{debugInfo}</pre>}
    </div>
  )
}

export default App
