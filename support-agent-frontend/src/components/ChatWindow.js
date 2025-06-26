import { useEffect, useState, useRef } from 'react'
import { getSessionMessages, sendReply } from '../services/api'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function ChatWindow({ sessionId }) {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const stompClientRef = useRef(null)

  useEffect(() => {
    if (sessionId) {
      getSessionMessages(sessionId).then(setMessages)
      connectWebSocket()
    }

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate()
      }
    }
  }, [sessionId])

  const connectWebSocket = () => {
    const socket = new SockJS('http://localhost:8081/chat-websocket')
    const stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('Agent connected to chat')

        // Subscribe to messages for this session
        stompClient.subscribe(`/topic/session/${sessionId}`, (message) => {
          const chatMessage = JSON.parse(message.body)
          setMessages((prev) => {
            // Avoid duplicates
            const exists = prev.some(
              (m) =>
                m.content === chatMessage.content &&
                m.sender === chatMessage.sender
            )
            if (!exists) {
              return [
                ...prev,
                {
                  sender: chatMessage.sender,
                  content: chatMessage.content,
                },
              ]
            }
            return prev
          })
        })
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame)
      },
    })

    stompClient.activate()
    stompClientRef.current = stompClient
  }

  const handleSend = async () => {
    if (!input.trim()) return

    const message = input
    setInput('')

    // Add message to UI immediately
    setMessages((prev) => [
      ...prev,
      {
        sender: 'agent',
        content: message,
      },
    ])

    // Send via backend
    await sendReply(sessionId, message)
  }

  return (
    <div>
      <h3>Session #{sessionId}</h3>

      <div
        style={{
          minHeight: 300,
          maxHeight: 400,
          overflowY: 'auto',
          marginBottom: 8,
        }}
      >
        {messages.map((m, i) => (
          <div
            key={i}
            style={{
              marginBottom: 8,
              padding: 8,
              backgroundColor: m.sender === 'agent' ? '#e3f2fd' : '#ffffff', // Neutral for others
              borderRadius: 4,
              // Remove colored border for all
            }}
          >
            <div>
              <strong>{m.sender}</strong>
            </div>
            <div>{m.content}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'flex' }}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Type your reply..."
          style={{ flex: 1, padding: 8 }}
        />
        <button
          onClick={handleSend}
          disabled={!input.trim()}
          style={{
            padding: '8px 16px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: 4,
          }}
        >
          Send
        </button>
      </div>
    </div>
  )
}
