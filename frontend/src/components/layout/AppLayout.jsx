import { useEffect, useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Menu } from 'lucide-react';
import Sidebar from './Sidebar';
import GradualBlur from '../motion/GradualBlur';

export default function AppLayout() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!menuOpen) return undefined;
    const overflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    const handleKeyDown = (event) => {
      if (event.key === 'Escape') setMenuOpen(false);
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.body.style.overflow = overflow;
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [menuOpen]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-stone-50">
      <Sidebar />
      <Sidebar mobile open={menuOpen} onClose={() => setMenuOpen(false)} />

      <div className="lg:pl-64">
        <header className="sticky top-0 z-30 flex items-center gap-3 border-b border-stone-200 bg-white/95 px-4 py-3 backdrop-blur sm:px-6 lg:hidden">
          <button
            type="button"
            onClick={() => setMenuOpen(true)}
            aria-label="Abrir menú de navegación"
            aria-expanded={menuOpen}
            className="rounded-lg border border-stone-200 p-2 text-stone-600 transition hover:bg-stone-50"
          >
            <Menu size={18} />
          </button>
          <span className="text-sm font-semibold tracking-tight text-stone-900">
            AgroAlerta <span className="text-emerald-700">Cañete</span>
          </span>
        </header>

        <main className="mx-auto w-full max-w-[1560px] px-4 py-6 sm:px-6 lg:px-8 lg:py-8">
          <Outlet />
        </main>

        <GradualBlur
          target="page"
          position="bottom"
          height="3.5rem"
          layers={6}
          strength={1.4}
          tint="rgba(250, 250, 249, 0.78)"
          autoHide
          zIndex={20}
          className="left-0 lg:left-64"
          style={{ left: undefined }}
        />
      </div>
    </div>
  );
}
