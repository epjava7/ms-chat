import { useEffect, useState, useRef } from 'react'
import { getEscalatedSessions } from '../services/api'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export function EscalatedSessionList({ onSelect }) {
  const [sessions, setSessions] = useState([])
  const stompClientRef = useRef(null)

  useEffect(() => {
    getEscalatedSessions().then(setSessions)
    connectWebSocket()

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate()
      }
    }
  }, [])

  const connectWebSocket = () => {
    const socket = new SockJS('http://localhost:8081/chat-websocket')
    const stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('Sessions list connected')

        // Listen for new escalations
        stompClient.subscribe('/topic/agents', (message) => {
          const agentMessage = JSON.parse(message.body)
          if (agentMessage.content === 'NEW_ESCALATION') {
            // Refresh the sessions list
            getEscalatedSessions().then(setSessions)
          }
        })
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame)
      },
    })

    stompClient.activate()
    stompClientRef.current = stompClient
  }

  return (
    <div className="container mt-4">
      <div className="card">
        <div className="card-header">Escalated Sessions</div>
        <ul className="list-group list-group-flush">
          {sessions.map((session) => (
            <li className="list-group-item" key={session.id}>
              <button
                onClick={() => onSelect(session.id)}
                style={{
                  background: 'none',
                  border: 'none',
                  textAlign: 'left',
                  width: '100%',
                  padding: '8px 0',
                  cursor: 'pointer',
                }}
              >
                <div>Session #{session.id}</div>
                <div>username: {session.username || 'Anonymous'}</div>
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
