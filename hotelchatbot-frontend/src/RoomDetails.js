import { useParams } from 'react-router-dom'
import { useEffect, useState } from 'react'

export function RoomDetails() {
  const { id } = useParams()
  const [hotel, setHotel] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchHotel() {
      setLoading(true)
      try {
        const res = await fetch(`http://localhost:8081/api/hotels/${id}`)
        const data = await res.json()
        setHotel(data)
      } catch (err) {
        setHotel(null)
      }
      setLoading(false)
    }
    fetchHotel()
  }, [id])

  if (loading) return <div className="container my-5">Loading...</div>
  if (!hotel) return <div className="container my-5">Hotel not found.</div>

  return (
    <div className="container my-5">
      <div style={{ display: 'flex', gap: 32 }}>
        {hotel.imageUrl && (
          <img
            src={hotel.imageUrl}
            alt={hotel.name}
            style={{
              width: 350,
              height: 220,
            }}
          />
        )}
        <div>
          <h2 style={{ marginBottom: 0 }}>{hotel.name}</h2>
          <div style={{ color: '#f5a623', fontWeight: 'bold', fontSize: 18 }}>
            <span style={{ color: '#333', fontWeight: 'normal', fontSize: 16 }}>
              ({hotel.starRating} stars)
            </span>
          </div>
          <div style={{ margin: '8px 0', color: '#555' }}>
            {hotel.address}, {hotel.city}
          </div>
          <p style={{ maxWidth: 500 }}>{hotel.description}</p>
          <div>
            Amenities:
            <ul
              style={{
                display: 'flex',
                flexWrap: 'wrap',
                gap: 12,
                listStyle: 'none',
                padding: 0,
              }}
            >
              {hotel.amenities && hotel.amenities.length > 0 ? (
                hotel.amenities.map((a, i) => (
                  <li
                    key={i}
                    style={{
                      background: '#f0f0f0',
                    }}
                  >
                    {a}
                  </li>
                ))
              ) : (
                <li>No amenities listed.</li>
              )}
            </ul>
          </div>
        </div>
      </div>

      <h3 style={{ marginTop: 40 }}>Available Rooms</h3>
      <div>
        {hotel.rooms && hotel.rooms.length > 0 ? (
          <table className="table" style={{ marginTop: 10 }}>
            <thead>
              <tr>
                <th>Type</th>
                <th>Description</th>
                <th>Amenities</th>
                <th>Price</th>
              </tr>
            </thead>
            <tbody>
              {hotel.rooms.map((room) => (
                <tr key={room.id}>
                  <td>{room.type}</td>
                  <td>{room.description}</td>
                  <td>
                    {room.amenities && room.amenities.length > 0
                      ? room.amenities.join(', ')
                      : 'None'}
                  </td>
                  <td>${room.price}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div>No rooms available.</div>
        )}
      </div>
    </div>
  )
}
