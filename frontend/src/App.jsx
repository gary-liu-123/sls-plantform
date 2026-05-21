import { useState } from 'react'

const API_URL = window.location.hostname === 'localhost'
  ? 'http://localhost:8283/api/upload'
  : `http://${window.location.hostname}:8283/api/upload`

function App() {
  const [preview, setPreview] = useState(null)
  const [uploadStatus, setUploadStatus] = useState('')

  const handleFileChange = (event) => {
    const file = event.target.files[0]
    if (!file) {
      console.log('No file selected')
      return
    }

    console.log('File selected:', file.name, 'size:', file.size, 'type:', file.type)

    // Read file for preview
    const reader = new FileReader()
    reader.onload = (e) => {
      setPreview(e.target.result)
    }
    reader.onerror = (e) => {
      console.error('FileReader error:', e)
    }
    reader.readAsDataURL(file)

    // Upload to backend
    const formData = new FormData()
    formData.append('file', file)

    console.log('Uploading to:', API_URL)

    setUploadStatus('上传中...')

    fetch(API_URL, {
      method: 'POST',
      body: formData,
      headers: {
        'Accept': 'application/json'
      }
    })
      .then((res) => {
        console.log('Response status:', res.status, 'ok:', res.ok)
        console.log('Response headers:', [...res.headers.entries()])
        if (res.ok) {
          setUploadStatus('上传成功')
        } else {
          res.text().then(text => {
            console.error('Error response body:', text)
            setUploadStatus('上传失败: ' + text)
          }).catch(() => {
            setUploadStatus('上传失败')
          })
        }
      })
      .catch((err) => {
        console.error('Upload error:', err)
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