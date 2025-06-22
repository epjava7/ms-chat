import { Chat } from './Chat'

export function FloatingChat({ open, setOpen, setHasMessages }) {
  //   const [open, setOpen] = useState(false)

  // Add this function to handle chat close and session deletion
  const handleClose = async () => {
    if (window.confirm('End chat?')) {
      const sessionId = localStorage.getItem('chatSessionId')
      if (sessionId) {
        await fetch(`http://localhost:8081/api/chatbot/session/${sessionId}`, {
          method: 'DELETE',
        })
        localStorage.removeItem('chatSessionId')
      }
      setOpen(false)
    }
  }

  return (
    <>
      {open && (
        <div
          style={{
            position: 'fixed',
            bottom: 80,
            right: 20,
            zIndex: 1000,
            background: '#fff',
            border: '1px solid #aaa',
            width: 320,
            maxWidth: '95vw',
            boxShadow: '2px 2px 8px #ccc',
            padding: 0,
            color: '#000',
          }}
        >
          <div
            style={{
              padding: '6px 10px',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              borderBottom: '1px solid #ccc',
              color: '#000',
            }}
          >
            <span style={{ fontWeight: 'bold', fontSize: 16, color: '#000' }}>
              Support
            </span>
            <button
              style={{
                background: 'none',
                fontSize: 18,
                cursor: 'pointer',
                color: '#333',
                border: 'none',
              }}
              onClick={handleClose}
            >
              x
            </button>
          </div>
          <div
            style={{
              maxHeight: 350,
              overflow: 'auto',
              padding: 8,
              color: '#000',
            }}
          >
            <Chat setHasMessages={setHasMessages} />
          </div>
        </div>
      )}
      <button
        onClick={() => setOpen((v) => !v)}
        style={{
          position: 'fixed',
          bottom: 20,
          right: 20,
          zIndex: 1000,
          background: '#1976d2',
          color: '#fff',
          border: '1px solid #aaa',
          borderRadius: '50%',
          width: 48,
          height: 48,
          fontSize: 22,
          cursor: 'pointer',
        }}
      >
        ?
      </button>
    </>
  )
}
