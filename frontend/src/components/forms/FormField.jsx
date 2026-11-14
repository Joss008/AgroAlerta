import { CircleAlert } from 'lucide-react';

export default function FormField({ label, htmlFor, required, error, hint, children }) {
  return (
    <div>
      <label
        htmlFor={htmlFor}
        className="mb-1.5 block text-sm font-medium text-stone-700"
      >
        {label}
        {required ? (
          <span className="ml-1 text-rose-500" aria-hidden="true">
            *
          </span>
        ) : null}
      </label>
      {children}
      {error ? (
        <p
          className="mt-1.5 flex items-center gap-1 text-xs font-medium text-rose-600"
          role="alert"
        >
          <CircleAlert size={12} className="shrink-0" aria-hidden="true" />
          {error}
        </p>
      ) : hint ? (
        <p className="mt-1.5 text-xs text-stone-500">{hint}</p>
      ) : null}
    </div>
  );
}
