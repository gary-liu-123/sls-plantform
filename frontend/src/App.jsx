import { useState } from 'react'

const API_URL = 'http://localhost:8283/api/upload'

function App() {
  const [preview, setPreview] = useState(null)
  const [uploadStatus, setUploadStatus] = useState('')

  const handleFileChange = (event) => {
    const file = event.target.files[0]
    if (!file) return

    // Read file for preview
    const reader = new FileReader()
    reader.onload = (e) => {
      setPreview(e.target.result)
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
          setUploadStatus('上传失败')
        }
      })
      .catch(() => {
        setUploadStatus('上传失败')
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
    </div>
  )
}

export default App