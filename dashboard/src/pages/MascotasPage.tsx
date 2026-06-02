import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { mascotasApi, clientesApi } from '../api/services'
import type { MascotaResponse, MascotaRequest, EspecieMascota } from '../types'

const ESPECIES: EspecieMascota[] = ['PERRO', 'GATO', 'AVE', 'OTRO']

const ESPECIE_EMOJI: Record<EspecieMascota, string> = {
  PERRO: '🐶', GATO: '🐱', AVE: '🐦', OTRO: '🐾',
}

// ── Form modal ────────────────────────────────────────────
interface FormProps {
  initial?: MascotaResponse
  onClose: () => void
}

function MascotaForm({ initial, onClose }: FormProps) {
  const qc = useQueryClient()
  const isEdit = !!initial

  const [form, setForm] = useState<MascotaRequest>({
    nombre:    initial?.nombre            ?? '',
    especie:   initial?.especie           ?? 'PERRO',
    raza:      initial?.raza              ?? '',
    edad:      initial?.edad              ?? 0,
    peso:      initial?.peso              ?? 0,
    clienteId: initial?.clienteId         ?? 0,
  })
  const [errors, setErrors] = useState<Record<string, string>>({})

  // Load clients for select
  const { data: clientesData } = useQuery({
    queryKey: ['clientes-select'],
    queryFn: () => clientesApi.getAll(0, 100),
  })

  const validate = (): boolean => {
    const e: Record<string, string> = {}
    if (!form.nombre.trim())        e.nombre    = 'Requerido'
    if (!form.raza.trim())          e.raza      = 'Requerido'
    if (form.edad < 0)              e.edad      = 'Inválido'
    if (form.peso <= 0)             e.peso      = 'Debe ser mayor a 0'
    if (!form.clienteId)            e.clienteId = 'Selecciona un cliente'
    setErrors(e)
    return Object.keys(e).length === 0
  }

  const createMut = useMutation({
    mutationFn: mascotasApi.create,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['mascotas'] }); onClose() },
  })
  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: MascotaRequest }) =>
      mascotasApi.update(id, data),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['mascotas'] }); onClose() },
  })

  const isPending = createMut.isPending || updateMut.isPending

  const handleSubmit = () => {
    if (!validate()) return
    if (isEdit && initial) updateMut.mutate({ id: initial.id, data: form })
    else                   createMut.mutate(form)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center
                    bg-black/60 backdrop-blur-sm px-4">
      <div className="w-full max-w-md rounded-2xl bg-slate-900 border border-slate-800 shadow-2xl">
        <div className="flex items-center justify-between border-b border-slate-800 px-6 py-4">
          <h2 className="font-semibold text-white">
            {isEdit ? 'Editar mascota' : 'Nueva mascota'}
          </h2>
          <button onClick={onClose} className="text-slate-500 hover:text-white">✕</button>
        </div>

        <div className="grid grid-cols-2 gap-4 p-6">

          {/* Nombre */}
          <div className="col-span-2">
            <label className="mb-1 block text-xs font-medium text-slate-400">Nombre</label>
            <input
              value={form.nombre}
              onChange={e => setForm(f => ({ ...f, nombre: e.target.value }))}
              placeholder="Firulais"
              className={`w-full rounded-lg bg-slate-800 border px-3 py-2 text-sm
                          text-white placeholder-slate-600 focus:outline-none
                          focus:ring-1 focus:ring-blue-500
                          ${errors.nombre ? 'border-red-500' : 'border-slate-700'}`}
            />
            {errors.nombre && <p className="mt-1 text-xs text-red-400">{errors.nombre}</p>}
          </div>

          {/* Especie */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-400">Especie</label>
            <select
              value={form.especie}
              onChange={e => setForm(f => ({ ...f, especie: e.target.value as EspecieMascota }))}
              className="w-full rounded-lg bg-slate-800 border border-slate-700 px-3 py-2
                         text-sm text-white focus:outline-none focus:ring-1 focus:ring-blue-500"
            >
              {ESPECIES.map(e => (
                <option key={e} value={e}>
                  {ESPECIE_EMOJI[e]} {e}
                </option>
              ))}
            </select>
          </div>

          {/* Raza */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-400">Raza</label>
            <input
              value={form.raza}
              onChange={e => setForm(f => ({ ...f, raza: e.target.value }))}
              placeholder="Labrador"
              className={`w-full rounded-lg bg-slate-800 border px-3 py-2 text-sm
                          text-white placeholder-slate-600 focus:outline-none
                          focus:ring-1 focus:ring-blue-500
                          ${errors.raza ? 'border-red-500' : 'border-slate-700'}`}
            />
          </div>

          {/* Edad */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-400">Edad (años)</label>
            <input
              type="number" min={0}
              value={form.edad}
              onChange={e => setForm(f => ({ ...f, edad: +e.target.value }))}
              className={`w-full rounded-lg bg-slate-800 border px-3 py-2 text-sm
                          text-white focus:outline-none focus:ring-1 focus:ring-blue-500
                          ${errors.edad ? 'border-red-500' : 'border-slate-700'}`}
            />
          </div>

          {/* Peso */}
          <div>
            <label className="mb-1 block text-xs font-medium text-slate-400">Peso (kg)</label>
            <input
              type="number" min={0} step={0.1}
              value={form.peso}
              onChange={e => setForm(f => ({ ...f, peso: +e.target.value }))}
              className={`w-full rounded-lg bg-slate-800 border px-3 py-2 text-sm
                          text-white focus:outline-none focus:ring-1 focus:ring-blue-500
                          ${errors.peso ? 'border-red-500' : 'border-slate-700'}`}
            />
            {errors.peso && <p className="mt-1 text-xs text-red-400">{errors.peso}</p>}
          </div>

          {/* Cliente */}
          <div className="col-span-2">
            <label className="mb-1 block text-xs font-medium text-slate-400">Propietario</label>
            <select
              value={form.clienteId}
              onChange={e => setForm(f => ({ ...f, clienteId: +e.target.value }))}
              className={`w-full rounded-lg bg-slate-800 border px-3 py-2 text-sm
                          text-white focus:outline-none focus:ring-1 focus:ring-blue-500
                          ${errors.clienteId ? 'border-red-500' : 'border-slate-700'}`}
            >
              <option value={0}>Seleccionar cliente...</option>
              {clientesData?.content.map(c => (
                <option key={c.id} value={c.id}>
                  {c.nombre} {c.apellido}
                </option>
              ))}
            </select>
            {errors.clienteId && (
              <p className="mt-1 text-xs text-red-400">{errors.clienteId}</p>
            )}
          </div>
        </div>

        <div className="flex gap-3 border-t border-slate-800 px-6 py-4">
          <button onClick={onClose}
            className="flex-1 rounded-lg border border-slate-700 py-2 text-sm
                       text-slate-400 hover:text-white transition-colors">
            Cancelar
          </button>
          <button onClick={handleSubmit} disabled={isPending}
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
export default function MascotasPage() {
  const qc = useQueryClient()
  const [page, setPage]   = useState(0)
  const [modal, setModal] = useState<'create' | MascotaResponse | null>(null)

  const { data, isLoading, isError } = useQuery({
    queryKey: ['mascotas', page],
    queryFn: () => mascotasApi.getAll(page, 10),
  })

  const deleteMut = useMutation({
    mutationFn: mascotasApi.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['mascotas'] }),
  })

  return (
    <div className="max-w-6xl">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-white">Mascotas</h1>
          <p className="text-sm text-slate-500">
            {data?.totalElements ?? 0} registros totales
          </p>
        </div>
        <button
          onClick={() => setModal('create')}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold
                     text-white hover:bg-blue-500 transition-colors">
          + Nueva mascota
        </button>
      </div>

      {isError && (
        <div className="mb-4 rounded-lg bg-red-950/50 border border-red-900
                        px-4 py-3 text-sm text-red-400">
          Error al cargar mascotas.
        </div>
      )}

      <div className="rounded-xl bg-slate-900 border border-slate-800 overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-slate-800">
              {['ID', 'Nombre', 'Especie', 'Raza', 'Edad', 'Peso', 'Propietario', 'Acciones'].map(h => (
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
                    {Array.from({ length: 8 }).map((_, j) => (
                      <td key={j} className="px-4 py-3">
                        <div className="h-3 rounded bg-slate-800" />
                      </td>
                    ))}
                  </tr>
                ))
              : data?.content.map(m => (
                  <tr key={m.id}
                    className="border-b border-slate-800/50 hover:bg-slate-800/40 transition-colors">
                    <td className="px-4 py-3 text-slate-500 font-mono text-xs">#{m.id}</td>
                    <td className="px-4 py-3 font-medium text-white">{m.nombre}</td>
                    <td className="px-4 py-3">
                      <span className="flex items-center gap-1.5 text-slate-400">
                        {ESPECIE_EMOJI[m.especie]} {m.especie}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-400">{m.raza}</td>
                    <td className="px-4 py-3 text-slate-400">{m.edad} años</td>
                    <td className="px-4 py-3 text-slate-400">{m.peso} kg</td>
                    <td className="px-4 py-3 text-slate-400">{m.clienteNombre}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button
                          onClick={() => setModal(m)}
                          className="rounded px-2 py-1 text-xs text-blue-400
                                     hover:bg-blue-950/50 transition-colors">
                          Editar
                        </button>
                        <button
                          onClick={() => {
                            if (confirm(`¿Eliminar a ${m.nombre}?`))
                              deleteMut.mutate(m.id)
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

        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-slate-800 px-4 py-3">
            <p className="text-xs text-slate-500">
              Página {data.number + 1} de {data.totalPages}
            </p>
            <div className="flex gap-2">
              <button disabled={data.first} onClick={() => setPage(p => p - 1)}
                className="rounded px-3 py-1 text-xs text-slate-400 border border-slate-700
                           disabled:opacity-40 hover:text-white transition-colors">
                ← Anterior
              </button>
              <button disabled={data.last} onClick={() => setPage(p => p + 1)}
                className="rounded px-3 py-1 text-xs text-slate-400 border border-slate-700
                           disabled:opacity-40 hover:text-white transition-colors">
                Siguiente →
              </button>
            </div>
          </div>
        )}
      </div>

      {modal && (
        <MascotaForm
          initial={modal === 'create' ? undefined : modal}
          onClose={() => setModal(null)}
        />
      )}
    </div>
  )
}
