import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import ChartFrame from './ChartFrame';
import {
  AXIS_LINE_STROKE,
  AXIS_TICK_STYLE,
  CHART_COLORS,
  GRID_STROKE,
  truncateLabel,
} from './chartTheme';

function ChartTooltip({ active, payload, label, valueFormatter, labelFormatter }) {
  if (!active || !payload?.length) return null;
  const formattedLabel = labelFormatter ? labelFormatter(label) : label;
  return (
    <div className="rounded-lg border border-stone-200 bg-white px-3 py-2 shadow-lg">
      <p className="text-xs font-semibold text-stone-800">{formattedLabel}</p>
      <div className="mt-1 space-y-1">
        {payload.map((entry, index) => (
          <p
            key={entry.dataKey ?? index}
            className="flex items-center gap-2 text-xs text-stone-600"
          >
            <span
              className="h-2 w-2 shrink-0 rounded-full"
              style={{ backgroundColor: entry.color || CHART_COLORS.teal }}
              aria-hidden="true"
            />
            <span>{entry.name}</span>
            <span className="ml-auto font-medium text-stone-900 tabular-nums">
              {valueFormatter ? valueFormatter(entry.value) : String(entry.value ?? '—')}
            </span>
          </p>
        ))}
      </div>
    </div>
  );
}

export default function LineChartCard({
  title,
  description,
  legend,
  data = [],
  xKey,
  series = [],
  height = 280,
  valueFormatter,
  xTickFormatter,
  loading = false,
  error = null,
  onRetry,
  className = '',
  emptyTitle,
  emptyDescription,
}) {
  const resolvedSeries = series.length
    ? series
    : [{ dataKey: 'value', name: 'Valor', color: CHART_COLORS.teal }];

  const empty = !loading && !error && data.length === 0;

  return (
    <ChartFrame
      title={title}
      description={description}
      legend={legend}
      loading={loading}
      error={error}
      onRetry={onRetry}
      empty={empty}
      emptyTitle={emptyTitle}
      emptyDescription={emptyDescription}
      className={className}
    >
      <div style={{ height }} className="w-full px-3 pt-2">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 8, right: 16, left: 0, bottom: 4 }}>
            <CartesianGrid stroke={GRID_STROKE} strokeDasharray="3 3" vertical={false} />
            <XAxis
              dataKey={xKey}
              tick={AXIS_TICK_STYLE}
              axisLine={{ stroke: AXIS_LINE_STROKE }}
              tickLine={false}
              tickFormatter={(value) =>
                xTickFormatter ? xTickFormatter(value) : truncateLabel(value)
              }
            />
            <YAxis
              tick={AXIS_TICK_STYLE}
              axisLine={false}
              tickLine={false}
              width={64}
              tickFormatter={valueFormatter}
            />
            <Tooltip
              content={
                <ChartTooltip
                  valueFormatter={valueFormatter}
                  labelFormatter={xTickFormatter}
                />
              }
            />
            {resolvedSeries.map((item) => (
              <Line
                key={item.dataKey}
                type="monotone"
                dataKey={item.dataKey}
                name={item.name}
                stroke={item.color || CHART_COLORS.teal}
                strokeWidth={2.2}
                dot={{
                  r: 4,
                  fill: item.color || CHART_COLORS.teal,
                  stroke: '#ffffff',
                  strokeWidth: 1.5,
                }}
                activeDot={{
                  r: 6,
                  fill: item.color || CHART_COLORS.teal,
                  stroke: '#ffffff',
                  strokeWidth: 2,
                }}
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </ChartFrame>
  );
}
