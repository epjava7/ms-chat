import { useState, useEffect } from 'react'
import keycloak from './keycloak'
import { useKeycloak } from '@react-keycloak/web'

export function Chat({ setHasMessages }) {
  const { keycloak } = useKeycloak()

  const [sessionId, setSessionId] = useState(() =>
    localStorage.getItem('chatSessionId')
  )
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!sessionId) {
      createNewSession()
    } else {
      fetch(`http://localhost:8081/api/chatbot/session/${sessionId}/messages`)
        .then((res) => {
          if (res.ok) return res.json()
          throw new Error('Session not found')
        })
        .then(setMessages)
        .catch(() => {
          createNewSession()
        })
    }
  }, [])

  useEffect(() => {
    if (setHasMessages) setHasMessages(messages.length > 0)
  }, [messages, setHasMessages])

  // function createNewSession() {
  //   // fetch('http://localhost:8083/api/chatbot/api/chatbot/session', {
  //   //   method: 'POST',
  //   //   headers: { 'Content-Type': 'application/json' },
  //   // })
  //   fetch('http://localhost:8083/api/chatbot/api/chatbot/session', {
  //     method: 'POST',
  //     headers: {
  //       'Content-Type': 'application/json',
  //       Authorization: `Bearer ${keycloak.token}`,
  //     },
  //   })
  //     .then((res) => res.json())
  //     .then((data) => {
  //       setSessionId(data.id)
  //       localStorage.setItem('chatSessionId', data.id)
  //       setMessages([])
  //     })
  // }

  function createNewSession() {
    fetch('http://localhost:8083/api/chatbot/api/chatbot/session', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${keycloak.token}`,
      },
    })
      .then(async (res) => {
        const text = await res.text()
        try {
          return text ? JSON.parse(text) : {}
        } catch (e) {
          return { error: 'Invalid JSON response from server' }
        }
      })
      .then((data) => {
        if (data && data.id) {
          setSessionId(data.id)
          localStorage.setItem('chatSessionId', data.id)
          setMessages([])
        } else {
          // Optionally handle error here
          console.error(data.error || 'Failed to create session')
        }
      })
  }

  useEffect(() => {
    if (!sessionId) {
      createNewSession()
    } else {
      //   fetch(
      //     `http://localhost:8083/api/chatbot/api/chatbot/session/${sessionId}/messages`
      //   )
      fetch(
        `http://localhost:8083/api/chatbot/api/chatbot/session/${sessionId}/messages`,
        {
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
          },
        }
      )
        .then((res) => {
          if (res.ok) return res.json()
          throw new Error('Session not found')
        })
        .then(setMessages)
        .catch(() => {
          createNewSession()
        })
    }
  }, [sessionId])

  // send message, get reply
  const sendMessage = async () => {
    if (!input.trim()) return
    setLoading(true)
    try {
      //   const res = await fetch(
      //     `http://localhost:8083/api/chatbot/api/chatbot/session/${sessionId}/message`,
      //     {
      //       method: 'POST',
      //       headers: { 'Content-Type': 'application/json' },
      //       body: JSON.stringify(input),
      //     }
      //   )
      const res = await fetch(
        `http://localhost:8083/api/chatbot/api/chatbot/session/${sessionId}/message`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${keycloak.token}`,
          },
          body: JSON.stringify(input),
        }
      )
      if (res.status === 404) {
        // Session not found, create new session and retry
        await createNewSession()
        setLoading(false)
        return
      }
      // get all messages again
      fetch(`http://localhost:8081/api/chatbot/session/${sessionId}/messages`)
        .then((res) => res.json())
        .then(setMessages)
      setInput('')
    } finally {
      setLoading(false)
    }
  }

  if (!keycloak.authenticated) {
    return (
      <div>
        <p>Login to use chat</p>
        <button className="btn btn-primary" onClick={() => keycloak.login()}>
          Login
        </button>
      </div>
    )
  }

  return (
    <div>
      <div style={{ minHeight: 200, marginBottom: 16 }}>
        {messages.map((m, i) => (
          <div
            key={i}
            style={{
              display: 'flex',
              justifyContent: m.sender === 'user' ? 'flex-end' : 'flex-start',
              margin: '8px 0',
            }}
          >
            <div
              style={{
                background: m.sender === 'user' ? '#007bff' : '#e6f4ea',
                color: m.sender === 'user' ? '#fff' : '#28a745',
                padding: '10px 16px',
                textAlign: m.sender === 'user' ? 'right' : 'left',
                alignSelf: m.sender === 'user' ? 'flex-end' : 'flex-start',
              }}
            >
              {m.content.replace(/^"(.*)"$/, '$1')}
            </div>
          </div>
        ))}
      </div>
      <div style={{ display: 'flex', gap: 8 }}>
        <input
          className="form-control"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
          disabled={loading}
          placeholder=""
        />
        <button
          className="btn btn-primary"
          onClick={sendMessage}
          disabled={loading || !input.trim()}
        >
          Send
        </button>
      </div>
    </div>
  )
}
