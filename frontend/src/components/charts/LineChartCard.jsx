import {
  CartesianGrid,
  Line,
  LineChart,
  ReferenceLine,
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

function ChartTooltip({ active, payload, label, valueFormatter }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="rounded-lg border border-stone-200 bg-white px-3 py-2 shadow-lg">
      <p className="text-xs font-semibold text-stone-800">{label}</p>
      <div className="mt-1 space-y-1">
        {payload.map((entry, index) => (
          <p
            key={entry.dataKey ?? index}
            className="flex items-center gap-2 text-xs text-stone-600"
          >
            <span
              className="h-2 w-2 shrink-0 rounded-full"
              style={{ backgroundColor: entry.color || entry.stroke }}
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
  height = 300,
  valueFormatter,
  xTickFormatter,
  referenceLines = [],
  loading = false,
  error = null,
  onRetry,
  className = '',
  emptyTitle,
  emptyDescription,
}) {
  const resolvedSeries = series.length
    ? series
    : [{ dataKey: 'value', name: 'Valor', stroke: CHART_COLORS.emerald }];

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
      <div style={{ height }} className="w-full">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 8, right: 16, left: 0, bottom: 0 }}>
            <CartesianGrid vertical={false} stroke={GRID_STROKE} strokeDasharray="3 3" />
            <XAxis
              dataKey={xKey}
              tick={AXIS_TICK_STYLE}
              tickLine={false}
              axisLine={{ stroke: AXIS_LINE_STROKE }}
              tickFormatter={(value) =>
                xTickFormatter ? xTickFormatter(value) : truncateLabel(value)
              }
              interval={0}
              height={44}
            />
            <YAxis
              tick={AXIS_TICK_STYLE}
              tickLine={false}
              axisLine={false}
              width={72}
              tickFormatter={(value) =>
                valueFormatter ? valueFormatter(value) : String(value)
              }
            />
            <Tooltip
              cursor={{ stroke: '#d6d3d1', strokeDasharray: '3 3' }}
              content={<ChartTooltip valueFormatter={valueFormatter} />}
            />
            {referenceLines.map((line) => (
              <ReferenceLine
                key={`ref-${line.y}`}
                y={line.y}
                stroke={line.stroke || '#a8a29e'}
                strokeDasharray={line.strokeDasharray || '4 4'}
              />
            ))}
            {resolvedSeries.map((item) => (
              <Line
                key={item.dataKey}
                type="linear"
                dataKey={item.dataKey}
                name={item.name}
                stroke={item.stroke}
                strokeWidth={2.5}
                dot={{ r: 3.5, strokeWidth: 0, fill: item.stroke }}
                activeDot={{ r: 5, strokeWidth: 0 }}
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </ChartFrame>
  );
}
