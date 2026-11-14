export const CHART_COLORS = {
  emerald: '#059669',
  emeraldLight: '#34d399',
  amber: '#f59e0b',
  sky: '#0ea5e9',
  stone: '#78716c',
  lime: '#65a30d',
  teal: '#0d9488',
  orange: '#ea580c',
  rose: '#e11d48',
  violet: '#7c3aed',
};

export const CATEGORICAL_PALETTE = [
  CHART_COLORS.emerald,
  CHART_COLORS.amber,
  CHART_COLORS.sky,
  CHART_COLORS.stone,
  CHART_COLORS.lime,
  CHART_COLORS.teal,
  CHART_COLORS.orange,
  CHART_COLORS.rose,
];

export const AXIS_TICK_STYLE = {
  fontSize: 12,
  fill: '#787161',
  fontFamily: 'inherit',
};

export const GRID_STROKE = '#e7e5e4';
export const AXIS_LINE_STROKE = '#d6d3d1';

export function truncateLabel(value, maxLength = 14) {
  const text = String(value ?? '');
  return text.length > maxLength ? `${text.slice(0, maxLength - 1)}…` : text;
}
