import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'

const AMENITIES = [
  'Free Wi-Fi',
  'TV',
  'Mini-bar',
  'Room Service',
  'Coffee Maker',
  'Pool Access',
  'Free Breakfast',
]
const STAR_RATINGS = [1, 2, 3, 4, 5]

export function Hero() {
  const [searchBarFilters, setSearchBarFilters] = useState({
    destination: '',
    guests: '',
    checkIn: '',
    checkOut: '',
  })

  const handleSearchBarChange = (e) => {
    const { name, value } = e.target
    setSearchBarFilters({
      ...searchBarFilters,
      [name]: value,
    })
  }

  const [aiQuery, setAiQuery] = useState('')

  const handleAiSearch = async (e) => {
    e.preventDefault()
    const res = await fetch(
      'http://localhost:8083/api/chatbot/api/hotels/search-ai',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(aiQuery),
      }
    )
    const data = await res.json()
    console.log(data)
    setFilteredRooms(data)
  }

  const [filters, setFilters] = useState({
    propertyName: '',
    minPrice: '',
    maxPrice: '',
    amenities: [],
    starRating: '',
    destination: '',
    guests: '',
    checkIn: '',
    checkOut: '',
  })
  const [filteredRooms, setFilteredRooms] = useState([])

  const handleFilterChange = (e) => {
    const { name, value, type, checked } = e.target
    let newFilters = { ...filters }
    if (type === 'checkbox' && name === 'amenities') {
      if (checked) {
        newFilters.amenities = [...filters.amenities, value]
      } else {
        newFilters.amenities = filters.amenities.filter((a) => a !== value)
      }
    } else {
      newFilters[name] = value
    }
    setFilters(newFilters)
    handleSearch(newFilters)
  }

  const handleSearch = async (form = filters) => {
    const params = new URLSearchParams()
    if (form.destination) params.append('city', form.destination)
    if (form.guests) params.append('guests', form.guests)
    if (form.checkIn) params.append('checkIn', form.checkIn)
    if (form.checkOut) params.append('checkOut', form.checkOut)
    if (form.propertyName) params.append('propertyName', form.propertyName)
    if (form.minPrice) params.append('minPrice', form.minPrice)
    if (form.maxPrice) params.append('maxPrice', form.maxPrice)
    if (form.starRating) params.append('starRating', form.starRating)
    if (form.amenities.length > 0)
      params.append('amenities', form.amenities.join(','))

    try {
      const url = `/api/chatbot/api/hotels?${params}`
      console.log('Fetching:', url)
      const res = await fetch(`http://localhost:8083${url}`)
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`)
      }
      const text = await res.text()
      const data = text ? JSON.parse(text) : []
      console.log(data)
      setFilteredRooms(data)
    } catch (err) {
      console.log(err)
      setFilteredRooms([])
    }
  }

  // Initial load
  useEffect(() => {
    handleSearch(filters)
    // eslint-disable-next-line
  }, [])

  return (
    <div>
      <div className="container my-4">
        {/* Expedia-style filter header */}
        <form
          className="row g-2 align-items-end mb-5"
          onSubmit={(e) => {
            e.preventDefault()
            const merged = { ...filters, ...searchBarFilters }
            setFilters(merged)
            handleSearch(merged)
          }}
        >
          <div className="col-md-3">
            <input
              type="text"
              className="form-control"
              id="destination"
              name="destination"
              value={searchBarFilters.destination}
              onChange={handleSearchBarChange}
              placeholder="City"
            />
          </div>
          <div className="col-md-2">
            <input
              type="date"
              className="form-control"
              id="checkIn"
              name="checkIn"
              value={searchBarFilters.checkIn}
              onChange={handleSearchBarChange}
            />
          </div>
          <div className="col-md-2">
            <input
              type="date"
              className="form-control"
              id="checkOut"
              name="checkOut"
              value={searchBarFilters.checkOut}
              onChange={handleSearchBarChange}
            />
          </div>
          <div className="col-md-2">
            <input
              type="number"
              className="form-control"
              id="guests"
              name="guests"
              min="1"
              value={searchBarFilters.guests}
              onChange={handleSearchBarChange}
              placeholder="Guests"
            />
          </div>
          <div className="col-md-2">
            <button type="submit" className="btn btn-primary w-100">
              Search
            </button>
          </div>
        </form>
        <div className="row">
          {/* Sidebar Filters */}
          <aside className="col-md-3 mb-3">
            <div className="card p-3">
              <h5>Filters</h5>

              <form onSubmit={handleAiSearch} className="mb-3">
                <label className="form-label">AI Search</label>
                <div className="input-group flex-nowrap">
                  <textarea
                    className="form-control form-control-lg"
                    style={{
                      minWidth: 0,
                      width: '100%',
                      resize: 'vertical',
                      minHeight: '48px',
                      fontSize: '14px',
                    }}
                    placeholder="Hotels with breakfast"
                    value={aiQuery}
                    onChange={(e) => setAiQuery(e.target.value)}
                    rows={2}
                  />
                  <button
                    className="btn btn-primary"
                    type="submit"
                    style={{ minWidth: 12 }}
                  ></button>
                </div>
              </form>

              {/* Property Name */}
              <div className="mb-3">
                <label className="form-label">Property Name</label>
                <input
                  type="text"
                  className="form-control"
                  name="propertyName"
                  value={filters.propertyName}
                  onChange={handleFilterChange}
                  placeholder=""
                />
              </div>
              {/* Price Range */}
              <div className="mb-3">
                <label className="form-label">Min Price</label>
                <input
                  type="number"
                  className="form-control"
                  name="minPrice"
                  value={filters.minPrice}
                  onChange={handleFilterChange}
                  min="0"
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Max Price</label>
                <input
                  type="number"
                  className="form-control"
                  name="maxPrice"
                  value={filters.maxPrice}
                  onChange={handleFilterChange}
                  min="0"
                />
              </div>
              {/* Star Rating */}
              <div className="mb-3">
                <label className="form-label">Star Rating</label>
                <select
                  className="form-select"
                  name="starRating"
                  value={filters.starRating}
                  onChange={handleFilterChange}
                >
                  <option value="">Any</option>
                  {STAR_RATINGS.map((star) => (
                    <option key={star} value={star}>
                      {star} Star{star > 1 ? 's' : ''}
                    </option>
                  ))}
                </select>
              </div>
              {/* Amenities */}
              <div className="mb-3">
                <label className="form-label">Amenities</label>
                <div className="d-flex flex-column">
                  {AMENITIES.map((amenity) => (
                    <div key={amenity} className="form-check mb-1">
                      <input
                        className="form-check-input"
                        type="checkbox"
                        name="amenities"
                        value={amenity}
                        checked={filters.amenities.includes(amenity)}
                        onChange={handleFilterChange}
                        id={`amenity-${amenity}`}
                      />
                      <label
                        className="form-check-label"
                        htmlFor={`amenity-${amenity}`}
                      >
                        {amenity}
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </aside>
          {/* Hotel/Room Listings (right side) */}
          <main className="col-md-9">
            {filteredRooms.map((hotel) => (
              <div className="col-12 mb-4" key={hotel.id}>
                <Link
                  to={`/rooms/${hotel.id}`}
                  style={{ textDecoration: 'none', color: 'inherit' }}
                >
                  <div className="card flex-row" style={{ minHeight: '200px' }}>
                    {hotel.imageUrl && (
                      <div style={{ flex: '0 0 200px', maxWidth: '200px' }}>
                        <img
                          src={hotel.imageUrl}
                          className="card-img-left"
                          alt={hotel.name}
                          style={{
                            width: '100%',
                            height: '100%',
                            objectFit: 'cover',
                          }}
                        />
                      </div>
                    )}
                    <div
                      className="card-body d-flex flex-column justify-content-between"
                      style={{ flex: 1 }}
                    >
                      <div>
                        <h5 className="card-title">{hotel.name}</h5>
                        <p className="card-text mb-1">{hotel.city}</p>
                        {hotel.description && (
                          <p
                            className="card-text mb-2"
                            style={{ color: '#555', fontSize: '0.95rem' }}
                          >
                            {hotel.description}
                          </p>
                        )}
                        <p className="card-text mb-1">
                          <strong>Stars:</strong> {hotel.starRating}{' '}
                          &nbsp;|&nbsp;
                          <strong>Avg. Price:</strong> ${hotel.averagePrice}
                        </p>
                        <p className="card-text mb-1">
                          <strong>Times Booked:</strong> {hotel.timesBooked}
                        </p>
                      </div>
                    </div>
                  </div>
                </Link>
              </div>
            ))}
          </main>
        </div>
      </div>
    </div>
  )
}
