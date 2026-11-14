import { RefreshCw, TriangleAlert } from 'lucide-react';

export default function ErrorAlert({ message, onRetry, title, className = '' }) {
  return (
    <div
      role="alert"
      className={`flex flex-col gap-3 rounded-lg border border-rose-200 bg-rose-50 px-4 py-3 sm:flex-row sm:items-center sm:justify-between ${className}`}
    >
      <div className="flex items-start gap-3">
        <TriangleAlert size={18} className="mt-0.5 shrink-0 text-rose-600" aria-hidden="true" />
        <div className="min-w-0">
          {title ? <p className="text-sm font-semibold text-rose-900">{title}</p> : null}
          <p className="text-sm leading-snug text-rose-800">{message}</p>
        </div>
      </div>
      {onRetry ? (
        <button
          type="button"
          onClick={onRetry}
          className="inline-flex shrink-0 items-center justify-center gap-1.5 self-start rounded-lg border border-rose-300 bg-white px-3 py-1.5 text-sm font-medium text-rose-700 transition hover:bg-rose-100 sm:self-auto"
        >
          <RefreshCw size={14} aria-hidden="true" />
          Reintentar
        </button>
      ) : null}
    </div>
  );
}
