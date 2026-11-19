import { useEffect, useLayoutEffect, useRef } from 'react';
import './AnimatedContent.css';

const useIsomorphicLayoutEffect =
  typeof window === 'undefined' ? useEffect : useLayoutEffect;

const WIPES = {
  up: 'linear-gradient(to top, #000 calc(var(--ac-wipe) * 140% - 40%), transparent calc(var(--ac-wipe) * 140%))',
  down: 'linear-gradient(to bottom, #000 calc(var(--ac-wipe) * 140% - 40%), transparent calc(var(--ac-wipe) * 140%))',
  left: 'linear-gradient(to left, #000 calc(var(--ac-wipe) * 140% - 40%), transparent calc(var(--ac-wipe) * 140%))',
  right: 'linear-gradient(to right, #000 calc(var(--ac-wipe) * 140% - 40%), transparent calc(var(--ac-wipe) * 140%))',
  center:
    'radial-gradient(farthest-corner at 50% 50%, #000 calc(var(--ac-wipe) * 140% - 40%), transparent calc(var(--ac-wipe) * 140%))',
};

/**
 * Wrapper de entrada animada sin dependencias externas (sin GSAP):
 * - Disparo por scroll (IntersectionObserver) o mount
 * - Traslación, opacidad, escala, blur, inclinación 3D (tilt) y wipe suave
 * - Stagger entre hijos directos
 * - Limpieza de propiedades GPU al finalizar
 */
export default function AnimatedContent({
  children,
  distance = 28,
  direction = 'vertical',
  reverse = false,
  duration = 0.55,
  easing = 'cubic-bezier(0.22, 1, 0.36, 1)',
  initialOpacity = 0,
  animateOpacity = true,
  scale = 1,
  blur = 0,
  rotate = 0,
  tilt = 0,
  wipe = 'none',
  threshold = 0.1,
  delay = 0,
  stagger = 0,
  trigger = 'scroll',
  once = true,
  onStart,
  onComplete,
  className = '',
  style,
  ...props
}) {
  const ref = useRef(null);
  const callbacksRef = useRef({ onStart, onComplete });
  callbacksRef.current = { onStart, onComplete };

  useIsomorphicLayoutEffect(() => {
    const el = ref.current;
    if (!el) return undefined;

    const reduce =
      window.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? false;

    el.style.visibility = 'visible';

    if (reduce) {
      callbacksRef.current.onComplete?.();
      return undefined;
    }

    const targets =
      stagger > 0 && el.children.length ? Array.from(el.children) : [el];
    const horizontal = direction === 'horizontal';
    const sign = reverse ? -1 : 1;
    const offset = distance * sign;
    const mask = WIPES[wipe];

    const buildTransform = (isStart) => {
      if (!isStart) return 'none';
      const parts = [];
      if (tilt) parts.push('perspective(900px)');
      parts.push(
        horizontal
          ? `translate3d(${offset}px, 0, 0)`
          : `translate3d(0, ${offset}px, 0)`,
      );
      if (scale !== 1) parts.push(`scale(${scale})`);
      if (rotate) parts.push(`rotate(${rotate * sign}deg)`);
      if (tilt) {
        parts.push(
          horizontal
            ? `rotateY(${-tilt * sign}deg)`
            : `rotateX(${tilt * sign}deg)`,
        );
      }
      return parts.join(' ');
    };

    const fromFrame = {
      transform: buildTransform(true),
      opacity: animateOpacity ? initialOpacity : 1,
    };
    const toFrame = {
      transform: buildTransform(false),
      opacity: 1,
    };

    if (blur > 0) {
      fromFrame.filter = `blur(${blur}px)`;
      toFrame.filter = 'blur(0px)';
    }

    if (mask) {
      fromFrame['--ac-wipe'] = 0;
      toFrame['--ac-wipe'] = 1;
    }

    let animations = [];
    let hasPlayed = false;

    const prime = () => {
      targets.forEach((target) => {
        target.style.opacity = String(fromFrame.opacity);
        target.style.transform = fromFrame.transform;
        if (blur > 0) target.style.filter = fromFrame.filter;
        if (mask) {
          target.style.maskImage = mask;
          target.style.webkitMaskImage = mask;
          target.style.setProperty('--ac-wipe', '0');
        }
      });
    };

    const clearTargetProps = (target) => {
      target.style.opacity = '';
      target.style.transform = '';
      target.style.filter = '';
      if (mask) {
        target.style.maskImage = '';
        target.style.webkitMaskImage = '';
        target.style.removeProperty('--ac-wipe');
      }
    };

    const play = () => {
      if (hasPlayed && once) return;
      hasPlayed = true;
      animations.forEach((anim) => anim.cancel());
      animations = [];
      callbacksRef.current.onStart?.();

      let finished = 0;
      targets.forEach((target, index) => {
        const anim = target.animate([fromFrame, toFrame], {
          duration: Math.max(100, duration * 1000),
          delay: Math.max(0, (delay + index * stagger) * 1000),
          easing,
          fill: 'both',
        });
        anim.onfinish = () => {
          anim.cancel();
          clearTargetProps(target);
          finished += 1;
          if (finished === targets.length) {
            callbacksRef.current.onComplete?.();
          }
        };
        animations.push(anim);
      });
    };

    prime();

    if (trigger === 'mount') {
      play();
      return () => animations.forEach((anim) => anim.cancel());
    }

    if (typeof IntersectionObserver === 'undefined') {
      targets.forEach(clearTargetProps);
      return undefined;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          if (entry.isIntersecting) {
            play();
            if (once) observer.disconnect();
          } else if (!once && hasPlayed) {
            hasPlayed = false;
            animations.forEach((anim) => anim.cancel());
            animations = [];
            prime();
          }
        }
      },
      { threshold: clampThreshold(threshold) },
    );

    observer.observe(el);

    return () => {
      observer.disconnect();
      animations.forEach((anim) => anim.cancel());
      targets.forEach(clearTargetProps);
    };
  }, [
    distance,
    direction,
    reverse,
    duration,
    easing,
    initialOpacity,
    animateOpacity,
    scale,
    blur,
    rotate,
    tilt,
    wipe,
    threshold,
    delay,
    stagger,
    trigger,
    once,
  ]);

  return (
    <div
      ref={ref}
      className={className}
      style={{ visibility: 'hidden', ...style }}
      {...props}
    >
      {children}
    </div>
  );
}

function clampThreshold(value) {
  return Math.min(1, Math.max(0, Number(value) || 0));
}
