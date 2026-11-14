export const inputClasses =
  'block w-full rounded-lg border border-stone-300 bg-white px-3 py-2 text-sm text-stone-900 placeholder:text-stone-500 transition focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-600/15 disabled:cursor-not-allowed disabled:bg-stone-100 disabled:text-stone-500';

export const invalidClasses = 'border-rose-400 focus:border-rose-500 focus:ring-rose-500/15';

export function fieldClasses({ invalid = false, className = '' } = {}) {
  return `${inputClasses} ${invalid ? invalidClasses : ''} ${className}`.trim();
}

export function Input({ invalid = false, className = '', ...props }) {
  return <input aria-invalid={invalid || undefined} className={fieldClasses({ invalid, className })} {...props} />;
}

export function Textarea({ invalid = false, className = '', rows = 3, ...props }) {
  return (
    <textarea
      rows={rows}
      aria-invalid={invalid || undefined}
      className={fieldClasses({ invalid, className: `${className} resize-y` })}
      {...props}
    />
  );
}

export function Select({ invalid = false, className = '', children, ...props }) {
  return (
    <div className="relative">
      <select
        aria-invalid={invalid || undefined}
        className={fieldClasses({
          invalid,
          className: `${className} appearance-none pr-9`,
        })}
        {...props}
      >
        {children}
      </select>
      <svg
        className="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-stone-400"
        viewBox="0 0 20 20"
        fill="currentColor"
        aria-hidden="true"
      >
        <path
          fillRule="evenodd"
          d="M5.23 7.21a.75.75 0 0 1 1.06.02L10 11.17l3.71-3.94a.75.75 0 1 1 1.08 1.04l-4.25 4.5a.75.75 0 0 1-1.08 0l-4.25-4.5a.75.75 0 0 1 .02-1.06Z"
          clipRule="evenodd"
        />
      </svg>
    </div>
  );
}
