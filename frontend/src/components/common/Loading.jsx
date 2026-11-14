export default function Loading({ label = 'Cargando…', className = '', compact = false }) {
  return (
    <div
      className={`flex flex-col items-center justify-center gap-3 ${compact ? 'py-6' : 'py-16'} ${className}`}
      role="status"
    >
      <span
        className="h-7 w-7 animate-spin rounded-full border-2 border-stone-200 border-t-emerald-600"
        aria-hidden="true"
      />
      <p className="text-sm text-stone-500">{label}</p>
    </div>
  );
}
