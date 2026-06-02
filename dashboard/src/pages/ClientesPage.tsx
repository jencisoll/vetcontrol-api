import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { clientesApi } from '../api/services'
import type { ClienteResponse, ClienteRequest } from '../types'

// ── Form modal ────────────────────────────────────────────
interface FormProps {
  initial?: ClienteResponse
  onClose: () => void
}

function ClienteForm({ initial, onClose }: FormProps) {
  const qc = useQueryClient()
  const isEdit = !!initial

  const [form, setForm] = useState<ClienteRequest>({
    nombre:    initial?.nombre    ?? '',
    apellido:  initial?.apellido  ?? '',
    email:     initial?.email     ?? '',
    telefono:  initial?.telefono  ?? '',
    direccion: initial?.direccion ?? '',
  })
  const [errors, setErrors] = useState<Partial<ClienteRequest>>({})

  const validate = (): boolean => {
    const e: Partial<ClienteRequest> = {}
    if (!form.nombre.trim())    e.nombre    = 'Requerido'
    if (!form.apellido.trim())  e.apellido  = 'Requerido'
    if (!form.email.trim())     e.email     = 'Requerido'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
      e.email = 'Email inválido'
    if (!form.telefono.trim())  e.telefono  = 'Requerido'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const createMut = useMutation({
    mutationFn: clientesApi.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['clientes'] }); onClose() },
  })

  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: ClienteRequest }) =>
      clientesApi.update(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['clientes'] }); onClose() },
  })

  const isPending = createMut.isPending || updateMut.isPending

  const handleSubmit = () => {
    if (!validate()) return
    if (isEdit && initial) updateMut.mutate({ id: initial.id, data: form })
    else                   createMut.mutate(form)
  }

  const field = (
    name: keyof ClienteRequest,
    label: string,
    type = 'text',
    placeholder = ''
  ) => (
    <div>
      <label className="mb-1 block text-xs font-medium text-slate-400">{label}</label>
      <input
        type={type}
        placeholder={placeholder}
        value={form[name]}
        onChange={e => setForm(f => ({ ...f, [name]: e.target.value }))}
        className={`w-full rounded-lg bg-slate-800 border px-3 py-2 text-sm
                    text-white placeholder-slate-600 focus:outline-none
                    focus:ring-1 focus:ring-blue-500 transition-colors
                    ${errors[name] ? 'border-red-500' : 'border-slate-700'}`}
      />
      {errors[name] && (
        <p className="mt-1 text-xs text-red-400">{errors[name]}</p>
      )}
    </div>
  )

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center
                    bg-black/60 backdrop-blur-sm px-4">
      <div className="w-full max-w-md rounded-2xl bg-slate-900 border border-slate-800
                      shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-800 px-6 py-4">
          <h2 className="font-semibold text-white">
            {isEdit ? 'Editar cliente' : 'Nuevo cliente'}
          </h2>
          <button onClick={onClose}
            className="text-slate-500 hover:text-white transition-colors">✕</button>
        </div>

        <div className="grid grid-cols-2 gap-4 p-6">
          {field('nombre',    'Nombre',    'text', 'Juan')}
          {field('apellido',  'Apellido',  'text', 'Pérez')}
          <div className="col-span-2">
            {field('email',   'Email',     'email', 'juan@email.com')}
          </div>
          {field('telefono',  'Teléfono',  'tel',  '999999999')}
          {field('direccion', 'Dirección', 'text', 'Av. Lima 123')}
        </div>

        <div className="flex gap-3 border-t border-slate-800 px-6 py-4">
          <button onClick={onClose}
            className="flex-1 rounded-lg border border-slate-700 py-2 text-sm
                       text-slate-400 hover:text-white transition-colors">
            Cancelar
          </button>
          <button
            onClick={handleSubmit}
            disabled={isPending}
            className="flex-1 rounded-lg bg-blue-600 py-2 text-sm font-semibold
                       text-white hover:bg-blue-500 disabled:opacity-50 transition-colors">
            {isPending ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear'}
          </button>
        </div>
      </div>
    </div>
  )
}

// ── Main page ─────────────────────────────────────────────
export default function ClientesPage() {
  const qc = useQueryClient()
  const [page, setPage]         = useState(0)
  const [modal, setModal]       = useState<'create' | ClienteResponse | null>(null)

  const { data, isLoading, isError } = useQuery({
    queryKey: ['clientes', page],
    queryFn: () => clientesApi.getAll(page, 10),
  })

  const deleteMut = useMutation({
    mutationFn: clientesApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['clientes'] }),
  })

  return (
    <div className="max-w-6xl">

      {/* Header */}
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white">Clientes</h1>
          <p className="text-sm text-slate-500">
            {data?.totalElements ?? 0} registros totales
          </p>
        </div>
        <button
          onClick={() => setModal('create')}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold
                     text-white hover:bg-blue-500 transition-colors">
          + Nuevo cliente
        </button>
      </div>

      {/* Error */}
      {isError && (
        <div className="mb-4 rounded-lg bg-red-950/50 border border-red-900
                        px-4 py-3 text-sm text-red-400">
          Error al cargar clientes. Verifica la conexión con el backend.
        </div>
      )}

      {/* Table */}
      <div className="rounded-xl bg-slate-900 border border-slate-800 overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-slate-800">
              {['ID', 'Nombre', 'Email', 'Teléfono', 'Mascotas', 'Estado', 'Acciones'].map(h => (
                <th key={h}
                  className="px-4 py-3 text-left text-xs font-semibold
                             uppercase tracking-wider text-slate-500">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {isLoading
              ? Array.from({ length: 5 }).map((_, i) => (
                  <tr key={i} className="border-b border-slate-800/50 animate-pulse">
                    {Array.from({ length: 7 }).map((_, j) => (
                      <td key={j} className="px-4 py-3">
                        <div className="h-3 rounded bg-slate-800" />
                      </td>
                    ))}
                  </tr>
                ))
              : data?.content.map(c => (
                  <tr key={c.id}
                    className="border-b border-slate-800/50 hover:bg-slate-800/40
                               transition-colors">
                    <td className="px-4 py-3 text-slate-500 font-mono text-xs">
                      #{c.id}
                    </td>
                    <td className="px-4 py-3 font-medium text-white">
                      {c.nombre} {c.apellido}
                    </td>
                    <td className="px-4 py-3 text-slate-400">{c.email}</td>
                    <td className="px-4 py-3 text-slate-400">{c.telefono}</td>
                    <td className="px-4 py-3">
                      <span className="rounded-full bg-slate-800 px-2 py-0.5
                                       text-xs text-slate-400">
                        {c.mascotas}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`rounded-full px-2 py-0.5 text-xs font-medium
                        ${c.activo
                          ? 'bg-emerald-950/60 text-emerald-400 border border-emerald-900'
                          : 'bg-slate-800 text-slate-500 border border-slate-700'}`}>
                        {c.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button
                          onClick={() => setModal(c)}
                          className="rounded px-2 py-1 text-xs text-blue-400
                                     hover:bg-blue-950/50 transition-colors">
                          Editar
                        </button>
                        <button
                          onClick={() => {
                            if (confirm(`¿Eliminar a ${c.nombre}?`))
                              deleteMut.mutate(c.id)
                          }}
                          className="rounded px-2 py-1 text-xs text-red-400
                                     hover:bg-red-950/50 transition-colors">
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
            }
          </tbody>
        </table>

        {/* Pagination */}
        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-slate-800 px-4 py-3">
            <p className="text-xs text-slate-500">
              Página {data.number + 1} de {data.totalPages}
            </p>
            <div className="flex gap-2">
              <button
                disabled={data.first}
                onClick={() => setPage(p => p - 1)}
                className="rounded px-3 py-1 text-xs text-slate-400
                           border border-slate-700 disabled:opacity-40
                           hover:text-white transition-colors">
                ← Anterior
              </button>
              <button
                disabled={data.last}
                onClick={() => setPage(p => p + 1)}
                className="rounded px-3 py-1 text-xs text-slate-400
                           border border-slate-700 disabled:opacity-40
                           hover:text-white transition-colors">
                Siguiente →
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Modal */}
      {modal && (
        <ClienteForm
          initial={modal === 'create' ? undefined : modal}
          onClose={() => setModal(null)}
        />
      )}
    </div>
  )
}
