const DASH = '—';

const formatters = new Map();

function getFormatter(minFraction, maxFraction) {
  const key = `${minFraction}:${maxFraction}`;
  if (!formatters.has(key)) {
    formatters.set(
      key,
      new Intl.NumberFormat('en-US', {
        minimumFractionDigits: minFraction,
        maximumFractionDigits: maxFraction,
      }),
    );
  }
  return formatters.get(key);
}

function toNumber(value) {
  if (value == null || value === '') return null;
  const n = Number(value);
  return Number.isNaN(n) ? null : n;
}

export function formatNumber(value, { min = 0, max = 2 } = {}) {
  const n = toNumber(value);
  if (n == null) return DASH;
  return getFormatter(min, max).format(n);
}

export function formatInteger(value) {
  const n = toNumber(value);
  if (n == null) return DASH;
  return getFormatter(0, 0).format(Math.round(n));
}

export function formatSoles(value) {
  const n = toNumber(value);
  if (n == null) return DASH;
  const abs = getFormatter(2, 2).format(Math.abs(n));
  return n < 0 ? `S/ -${abs}` : `S/ ${abs}`;
}

export function formatQuantity(value, unit, { min = 0, max = 2 } = {}) {
  const n = toNumber(value);
  if (n == null) return DASH;
  return `${getFormatter(min, max).format(n)} ${unit}`;
}

export const formatKg = (value) => formatQuantity(value, 'kg');
export const formatHectares = (value) => formatQuantity(value, 'ha');
export const formatM3 = (value) => formatQuantity(value, 'm³');
export const formatRendimiento = (value) => formatQuantity(value, 'kg/ha', { min: 0, max: 1 });

export function formatDate(value) {
  if (!value) return DASH;
  const match = String(value).match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (!match) return String(value);
  return `${match[3]}/${match[2]}/${match[1]}`;
}

export function formatList(value) {
  if (value == null || value === '') return DASH;
  return String(value);
}

export function truncate(value, maxLength = 40) {
  if (value == null || value === '') return DASH;
  const text = String(value);
  return text.length > maxLength ? `${text.slice(0, maxLength - 1)}…` : text;
}

export function todayISO() {
  const now = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`;
}

export { DASH };
