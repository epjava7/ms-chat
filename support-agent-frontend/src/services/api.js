const API_BASE = 'http://localhost:8083/api/chatbot/api'

export async function getEscalatedSessions() {
  const res = await fetch(`${API_BASE}/escalated-sessions`)
  if (!res.ok) throw new Error('Failed to fetch sessions')
  return res.json()
}

export async function getSessionMessages(sessionId) {
  const res = await fetch(`${API_BASE}/chatbot/session/${sessionId}/messages`)
  if (!res.ok) throw new Error('Failed to fetch messages')
  return res.json()
}

export async function sendReply(sessionId, reply) {
  const res = await fetch(`${API_BASE}/reply?sessionId=${sessionId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reply),
  })
  if (!res.ok) throw new Error('Failed to send reply')
}
