import { NavLink } from 'react-router-dom';
import { Leaf, X } from 'lucide-react';
import { NAV_ITEMS } from '../../routes/navigation';

function Brand({ onNavigate }) {
  return (
    <NavLink
      to="/dashboard"
      onClick={onNavigate}
      className="flex items-center gap-3"
      aria-label="Ir al dashboard de AgroAlerta Cañete"
    >
      <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-emerald-500/20 text-emerald-300 ring-1 ring-emerald-400/30">
        <Leaf size={18} aria-hidden="true" />
      </span>
      <span className="min-w-0 leading-tight">
        <span className="block truncate text-[15px] font-bold tracking-tight text-white">
          AgroAlerta
        </span>
        <span className="block text-[11px] font-semibold tracking-wide text-emerald-400 uppercase">
          Cañete
        </span>
      </span>
    </NavLink>
  );
}

export default function Sidebar({ mobile = false, open = false, onClose }) {
  const nav = (
    <nav aria-label="Navegación principal" className="flex-1 px-3 py-4">
      <ul className="space-y-1">
        {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
          <li key={to}>
            <NavLink
              to={to}
              onClick={onClose}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition ${
                  isActive
                    ? 'bg-emerald-500/15 text-white ring-1 ring-emerald-400/25'
                    : 'text-emerald-100/75 hover:bg-white/[0.06] hover:text-white'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <Icon
                    size={18}
                    className={isActive ? 'text-emerald-300' : 'text-emerald-200/50'}
                    aria-hidden="true"
                  />
                  {label}
                </>
              )}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );

  const footer = (
    <div className="border-t border-emerald-900/80 px-6 py-4">
      <p className="text-xs leading-relaxed text-emerald-200/55">
        Control agrícola · Cañete, Perú
      </p>
    </div>
  );

  if (mobile) {
    return (
      <>
        <div
          className={`fixed inset-0 z-40 bg-stone-950/50 backdrop-blur-[2px] transition-opacity duration-150 lg:hidden ${
            open ? 'opacity-100' : 'pointer-events-none opacity-0'
          }`}
          onClick={onClose}
          aria-hidden="true"
        />
        <aside
          className={`fixed inset-y-0 left-0 z-50 flex w-72 max-w-[85vw] flex-col border-r border-emerald-900/80 bg-emerald-950 transition-transform duration-200 ease-out lg:hidden ${
            open ? 'translate-x-0' : '-translate-x-full'
          }`}
          aria-hidden={!open}
        >
          <div className="flex items-center justify-between border-b border-emerald-900/80 px-5 py-4">
            <Brand onNavigate={onClose} />
            <button
              type="button"
              onClick={onClose}
              aria-label="Cerrar menú"
              className="rounded-lg p-1.5 text-emerald-200/70 transition hover:bg-white/10 hover:text-white"
            >
              <X size={20} />
            </button>
          </div>
          {nav}
          {footer}
        </aside>
      </>
    );
  }

  return (
    <aside className="fixed inset-y-0 left-0 z-30 hidden w-64 flex-col border-r border-emerald-900/80 bg-emerald-950 lg:flex">
      <div className="border-b border-emerald-900/80 px-5 py-5">
        <Brand />
      </div>
      {nav}
      {footer}
    </aside>
  );
}
