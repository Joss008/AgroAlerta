import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts';
import ChartFrame from './ChartFrame';
import { CATEGORICAL_PALETTE } from './chartTheme';

function ChartTooltip({ active, payload, valueFormatter }) {
  if (!active || !payload?.length) return null;
  const entry = payload[0];
  return (
    <div className="rounded-lg border border-stone-200 bg-white px-3 py-2 shadow-lg">
      <p className="text-xs font-semibold text-stone-800">{entry.name}</p>
      <p className="mt-1 text-xs text-stone-600">
        <span className="font-medium text-stone-900 tabular-nums">
          {valueFormatter ? valueFormatter(entry.value) : String(entry.value ?? '—')}
        </span>
      </p>
    </div>
  );
}

export default function PieChartCard({
  title,
  description,
  data = [],
  nameKey,
  valueKey,
  valueFormatter,
  colors = CATEGORICAL_PALETTE,
  height = 260,
  loading = false,
  error = null,
  onRetry,
  className = '',
  emptyTitle,
  emptyDescription,
}) {
  const empty = !loading && !error && data.length === 0;
  const total = data.reduce((sum, item) => sum + (Number(item[valueKey]) || 0), 0);

  return (
    <ChartFrame
      title={title}
      description={description}
      loading={loading}
      error={error}
      onRetry={onRetry}
      empty={empty}
      emptyTitle={emptyTitle}
      emptyDescription={emptyDescription}
      className={className}
    >
      <div className="flex flex-col gap-4 px-3 sm:flex-row sm:items-center">
        <div
          style={{ height }}
          className="relative w-full shrink-0 sm:w-1/2"
        >
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={data}
                dataKey={valueKey}
                nameKey={nameKey}
                innerRadius="58%"
                outerRadius="92%"
                paddingAngle={2}
                stroke="#ffffff"
                strokeWidth={2}
              >
                {data.map((entry, index) => (
                  <Cell
                    key={entry[nameKey] ?? index}
                    fill={colors[index % colors.length]}
                  />
                ))}
              </Pie>
              <Tooltip content={<ChartTooltip valueFormatter={valueFormatter} />} />
            </PieChart>
          </ResponsiveContainer>
          <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
            <span className="text-[11px] uppercase tracking-wide text-stone-500">Total</span>
            <span className="text-sm font-semibold text-stone-800 tabular-nums">
              {valueFormatter ? valueFormatter(total) : String(total)}
            </span>
          </div>
        </div>
        <ul className="min-w-0 flex-1 space-y-2 px-2">
          {data.map((entry, index) => (
            <li key={entry[nameKey] ?? index} className="flex items-center gap-2 text-sm">
              <span
                className="h-2.5 w-2.5 shrink-0 rounded-full"
                style={{ backgroundColor: colors[index % colors.length] }}
                aria-hidden="true"
              />
              <span className="min-w-0 flex-1 truncate text-stone-600">
                {entry[nameKey]}
              </span>
              <span className="shrink-0 font-medium text-stone-800 tabular-nums">
                {valueFormatter ? valueFormatter(entry[valueKey]) : String(entry[valueKey])}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </ChartFrame>
  );
}
