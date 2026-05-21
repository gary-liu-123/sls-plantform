import { useState } from 'react'

const API_URL = window.location.hostname === 'localhost'
  ? 'http://localhost:8283/api/upload'
  : `http://${window.location.hostname}:8283/api/upload`

function App() {
  const [preview, setPreview] = useState(null)
  const [uploadStatus, setUploadStatus] = useState('')
  const [debugInfo, setDebugInfo] = useState('')

  const handleFileChange = (event) => {
    const file = event.target.files[0]
    if (!file) {
      setDebugInfo('No file selected')
      return
    }

    setDebugInfo(`API: ${API_URL}\nFile: ${file.name}, Size: ${file.size} bytes, Type: ${file.type}`)

    // Read file for preview
    const reader = new FileReader()
    reader.onload = (e) => {
      setPreview(e.target.result)
    }
    reader.onerror = (e) => {
      setDebugInfo(prev => prev + '\nFileReader error: ' + e)
    }
    reader.readAsDataURL(file)

    // Upload to backend
    const formData = new FormData()
    formData.append('file', file)

    setUploadStatus('上传中...')

    fetch(API_URL, {
      method: 'POST',
      body: formData
    })
      .then((res) => {
        if (res.ok) {
          setUploadStatus('上传成功')
        } else {
          res.text().then(text => {
            setUploadStatus('上传失败: ' + text)
          }).catch(() => {
            setUploadStatus('上传失败')
          })
        }
      })
      .catch((err) => {
        setUploadStatus('上传失败: ' + err.message)
      })
  }

  return (
    <div>
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
      <button onClick={() => document.getElementById('galleryInput').click()}>
        照片上传
      </button>

      {preview && <img src={preview} alt="预览" style={{ maxWidth: '100%', marginTop: '16px' }} />}

      {uploadStatus && <p style={{ marginTop: '8px' }}>{uploadStatus}</p>}
      {debugInfo && <pre style={{ fontSize: '10px', marginTop: '8px', color: '#666' }}>{debugInfo}</pre>}
    </div>
  )
}

export default App