import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useLayoutEffect,
  useMemo,
  useRef,
} from 'react';
import './BentoGrid.css';

const useIsomorphicLayoutEffect =
  typeof window === 'undefined' ? useEffect : useLayoutEffect;

const BentoContext = createContext(null);

const clamp = (value, min, max) => Math.min(max, Math.max(min, value));

function parseRgb(value) {
  if (!value) return null;
  const text = String(value).trim();
  const hex = /^#?([0-9a-f]{3}|[0-9a-f]{6})$/i.exec(text);
  if (hex) {
    const full =
      hex[1].length === 3
        ? hex[1].replace(/./g, (digit) => digit + digit)
        : hex[1];
    return [0, 2, 4]
      .map((index) => parseInt(full.slice(index, index + 2), 16))
      .join(', ');
  }
  const numbers = text.match(/[\d.]+/g);
  if (numbers && numbers.length >= 3 && /^(rgba?\(|[\d.\s,]+$)/i.test(text)) {
    return numbers
      .slice(0, 3)
      .map((channel) => clamp(Math.round(parseFloat(channel)), 0, 255))
      .join(', ');
  }
  return null;
}

const TILT_MAX = 4.5;
const MAGNET_MAX = 4;

/**
 * Grilla interactiva inspirada en MagicBento:
 * - Spotlight interno suave que sigue al cursor
 * - Iluminación de borde compartida entre tarjetas vecinas
 * - Onda expansiva al hacer clic
 * - Entrada escalonada con curva spring
 */
export function BentoGrid({
  children,
  className = '',
  style,
  glowColor = '5, 150, 105',
  spotlightRadius = 260,
  enableBorderGlow = true,
  enableSpotlight = true,
  enableTilt = false,
  enableMagnetism = false,
  clickEffect = true,
  intro = true,
  staggerMs = 55,
  disableAnimations = false,
}) {
  const rootRef = useRef(null);
  const itemsRef = useRef(new Map());
  const orderRef = useRef(0);
  const engineRef = useRef(null);

  const settingsRef = useRef({});
  settingsRef.current = {
    spotlightRadius: Math.max(60, spotlightRadius),
    enableBorderGlow,
    enableSpotlight,
    enableTilt,
    enableMagnetism,
    clickEffect,
    intro,
    staggerMs,
    disableAnimations,
  };

  useIsomorphicLayoutEffect(() => {
    const root = rootRef.current;
    if (!root) return undefined;

    const reduced =
      window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? false;
    const motion = !settingsRef.current.disableAnimations && !reduced;
    const playIntro = motion && settingsRef.current.intro;

    if (playIntro) root.dataset.intro = '';
    else delete root.dataset.intro;

    const pointer = { x: -1e5, y: -1e5, inside: false, downId: null };
    const light = { x: -1e5, y: -1e5, near: 0, ready: false };
    const wave = { x: 0, y: 0, t: -1, duration: 0.95, reach: 800 };
    const states = new Map();
    let rects = new Map();
    let bounds = null;
    let dirty = true;
    let raf = 0;
    let last = 0;
    let visible = true;
    let entered = !playIntro;
    let settleTimer = 0;

    const stateFor = (id) => {
      let state = states.get(id);
      if (!state) {
        state = {
          hot: 0,
          rx: 0,
          ry: 0,
          tx: 0,
          ty: 0,
          vrx: 0,
          vry: 0,
          vtx: 0,
          vty: 0,
          press: 1,
          vpress: 0,
          written: {},
        };
        states.set(id, state);
      }
      return state;
    };

    const write = (element, state, name, value) => {
      if (state.written[name] === value) return;
      state.written[name] = value;
      element.style.setProperty(name, value);
    };

    const measure = () => {
      dirty = false;
      bounds = root.getBoundingClientRect();
      rects = new Map();
      itemsRef.current.forEach(({ cell }, id) => {
        if (cell) rects.set(id, cell.getBoundingClientRect());
      });
    };

    const startWave = (x, y, duration) => {
      if (!motion || !bounds) return;
      wave.x = x;
      wave.y = y;
      wave.t = 0;
      wave.duration = duration;
      wave.reach =
        Math.max(
          Math.hypot(x - bounds.left, y - bounds.top),
          Math.hypot(x - bounds.right, y - bounds.top),
          Math.hypot(x - bounds.left, y - bounds.bottom),
          Math.hypot(x - bounds.right, y - bounds.bottom),
        ) + 140;
    };

    const frame = (now) => {
      raf = 0;
      const dt = Math.min(0.05, (now - last) / 1000 || 0);
      last = now;
      if (dirty) measure();
      const s = settingsRef.current;
      let busy = false;

      const margin = s.spotlightRadius * 0.5;
      const near =
        pointer.inside &&
        bounds &&
        pointer.x > bounds.left - margin &&
        pointer.x < bounds.right + margin &&
        pointer.y > bounds.top - margin &&
        pointer.y < bounds.bottom + margin
          ? 1
          : 0;

      if (!light.ready && near) {
        light.x = pointer.x;
        light.y = pointer.y;
        light.ready = true;
      }

      const follow = 1 - Math.exp(-dt / 0.05);
      light.x += (pointer.x - light.x) * follow;
      light.y += (pointer.y - light.y) * follow;

      const fade = 1 - Math.exp(-dt / (near > light.near ? 0.12 : 0.36));
      light.near += (near - light.near) * fade;
      if (Math.abs(near - light.near) < 0.002) light.near = near;
      else busy = true;

      if (Math.abs(pointer.x - light.x) + Math.abs(pointer.y - light.y) > 0.3) {
        busy = true;
      }

      let waveRadius = 0;
      let waveAlpha = 0;
      if (wave.t >= 0) {
        wave.t += dt;
        const progress = Math.min(1, wave.t / wave.duration);
        waveRadius = wave.reach * (1 - Math.pow(1 - progress, 2.2));
        waveAlpha = Math.pow(1 - progress, 1.35);
        if (progress >= 1) {
          wave.t = -1;
          waveAlpha = 0;
        } else {
          busy = true;
        }
      }

      itemsRef.current.forEach(({ card }, id) => {
        const rect = rects.get(id);
        if (!card || !rect) return;
        const state = stateFor(id);

        const lx = light.x - rect.left;
        const ly = light.y - rect.top;
        const px = pointer.x - rect.left;
        const py = pointer.y - rect.top;
        const inside =
          pointer.inside &&
          entered &&
          px >= 0 &&
          py >= 0 &&
          px <= rect.width &&
          py <= rect.height;

        const hotTarget = inside && s.enableSpotlight ? 1 : 0;
        state.hot +=
          (hotTarget - state.hot) *
          (1 - Math.exp(-dt / (hotTarget > state.hot ? 0.14 : 0.3)));
        if (Math.abs(hotTarget - state.hot) < 0.002) state.hot = hotTarget;
        else busy = true;

        const u = clamp(px / Math.max(1, rect.width), 0, 1) - 0.5;
        const v = clamp(py / Math.max(1, rect.height), 0, 1) - 0.5;
        const tilt = inside && s.enableTilt && motion;
        const magnet = inside && s.enableMagnetism && motion;
        const targets = [
          tilt ? -v * 2 * TILT_MAX : 0,
          tilt ? u * 2 * TILT_MAX : 0,
          magnet ? u * 2 * MAGNET_MAX : 0,
          magnet ? v * 2 * MAGNET_MAX : 0,
        ];
        const keys = [
          ['rx', 'vrx'],
          ['ry', 'vry'],
          ['tx', 'vtx'],
          ['ty', 'vty'],
        ];
        const steps = Math.max(1, Math.ceil(dt / (1 / 240)));
        const h = dt / steps;

        keys.forEach(([key, velocity], k) => {
          for (let step = 0; step < steps; step++) {
            state[velocity] +=
              (170 * (targets[k] - state[key]) - 20 * state[velocity]) * h;
            state[key] += state[velocity] * h;
          }
          if (
            Math.abs(targets[k] - state[key]) < 0.002 &&
            Math.abs(state[velocity]) < 0.01
          ) {
            state[key] = targets[k];
            state[velocity] = 0;
          } else {
            busy = true;
          }
        });

        const pressTarget =
          pointer.downId === id && s.clickEffect && motion ? 0.986 : 1;
        for (let step = 0; step < steps; step++) {
          state.vpress +=
            (420 * (pressTarget - state.press) - 28 * state.vpress) * h;
          state.press += state.vpress * h;
        }
        if (
          Math.abs(pressTarget - state.press) < 0.0002 &&
          Math.abs(state.vpress) < 0.001
        ) {
          state.press = pressTarget;
          state.vpress = 0;
        } else {
          busy = true;
        }

        write(card, state, '--mb-x', `${lx.toFixed(1)}px`);
        write(card, state, '--mb-y', `${ly.toFixed(1)}px`);
        write(
          card,
          state,
          '--mb-near',
          (s.enableBorderGlow && motion ? light.near : 0).toFixed(3),
        );
        write(card, state, '--mb-hot', (motion ? state.hot : 0).toFixed(3));
        write(card, state, '--mb-rx', `${state.rx.toFixed(3)}deg`);
        write(card, state, '--mb-ry', `${state.ry.toFixed(3)}deg`);
        write(card, state, '--mb-tx', `${state.tx.toFixed(2)}px`);
        write(card, state, '--mb-ty', `${state.ty.toFixed(2)}px`);
        write(card, state, '--mb-press', state.press.toFixed(4));
        write(card, state, '--mb-wx', `${(wave.x - rect.left).toFixed(1)}px`);
        write(card, state, '--mb-wy', `${(wave.y - rect.top).toFixed(1)}px`);
        write(card, state, '--mb-wr', `${waveRadius.toFixed(1)}px`);
        write(card, state, '--mb-wa', waveAlpha.toFixed(3));
      });

      if (busy && visible) raf = requestAnimationFrame(frame);
    };

    const wake = () => {
      if (raf || !visible) return;
      last = performance.now();
      raf = requestAnimationFrame(frame);
    };

    const findCardId = (target) => {
      if (!(target instanceof Element)) return null;
      const cardEl = target.closest('.aa-bento__card');
      if (!cardEl) return null;
      for (const [id, entry] of itemsRef.current.entries()) {
        if (entry.card === cardEl) return id;
      }
      return null;
    };

    const onPointerMove = (event) => {
      pointer.x = event.clientX;
      pointer.y = event.clientY;
      pointer.inside = true;
      wake();
    };

    const onPointerOut = (event) => {
      if (event.relatedTarget) return;
      pointer.inside = false;
      pointer.downId = null;
      wake();
    };

    const onPointerDown = (event) => {
      pointer.x = event.clientX;
      pointer.y = event.clientY;
      pointer.inside = true;
      pointer.downId = findCardId(event.target);
      wake();
    };

    const release = () => {
      if (pointer.downId == null) return;
      pointer.downId = null;
      wake();
    };

    const onClick = (event) => {
      const id = findCardId(event.target);
      if (id == null || !settingsRef.current.clickEffect) return;
      if (dirty) measure();
      const entry = itemsRef.current.get(id);
      if (!entry?.card) return;
      if (event.detail === 0) {
        const rect = entry.card.getBoundingClientRect();
        startWave(rect.left + rect.width / 2, rect.top + rect.height / 2, 0.95);
      } else {
        startWave(event.clientX, event.clientY, 0.95);
      }
      wake();
    };

    const onScroll = () => {
      dirty = true;
      wake();
    };

    const resizeObserver =
      typeof ResizeObserver !== 'undefined'
        ? new ResizeObserver(() => {
            dirty = true;
            wake();
          })
        : null;
    resizeObserver?.observe(root);

    const enter = () => {
      entered = true;
      const sorted = Array.from(itemsRef.current.values()).sort(
        (a, b) => a.order - b.order,
      );
      sorted.forEach(({ cell }, index) => {
        if (!cell) return;
        cell.style.setProperty(
          '--mb-delay',
          `${index * settingsRef.current.staggerMs}ms`,
        );
        cell.dataset.state = 'in';
      });
      if (dirty) measure();
      settleTimer = window.setTimeout(
        () => {
          itemsRef.current.forEach(({ cell }) =>
            cell?.style.setProperty('--mb-delay', '0ms'),
          );
        },
        sorted.length * settingsRef.current.staggerMs + 1000,
      );
      wake();
    };

    let gate = null;
    if (playIntro && typeof IntersectionObserver !== 'undefined') {
      gate = new IntersectionObserver(
        (entries, observer) => {
          if (!entries.some((entry) => entry.isIntersecting)) return;
          observer.disconnect();
          enter();
        },
        { threshold: 0.08 },
      );
      gate.observe(root);
    } else {
      itemsRef.current.forEach(({ cell }) => {
        if (!cell) return;
        cell.style.setProperty('--mb-delay', '0ms');
        cell.dataset.state = 'in';
      });
    }

    const visibility =
      typeof IntersectionObserver !== 'undefined'
        ? new IntersectionObserver((entries) => {
            visible = entries.some((entry) => entry.isIntersecting);
            if (visible) {
              dirty = true;
              wake();
            }
          })
        : null;
    visibility?.observe(root);

    window.addEventListener('pointermove', onPointerMove, { passive: true });
    window.addEventListener('pointerdown', onPointerDown, { passive: true });
    window.addEventListener('pointerup', release, { passive: true });
    window.addEventListener('pointercancel', release, { passive: true });
    window.addEventListener('scroll', onScroll, { passive: true, capture: true });
    window.addEventListener('resize', onScroll, { passive: true });
    document.addEventListener('pointerout', onPointerOut);
    root.addEventListener('click', onClick);

    engineRef.current = {
      sync: () => {
        if (entered || !playIntro) {
          itemsRef.current.forEach(({ cell }) => {
            if (!cell) return;
            if (cell.dataset.state !== 'in') {
              cell.style.setProperty('--mb-delay', '0ms');
              cell.dataset.state = 'in';
            }
          });
        }
        dirty = true;
        wake();
      },
    };

    return () => {
      cancelAnimationFrame(raf);
      window.clearTimeout(settleTimer);
      gate?.disconnect();
      visibility?.disconnect();
      resizeObserver?.disconnect();
      window.removeEventListener('pointermove', onPointerMove);
      window.removeEventListener('pointerdown', onPointerDown);
      window.removeEventListener('pointerup', release);
      window.removeEventListener('pointercancel', release);
      window.removeEventListener('scroll', onScroll, { capture: true });
      window.removeEventListener('resize', onScroll);
      document.removeEventListener('pointerout', onPointerOut);
      root.removeEventListener('click', onClick);
      engineRef.current = null;
    };
  }, []);

  const register = useCallback((id, cell, card) => {
    if (!cell || !card) {
      itemsRef.current.delete(id);
      return;
    }
    const existing = itemsRef.current.get(id);
    const order = existing ? existing.order : orderRef.current++;
    itemsRef.current.set(id, { cell, card, order });
    engineRef.current?.sync();
  }, []);

  const contextValue = useMemo(
    () => ({
      register,
      enableBorderGlow,
      enableSpotlight,
    }),
    [register, enableBorderGlow, enableSpotlight],
  );

  const glowRgb = parseRgb(glowColor) ?? '5, 150, 105';
  const spotSize = Math.max(60, spotlightRadius);

  return (
    <BentoContext.Provider value={contextValue}>
      <div
        ref={rootRef}
        className={`aa-bento ${className}`.trim()}
        style={{
          '--mb-glow-rgb': glowRgb,
          '--mb-spot-size': `${spotSize}px`,
          '--mb-border-size': `${Math.round(spotSize * 0.78)}px`,
          ...style,
        }}
      >
        {children}
      </div>
    </BentoContext.Provider>
  );
}

let nextCardId = 1;

export function BentoCard({
  children,
  glowColor,
  className = '',
  cardClassName = '',
  style,
}) {
  const ctx = useContext(BentoContext);
  const idRef = useRef(null);
  if (idRef.current == null) idRef.current = nextCardId++;
  const cellRef = useRef(null);
  const cardRef = useRef(null);

  useIsomorphicLayoutEffect(() => {
    if (!ctx) return undefined;
    const id = idRef.current;
    ctx.register(id, cellRef.current, cardRef.current);
    return () => ctx.register(id, null, null);
  }, [ctx]);

  const overrideGlow = glowColor ? parseRgb(glowColor) : null;

  return (
    <div ref={cellRef} className={`aa-bento__cell ${className}`.trim()} style={style}>
      <div
        ref={cardRef}
        className={`aa-bento__card ${cardClassName}`.trim()}
        style={overrideGlow ? { '--mb-glow-rgb': overrideGlow } : undefined}
      >
        {ctx?.enableSpotlight !== false ? (
          <span className="aa-bento__light" aria-hidden="true" />
        ) : null}
        <div className="aa-bento__content">{children}</div>
        {ctx?.enableBorderGlow !== false ? (
          <span className="aa-bento__border" aria-hidden="true" />
        ) : null}
      </div>
    </div>
  );
}
