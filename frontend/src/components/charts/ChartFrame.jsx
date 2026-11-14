import ErrorAlert from '../common/ErrorAlert';
import EmptyState from '../common/EmptyState';
import Loading from '../common/Loading';
import { getApiMessage } from '../../utils/errors';

export default function ChartFrame({
  title,
  description,
  legend,
  loading = false,
  error = null,
  onRetry,
  empty = false,
  emptyTitle = 'Sin datos',
  emptyDescription = 'Todavía no hay información disponible para mostrar.',
  className = '',
  bodyClassName = '',
  children,
}) {
  let body;

  if (loading) {
    body = <Loading compact />;
  } else if (error && !empty) {
    body = (
      <div className="p-4">
        <ErrorAlert message={getApiMessage(error)} onRetry={onRetry} />
      </div>
    );
  } else if (empty) {
    body = (
      <div className="p-4">
        <EmptyState compact title={emptyTitle} description={emptyDescription} />
      </div>
    );
  } else {
    body = children;
  }

  return (
    <section
      className={`flex flex-col rounded-xl border border-stone-200 bg-white ${className}`}
    >
      <div className="flex flex-wrap items-start justify-between gap-x-4 gap-y-2 border-b border-stone-100 px-5 py-4">
        <div className="min-w-0">
          <h2 className="text-sm font-semibold text-stone-800">{title}</h2>
          {description ? (
            <p className="mt-0.5 text-xs text-stone-500">{description}</p>
          ) : null}
        </div>
        {legend?.length ? (
          <ul className="flex flex-wrap items-center gap-x-3 gap-y-1">
            {legend.map((item) => (
              <li key={item.name} className="flex items-center gap-1.5 text-xs text-stone-500">
                <span
                  className="h-2.5 w-2.5 shrink-0 rounded-full"
                  style={{ backgroundColor: item.color }}
                  aria-hidden="true"
                />
                {item.name}
              </li>
            ))}
          </ul>
        ) : null}
      </div>
      <div className={`min-w-0 flex-1 px-2 py-4 ${bodyClassName}`}>{body}</div>
    </section>
  );
}
