export default function EmptyState({
  icon: Icon,
  title,
  description,
  action,
  className = '',
  compact = false,
}) {
  return (
    <div
      className={`flex flex-col items-center justify-center rounded-xl border border-dashed border-stone-300 bg-white px-6 text-center ${
        compact ? 'py-8' : 'py-14'
      } ${className}`}
    >
      {Icon ? (
        <span className="mb-3 flex h-11 w-11 items-center justify-center rounded-full bg-stone-100 text-stone-500">
          <Icon size={20} aria-hidden="true" />
        </span>
      ) : null}
      <h3 className="text-sm font-semibold text-stone-800">{title}</h3>
      {description ? (
        <p className="mt-1 max-w-sm text-sm leading-relaxed text-stone-500">{description}</p>
      ) : null}
      {action ? <div className="mt-4">{action}</div> : null}
    </div>
  );
}
