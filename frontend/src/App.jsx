import { useEffect, useMemo, useState } from 'react'
import { getCounties, getTowns, createTown, updateTown, deleteTown, errorText } from './api.js'
import TownForm from './TownForm.jsx'

const PAGE_SIZE = 15
// TODO: same names / Neptun codes as in the footer of the other pages (templates/fragments.html)
const CREATORS = 'Mogajane Ditso Ofile (HHODUU)'

export default function App() {
  const [towns, setTowns] = useState([])
  const [counties, setCounties] = useState([])
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState(null)   // { type: 'success' | 'danger', text }
  const [editing, setEditing] = useState(null)
  const [search, setSearch] = useState('')
  const [countyFilter, setCountyFilter] = useState('')
  const [page, setPage] = useState(1)

  // READ
  const load = async () => {
    try {
      const [c, t] = await Promise.all([getCounties(), getTowns()])
      setCounties(c)
      setTowns(t.sort((a, b) => a.name.localeCompare(b.name, 'hu')))  // Hungarian alphabetical order
    } catch (err) {
      setMessage({ type: 'danger', text: 'Could not load data: ' + errorText(err) })
    } finally {
      setLoading(false)
    }
  }
  useEffect(() => { load() }, [])

  // CREATE + UPDATE
  const save = async (town) => {
    try {
      if (editing) {
        const updated = await updateTown(editing.id, town)
        setTowns(ts => ts.map(t => (t.id === updated.id ? updated : t)))
        setMessage({ type: 'success', text: `"${updated.name}" was updated.` })
        setEditing(null)
      } else {
        const created = await createTown(town)
        setTowns(ts => [...ts, created].sort((a, b) => a.name.localeCompare(b.name, 'hu')))
        setMessage({ type: 'success', text: `"${created.name}" was created (id ${created.id}).` })
      }
      return true
    } catch (err) {
      setMessage({ type: 'danger', text: errorText(err) })
      return false
    }
  }

  // DELETE
  const remove = async (town) => {
    if (!window.confirm(`Delete "${town.name}"?`)) return
    try {
      await deleteTown(town.id)
      setTowns(ts => ts.filter(t => t.id !== town.id))
      if (editing?.id === town.id) setEditing(null)
      setMessage({ type: 'success', text: `"${town.name}" was deleted.` })
    } catch (err) {
      setMessage({ type: 'danger', text: errorText(err) })
    }
  }

  const startEdit = (town) => {
    setEditing(town)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  // search + county filter + paging (client side)
  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase()
    return towns.filter(t =>
      (!q || t.name.toLowerCase().includes(q)) &&
      (!countyFilter || String(t.countyId) === countyFilter))
  }, [towns, search, countyFilter])

  const pages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE))
  const current = Math.min(page, pages)
  const visible = filtered.slice((current - 1) * PAGE_SIZE, current * PAGE_SIZE)

  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary sticky-top shadow-sm">
        <div className="container">
          <a className="navbar-brand fw-bold" href="/"><i className="bi bi-graph-up-arrow"></i> FX Horizon</a>
          <div className="navbar-nav ms-auto flex-row flex-wrap gap-3">
            <a className="nav-link" href="/">Mainpage</a>
            <a className="nav-link" href="/soap">SOAP</a>
            <a className="nav-link" href="/forex/account">Forex</a>
            <a className="nav-link" href="/calc">Test</a>
            <a className="nav-link active" href="/react/">React</a>
          </div>
        </div>
      </nav>

      <div className="page-header">
        <div className="container">
          <h1><i className="bi bi-buildings"></i> React &ndash; Hungarian towns (CRUD)</h1>
          <p className="mb-0 opacity-75">React + Axios client, Spring Boot REST API, H2 database (towns_db).</p>
        </div>
      </div>

      <main className="container">
        {message && (
          <div className={`alert alert-${message.type} alert-dismissible`}>
            {message.text}
            <button type="button" className="btn-close" onClick={() => setMessage(null)} aria-label="Close"></button>
          </div>
        )}

        <TownForm counties={counties} editing={editing} onSave={save} onCancel={() => setEditing(null)} />

        <div className="card p-4">
          <div className="row g-2 mb-3">
            <div className="col-md-6">
              <input className="form-control" placeholder="Search by name…" value={search}
                     onChange={e => { setSearch(e.target.value); setPage(1) }} />
            </div>
            <div className="col-md-4">
              <select className="form-select" value={countyFilter}
                      onChange={e => { setCountyFilter(e.target.value); setPage(1) }}>
                <option value="">All counties</option>
                {counties.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
            <div className="col-md-2 text-md-end align-self-center text-muted">
              {filtered.length} towns
            </div>
          </div>

          {loading ? <p>Loading…</p> : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-dark">
                  <tr><th>ID</th><th>Name</th><th>County</th><th>County seat</th><th>County rights</th><th></th></tr>
                </thead>
                <tbody>
                  {visible.map(t => (
                    <tr key={t.id} className={editing?.id === t.id ? 'table-warning' : ''}>
                      <td>{t.id}</td>
                      <td className="fw-semibold">{t.name}</td>
                      <td>{t.countyName}</td>
                      <td>{t.countySeat ? <i className="bi bi-check-lg text-success"></i> : ''}</td>
                      <td>{t.countyLevel ? <i className="bi bi-check-lg text-success"></i> : ''}</td>
                      <td className="text-end text-nowrap">
                        <button className="btn btn-sm btn-outline-primary me-2" onClick={() => startEdit(t)}>
                          <i className="bi bi-pencil"></i> Edit
                        </button>
                        <button className="btn btn-sm btn-outline-danger" onClick={() => remove(t)}>
                          <i className="bi bi-trash"></i> Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                  {visible.length === 0 && <tr><td colSpan="6" className="text-muted">No towns found.</td></tr>}
                </tbody>
              </table>
            </div>
          )}

          {pages > 1 && (
            <div className="d-flex justify-content-between align-items-center mt-3">
              <button className="btn btn-outline-secondary btn-sm" disabled={current === 1}
                      onClick={() => setPage(current - 1)}>&laquo; Previous</button>
              <span className="text-muted">Page {current} / {pages}</span>
              <button className="btn btn-outline-secondary btn-sm" disabled={current === pages}
                      onClick={() => setPage(current + 1)}>Next &raquo;</button>
            </div>
          )}
        </div>
      </main>

      <footer className="footer bg-dark text-light py-4 mt-5">
        <div className="container d-md-flex justify-content-between text-center text-md-start">
          <div><strong>FX Horizon</strong> &middot; Java applications &ndash; Lecture homework</div>
          <div className="small mt-2 mt-md-0">Created by: <span className="fw-semibold">{CREATORS}</span></div>
        </div>
      </footer>
    </>
  )
}
