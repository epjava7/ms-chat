import React from 'react'
import { useKeycloak } from '@react-keycloak/web'
import { Link, useLocation } from 'react-router-dom'

export function Navbar() {
  const location = useLocation()
  const { keycloak } = useKeycloak()

  return (
    <nav className="navbar navbar-expand-lg bg-light shadow-sm">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">
          HotelStay
        </Link>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link className="nav-link" to="/">
                Home
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/hotels">
                Hotels
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/experience">
                Experience
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link" to="/about">
                About
              </Link>
            </li>
          </ul>
          <div className="d-flex align-items-center gap-2">
            {keycloak.authenticated ? (
              <button
                className="btn btn-dark"
                onClick={() =>
                  keycloak.logout({ postLogoutUri: window.location.origin })
                }
              >
                Logout
              </button>
            ) : (
              <>
                <button
                  className="btn btn-dark"
                  onClick={() => keycloak.login()}
                >
                  Login
                </button>
                <button
                  className="btn btn-dark"
                  onClick={() => keycloak.register()}
                >
                  Sign Up
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  )
}
