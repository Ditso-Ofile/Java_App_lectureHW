import { useEffect, useState } from 'react'

const EMPTY = { name: '', countyId: '', countySeat: false, countyLevel: false }

/** Create / Update form. If `editing` is a town, the form is in edit mode. */
export default function TownForm({ counties, editing, onSave, onCancel }) {
  const [form, setForm] = useState(EMPTY)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    setForm(editing
      ? { name: editing.name, countyId: String(editing.countyId), countySeat: editing.countySeat, countyLevel: editing.countyLevel }
      : EMPTY)
    setErrors({})
  }, [editing])

  const change = (e) => {
    const { name, value, type, checked } = e.target
    setForm(f => ({ ...f, [name]: type === 'checkbox' ? checked : value }))
  }

  // client-side validation (the server validates again)
  const validate = () => {
    const e = {}
    if (!form.name.trim()) e.name = 'The town name is required.'
    else if (form.name.trim().length > 100) e.name = 'At most 100 characters.'
    if (!form.countyId) e.countyId = 'Please choose a county.'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const submit = async (e) => {
    e.preventDefault()
    if (!validate()) return
    setSaving(true)
    const ok = await onSave({ ...form, name: form.name.trim(), countyId: Number(form.countyId) })
    setSaving(false)
    if (ok && !editing) setForm(EMPTY)
  }

  return (
    <form className="card p-4 mb-4" onSubmit={submit} noValidate>
      <h5 className="mb-3">
        {editing ? <><i className="bi bi-pencil-square"></i> Edit town #{editing.id}</>
                 : <><i className="bi bi-plus-circle"></i> New town</>}
      </h5>
      <div className="row g-3 align-items-start">
        <div className="col-md-4">
          <label className="form-label" htmlFor="name">Town name</label>
          <input id="name" name="name" className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                 value={form.name} onChange={change} maxLength={100} />
          <div className="invalid-feedback">{errors.name}</div>
        </div>
        <div className="col-md-4">
          <label className="form-label" htmlFor="countyId">County</label>
          <select id="countyId" name="countyId" className={`form-select ${errors.countyId ? 'is-invalid' : ''}`}
                  value={form.countyId} onChange={change}>
            <option value="">-- choose --</option>
            {counties.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <div className="invalid-feedback">{errors.countyId}</div>
        </div>
        <div className="col-md-4 pt-md-4">
          <div className="form-check">
            <input id="countySeat" name="countySeat" type="checkbox" className="form-check-input"
                   checked={form.countySeat} onChange={change} />
            <label className="form-check-label" htmlFor="countySeat">County seat</label>
          </div>
          <div className="form-check">
            <input id="countyLevel" name="countyLevel" type="checkbox" className="form-check-input"
                   checked={form.countyLevel} onChange={change} />
            <label className="form-check-label" htmlFor="countyLevel">City with county rights</label>
          </div>
        </div>
      </div>
      <div className="mt-3 d-flex gap-2">
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Saving…' : editing ? 'Save changes' : 'Create'}
        </button>
        {editing && <button type="button" className="btn btn-outline-secondary" onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  )
}
