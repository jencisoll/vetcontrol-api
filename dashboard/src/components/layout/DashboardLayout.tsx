import { Outlet } from 'react-router-dom'
import Sidebar from './Sidebar'

export default function DashboardLayout() {
  return (
    <div className="flex min-h-screen bg-slate-950">
      <Sidebar />
      <main className="flex-1 pl-60 min-w-0">
        <div className="min-h-screen p-8">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
