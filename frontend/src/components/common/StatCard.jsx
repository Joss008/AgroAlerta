export default function StatCard({ label, value, hint, icon: Icon, tone = 'default', className = '' }) {
  const valueTone =
    tone === 'positive'
      ? 'text-emerald-700'
      : tone === 'negative'
        ? 'text-rose-600'
        : 'text-stone-900';

  return (
    <div
      className={`flex min-w-0 flex-col justify-between gap-3 rounded-xl border border-stone-200/90 bg-white px-4 py-4 shadow-[0_1px_2px_rgba(24,24,27,0.04)] ${className}`}
    >
      <div className="flex items-start justify-between gap-2">
        <p className="text-[13px] font-medium leading-snug text-stone-500">{label}</p>
        {Icon ? (
          <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-emerald-50 text-emerald-700 ring-1 ring-emerald-600/10">
            <Icon size={16} aria-hidden="true" />
          </span>
        ) : null}
      </div>
      <div className="min-w-0">
        <p className={`truncate text-xl font-bold tracking-tight tabular-nums sm:text-2xl ${valueTone}`}>
          {value}
        </p>
        {hint ? <p className="mt-1 truncate text-xs text-stone-500">{hint}</p> : null}
      </div>
    </div>
  );
}
