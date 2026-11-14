import { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react';
import { CheckCircle2, Info, X, XCircle } from 'lucide-react';

const ToastContext = createContext(null);

const TYPE_STYLES = {
  success: {
    icon: CheckCircle2,
    iconClass: 'text-emerald-600',
    borderClass: 'border-emerald-200',
  },
  error: {
    icon: XCircle,
    iconClass: 'text-rose-600',
    borderClass: 'border-rose-200',
  },
  info: {
    icon: Info,
    iconClass: 'text-sky-600',
    borderClass: 'border-sky-200',
  },
};

function Toast({ toast, onDismiss }) {
  const style = TYPE_STYLES[toast.type] || TYPE_STYLES.info;
  const Icon = style.icon;

  return (
    <div
      role={toast.type === 'error' ? 'alert' : 'status'}
      className={`aa-toast-in pointer-events-auto flex w-full items-start gap-3 rounded-lg border ${style.borderClass} bg-white px-4 py-3 shadow-lg sm:w-96`}
    >
      <Icon size={18} className={`mt-0.5 shrink-0 ${style.iconClass}`} aria-hidden="true" />
      <p className="flex-1 text-sm leading-snug text-stone-700">{toast.message}</p>
      <button
        type="button"
        onClick={() => onDismiss(toast.id)}
        className="shrink-0 rounded p-0.5 text-stone-400 transition hover:text-stone-600"
        aria-label="Cerrar notificación"
      >
        <X size={14} />
      </button>
    </div>
  );
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const counterRef = useRef(0);

  const dismiss = useCallback((id) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const push = useCallback(
    (type, message) => {
      const id = ++counterRef.current;
      setToasts((current) => [...current, { id, type, message }]);
      setTimeout(() => dismiss(id), 4500);
    },
    [dismiss],
  );

  const value = useMemo(
    () => ({
      success: (message) => push('success', message),
      error: (message) => push('error', message),
      info: (message) => push('info', message),
      dismiss,
    }),
    [push, dismiss],
  );

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div
        className="pointer-events-none fixed inset-x-4 top-4 z-[100] flex flex-col items-stretch gap-2 sm:inset-x-auto sm:right-4 sm:w-96"
        aria-live="polite"
      >
        {toasts.map((toast) => (
          <Toast key={toast.id} toast={toast} onDismiss={dismiss} />
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast debe usarse dentro de ToastProvider.');
  }
  return context;
}
