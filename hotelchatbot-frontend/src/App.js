import { Navbar } from './Navbar'
import { Hero } from './Hero'
import { RoomDetails } from './RoomDetails'
import { useState, useEffect } from 'react'
import { Routes, Route, useLocation } from 'react-router-dom'
import { FloatingChat } from './FloatingChat'

export function App() {
  const [chatOpen, setChatOpen] = useState(false)
  const [hasMessages, setHasMessages] = useState(false)
  const location = useLocation()

  useEffect(() => {
    if (!hasMessages) setChatOpen(false)
  }, [location, hasMessages])

  return (
    <div>
      <Navbar />
      <Routes>
        <Route path="/" element={<Hero />} />
        <Route path="/rooms/:id" element={<RoomDetails />} />
      </Routes>
      <FloatingChat
        open={chatOpen}
        setOpen={setChatOpen}
        setHasMessages={setHasMessages}
      />
    </div>
  )
}
