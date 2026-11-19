import { useEffect, useLayoutEffect, useRef } from 'react';
import './StatusMark.css';

const useIsomorphicLayoutEffect =
  typeof window === 'undefined' ? useEffect : useLayoutEffect;

const CHECK = 'M7.5 12.25 10.5 15.25 16.75 8.75';
const CROSS = 'M8.5 8.5 15.5 15.5M15.5 8.5 8.5 15.5';
const TEXT = {
  pending: 'Pendiente',
  running: 'En curso',
  done: 'Completado',
  failed: 'Error',
  cancelled: 'Cancelado',
};
const IDLE_DASH = 0.3;

const clamp01 = (v) => Math.min(1, Math.max(0, v));

const easeInOutCubic = (t) =>
  t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;

const easeOutCubic = (t) => 1 - Math.pow(1 - t, 3);

/**
 * Indicador de estado con morphing SVG en el mismo lugar (sin dependencia de `motion`):
 * - pending / cancelled: anillo segmentado en guiones
 * - running: fusión suave a arco de progreso (determinado o indeterminado giratorio)
 * - done / failed: cierre completo sin saltos + dibujo de check o cruz + tachado opcional
 */
export default function StatusMark({
  status = 'pending',
  progress,
  label,
  color = 'currentColor',
  doneColor = '#059669',
  errorColor = '#e11d48',
  size = 20,
  strokeWidth = 2,
  dashes = 8,
  fontSize = 14,
  spinDuration = 1100,
  arcLength = 0.68,
  drawDuration = 240,
  fillOpacity = 0.08,
  strike = true,
  strikeDelay = 60,
  className = '',
  style,
}) {
  const r = 10 - strokeWidth / 2;
  const C = 2 * Math.PI * r;
  const P = C / Math.max(1, dashes);
  const determinate = status === 'running' && Number.isFinite(progress);
  const indeterminate = status === 'running' && !determinate;
  const solid = status === 'running' || status === 'done' || status === 'failed';
  const targetArc = indeterminate
    ? arcLength
    : determinate
      ? clamp01(progress)
      : 1;

  const ringRef = useRef(null);
  const animRef = useRef({
    mode: solid ? 1 : 0,
    arc: targetArc,
    travel: 0,
    modeFrom: solid ? 1 : 0,
    modeTo: solid ? 1 : 0,
    modeStart: 0,
    arcFrom: targetArc,
    arcTo: targetArc,
    arcStart: 0,
    travelFrom: 0,
    travelTo: 0,
    travelStart: 0,
    travelSettling: false,
    indeterminate,
    C,
    P,
    spinDuration,
  });

  const writeRing = (state) => {
    const ring = ringRef.current;
    if (!ring) return;
    const dash =
      IDLE_DASH * state.P + (state.arc * state.C - IDLE_DASH * state.P) * state.mode;
    const gap =
      (1 - IDLE_DASH) * state.P +
      ((1 - state.arc) * state.C - (1 - IDLE_DASH) * state.P) * state.mode;
    ring.setAttribute(
      'stroke-dasharray',
      `${Math.max(0, dash)} ${Math.max(0, gap)}`,
    );
    ring.setAttribute('stroke-dashoffset', String(state.travel));
  };

  useIsomorphicLayoutEffect(() => {
    animRef.current.C = C;
    animRef.current.P = P;
    writeRing(animRef.current);
  }, [C, P]);

  useEffect(() => {
    const state = animRef.current;
    const reduce =
      window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? false;

    state.C = C;
    state.P = P;
    state.spinDuration = Math.max(200, spinDuration);
    state.indeterminate = indeterminate;

    const targetMode = solid ? 1 : 0;

    if (reduce) {
      state.mode = targetMode;
      state.arc = targetArc;
      state.travel = 0;
      state.travelSettling = false;
      writeRing(state);
      return undefined;
    }

    const now = performance.now();
    if (state.mode === 0) state.arc = targetArc;

    state.modeFrom = state.mode;
    state.modeTo = targetMode;
    state.modeStart = now;

    state.arcFrom = state.arc;
    state.arcTo = targetArc;
    state.arcStart = now;

    if (indeterminate) {
      state.travelSettling = false;
      state.travelStart = now;
      state.travelFrom = state.travel;
    } else {
      const unit = determinate ? C : P;
      const to = Math.floor(state.travel / unit) * unit;
      state.travelFrom = state.travel;
      state.travelTo = to;
      state.travelStart = now;
      state.travelSettling = Math.abs(state.travel - to) > 0.01;
    }

    let raf = 0;
    let last = now;
    const MORPH_MS = 300;
    const UI_MS = 280;

    const tick = (frameNow) => {
      const dt = Math.min(0.05, (frameNow - last) / 1000 || 0);
      last = frameNow;

      let active = false;

      if (state.mode !== state.modeTo) {
        const t = clamp01((frameNow - state.modeStart) / MORPH_MS);
        state.mode =
          state.modeFrom +
          (state.modeTo - state.modeFrom) * easeInOutCubic(t);
        if (t >= 1) state.mode = state.modeTo;
        else active = true;
      }

      if (state.arc !== state.arcTo) {
        const t = clamp01((frameNow - state.arcStart) / UI_MS);
        state.arc =
          state.arcFrom + (state.arcTo - state.arcFrom) * easeOutCubic(t);
        if (t >= 1) state.arc = state.arcTo;
        else active = true;
      }

      if (state.indeterminate) {
        state.travel -= (state.C * (dt * 1000)) / state.spinDuration;
        if (state.travel <= -state.C * 100) state.travel %= state.C;
        active = true;
      } else if (state.travelSettling) {
        const t = clamp01((frameNow - state.travelStart) / UI_MS);
        state.travel =
          state.travelFrom +
          (state.travelTo - state.travelFrom) * easeOutCubic(t);
        if (t >= 1) {
          state.travel = 0;
          state.travelSettling = false;
        } else {
          active = true;
        }
      }

      writeRing(state);

      if (active) raf = requestAnimationFrame(tick);
    };

    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [status, solid, determinate, indeterminate, targetArc, C, P, spinDuration]);

  const spoken =
    TEXT[status] +
    (determinate ? `, ${Math.round(clamp01(progress) * 100)}%` : '');
  const hasLabel = label !== undefined && label !== null;

  return (
    <span
      className={`status-mark${className ? ` ${className}` : ''}`}
      data-status={status}
      data-indeterminate={indeterminate ? '' : undefined}
      data-strike={strike ? '' : undefined}
      style={{
        '--sm-size': `${size}px`,
        '--sm-stroke': strokeWidth,
        '--sm-color': color,
        '--sm-done': doneColor,
        '--sm-error': errorColor,
        '--sm-fill': fillOpacity,
        '--sm-font': `${fontSize}px`,
        '--sm-draw': `${drawDuration}ms`,
        '--sm-strike-delay': `${120 + strikeDelay}ms`,
        ...style,
      }}
    >
      <svg
        className="status-mark__glyph"
        viewBox="0 0 24 24"
        width={size}
        height={size}
        role={hasLabel ? undefined : 'img'}
        aria-label={hasLabel ? undefined : spoken}
        aria-hidden={hasLabel || undefined}
      >
        <circle
          className="status-mark__track"
          cx="12"
          cy="12"
          r={r}
          transform="rotate(-90 12 12)"
        />
        <circle
          ref={ringRef}
          className="status-mark__ring"
          cx="12"
          cy="12"
          r={r}
          transform="rotate(-90 12 12)"
        />
        <path className="status-mark__check" d={CHECK} pathLength="1" />
        <path className="status-mark__cross" d={CROSS} pathLength="1" />
      </svg>
      {hasLabel ? <span className="status-mark__sr">{spoken}: </span> : null}
      {hasLabel ? (
        <span className="status-mark__label">
          {label}
          <span className="status-mark__strike" aria-hidden="true" />
        </span>
      ) : null}
    </span>
  );
}
