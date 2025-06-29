import { useState, useEffect, useRef } from 'react'
import keycloak from './keycloak'
import { useKeycloak } from '@react-keycloak/web'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function Chat({ setHasMessages }) {
  const { keycloak } = useKeycloak()
  const stompClientRef = useRef(null)

  const [sessionId, setSessionId] = useState(() =>
    localStorage.getItem('chatSessionId')
  )
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)

  // Connect to WebSocket when session is ready
  useEffect(() => {
    if (sessionId && keycloak.authenticated) {
      connectWebSocket()
    }

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate()
      }
    }
  }, [sessionId, keycloak.authenticated])

  const connectWebSocket = () => {
    const socket = new SockJS('http://localhost:8081/chat-websocket')
    const stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('Connected to chat')

        // Subscribe to messages for this session
        stompClient.subscribe(`/topic/session/${sessionId}`, (message) => {
          const chatMessage = JSON.parse(message.body)
          setMessages((prev) => [
            ...prev,
            {
              sender: chatMessage.sender,
              content: chatMessage.content,
            },
          ])
        })
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame)
      },
    })

    stompClient.activate()
    stompClientRef.current = stompClient
  }

  // Load initial messages and create session
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
          console.error(data.error || 'Failed to create session')
        }
      })
  }

  const sendMessage = async () => {
    if (!input.trim()) return
    setLoading(true)
    setInput('')

    try {
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
        await createNewSession()
        return
      }
    } catch (error) {
      console.error('Error sending message:', error)
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
                background:
                  m.sender === 'user'
                    ? '#007bff'
                    : m.sender === 'agent'
                    ? '#28a745'
                    : '#e6f4ea',
                color:
                  m.sender === 'user' || m.sender === 'agent'
                    ? '#fff'
                    : '#28a745',
                padding: '10px 16px',
                borderRadius: '18px',
                maxWidth: '70%',
                wordWrap: 'break-word',
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
          placeholder="Type a message..."
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
