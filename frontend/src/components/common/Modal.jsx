import { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

const SIZE_CLASSES = {
  sm: 'sm:max-w-md',
  md: 'sm:max-w-lg',
  lg: 'sm:max-w-2xl',
  xl: 'sm:max-w-4xl',
};

export default function Modal({
  open,
  onClose,
  title,
  description,
  children,
  footer,
  size = 'md',
  busy = false,
  closeOnBackdrop = true,
}) {
  const dialogRef = useRef(null);
  const previousFocusRef = useRef(null);
  const onCloseRef = useRef(onClose);
  const busyRef = useRef(busy);
  const wasBusyRef = useRef(busy);

  useEffect(() => {
    onCloseRef.current = onClose;
    busyRef.current = busy;
  });

  useEffect(() => {
    if (!open) return undefined;

    previousFocusRef.current = document.activeElement;
    const overflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    const focusTarget =
      dialogRef.current?.querySelector(
        'input:not([type="hidden"]), select, textarea, button:not([aria-label="Cerrar"])',
      ) || dialogRef.current;
    focusTarget?.focus?.();

    const handleKeyDown = (event) => {
      if (event.key === 'Escape' && !busyRef.current && onCloseRef.current) {
        event.stopPropagation();
        onCloseRef.current();
      }
      if (event.key === 'Tab' && dialogRef.current) {
        const focusable = dialogRef.current.querySelectorAll(
          'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])',
        );
        if (focusable.length === 0) return;
        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (event.shiftKey && document.activeElement === first) {
          event.preventDefault();
          last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
          event.preventDefault();
          first.focus();
        }
      }
    };

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = overflow;
      previousFocusRef.current?.focus?.();
    };
  }, [open]);

  useEffect(() => {
    const wasBusy = wasBusyRef.current;
    wasBusyRef.current = busy;
    if (!open || busy || !wasBusy) return;
    dialogRef.current
      ?.querySelector('input:not([type="hidden"]), select, textarea')
      ?.focus?.();
  }, [open, busy]);

  if (!open) return null;

  return createPortal(
    <div className="fixed inset-0 z-50 flex items-end justify-center overflow-y-auto p-4 sm:items-center sm:p-6">
      <div
        className="aa-fade fixed inset-0 bg-stone-900/45"
        onClick={() => {
          if (closeOnBackdrop && !busy && onClose) onClose();
        }}
        aria-hidden="true"
      />
      <div
        ref={dialogRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        tabIndex={-1}
        className={`aa-rise relative w-full rounded-t-2xl bg-white shadow-2xl outline-none sm:rounded-2xl ${
          SIZE_CLASSES[size] || SIZE_CLASSES.md
        }`}
      >
        <div className="flex items-start justify-between gap-4 border-b border-stone-200 px-5 py-4">
          <div className="min-w-0">
            <h2 id="modal-title" className="text-base font-semibold text-stone-900">
              {title}
            </h2>
            {description ? (
              <p className="mt-0.5 text-sm text-stone-500">{description}</p>
            ) : null}
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={busy}
            aria-label="Cerrar"
            className="-mr-1 shrink-0 rounded-lg p-1.5 text-stone-400 transition hover:bg-stone-100 hover:text-stone-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <X size={18} />
          </button>
        </div>

        <div className="px-5 py-5">{children}</div>

        {footer ? (
          <div className="flex flex-col-reverse gap-2 border-t border-stone-200 px-5 py-4 sm:flex-row sm:justify-end">
            {footer}
          </div>
        ) : null}
      </div>
    </div>,
    document.body,
  );
}
