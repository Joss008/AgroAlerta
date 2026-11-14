import { ChevronLeft, ChevronRight, Inbox } from 'lucide-react';
import EmptyState from '../common/EmptyState';
import ErrorAlert from '../common/ErrorAlert';
import Loading from '../common/Loading';
import { getApiMessage } from '../../utils/errors';

function defaultRowKey(row, index) {
  return row?.id ?? index;
}

function Cell({ column, row, index }) {
  const content = column.render ? column.render(row, index) : row[column.key];
  return (
    <td
      className={`px-4 py-3 align-middle text-sm ${
        column.align === 'right'
          ? 'text-right tabular-nums'
          : column.align === 'center'
            ? 'text-center'
            : 'text-stone-700'
      } ${column.className || ''}`}
    >
      {content}
    </td>
  );
}

function Pagination({ page, totalPages, totalElements, size, onPageChange }) {
  if (totalElements <= 0) return null;

  const from = page * size + 1;
  const to = Math.min((page + 1) * size, totalElements);

  return (
    <div className="flex flex-col gap-3 border-t border-stone-200 px-4 py-3 text-sm text-stone-500 sm:flex-row sm:items-center sm:justify-between">
      <p>
        Mostrando <span className="font-medium text-stone-700">{from}</span>–
        <span className="font-medium text-stone-700">{to}</span> de{' '}
        <span className="font-medium text-stone-700">{totalElements}</span> registros
      </p>
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => onPageChange(page - 1)}
          disabled={page <= 0}
          className="inline-flex items-center gap-1 rounded-lg border border-stone-300 bg-white px-3 py-1.5 text-sm font-medium text-stone-700 transition hover:bg-stone-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <ChevronLeft size={14} aria-hidden="true" />
          Anterior
        </button>
        <span className="whitespace-nowrap text-stone-600">
          Página <span className="font-medium">{Math.min(page + 1, totalPages)}</span> de{' '}
          <span className="font-medium">{Math.max(totalPages, 1)}</span>
        </span>
        <button
          type="button"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages - 1}
          className="inline-flex items-center gap-1 rounded-lg border border-stone-300 bg-white px-3 py-1.5 text-sm font-medium text-stone-700 transition hover:bg-stone-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          Siguiente
          <ChevronRight size={14} aria-hidden="true" />
        </button>
      </div>
    </div>
  );
}

export default function DataTable({
  columns,
  rows,
  rowKey = defaultRowKey,
  loading = false,
  error = null,
  onRetry,
  emptyIcon,
  emptyTitle = 'Sin registros',
  emptyDescription,
  emptyAction,
  page,
  totalPages,
  totalElements,
  size = 10,
  onPageChange,
  className = '',
}) {
  const showEmpty = !loading && !error && rows.length === 0;
  const showLoading = loading && rows.length === 0;
  const showError = Boolean(error) && rows.length === 0;

  return (
    <div
      className={`overflow-hidden rounded-xl border border-stone-200 bg-white ${className}`}
    >
      <div className={loading && rows.length > 0 ? 'opacity-60 transition-opacity' : ''}>
        {showLoading ? (
          <Loading />
        ) : showError ? (
          <div className="p-4">
            <ErrorAlert message={getApiMessage(error)} onRetry={onRetry} />
          </div>
        ) : showEmpty ? (
          <div className="p-4">
            <EmptyState
              icon={emptyIcon || Inbox}
              title={emptyTitle}
              description={emptyDescription}
              action={emptyAction}
              compact
            />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full border-collapse">
              <thead>
                <tr className="bg-stone-50">
                  {columns.map((column) => (
                    <th
                      key={column.key}
                      scope="col"
                      className={`px-4 py-3 text-xs font-semibold text-stone-500 ${
                        column.align === 'right'
                          ? 'text-right'
                          : column.align === 'center'
                            ? 'text-center'
                            : 'text-left'
                      } ${column.headerClassName || ''}`}
                    >
                      {column.header}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-stone-100">
                {rows.map((row, index) => (
                  <tr
                    key={rowKey(row, index)}
                    className="transition hover:bg-stone-50/70"
                  >
                    {columns.map((column) => (
                      <Cell key={column.key} column={column} row={row} index={index} />
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {onPageChange ? (
        <Pagination
          page={page}
          totalPages={totalPages}
          totalElements={totalElements}
          size={size}
          onPageChange={onPageChange}
        />
      ) : null}
    </div>
  );
}
