import { useState } from 'react'
import { EscalatedSessionList } from './components/EscalatedSessionList'
import { ChatWindow } from './components/ChatWindow'

function App() {
  const [selectedSession, setSelectedSession] = useState(null)

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', gap: 32 }}>
        <div>
          <EscalatedSessionList onSelect={setSelectedSession} />
        </div>
        <div style={{ flex: 1 }}>
          {selectedSession ? <ChatWindow sessionId={selectedSession} /> : null}
        </div>
      </div>
    </div>
  )
}

export default App
