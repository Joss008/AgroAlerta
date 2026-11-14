import { useEffect, useState } from 'react';
import { Loader2, TriangleAlert } from 'lucide-react';
import Modal from './Modal';
import { getApiMessage } from '../../utils/errors';

export default function ConfirmDialog({
  open,
  title = 'Confirmar eliminación',
  message,
  confirmLabel = 'Eliminar',
  cancelLabel = 'Cancelar',
  onConfirm,
  onClose,
}) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (open) {
      setBusy(false);
      setError(null);
    }
  }, [open]);

  const handleConfirm = async () => {
    setBusy(true);
    setError(null);
    try {
      await onConfirm();
    } catch (err) {
      setError(getApiMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={busy ? undefined : onClose}
      title={title}
      size="sm"
      busy={busy}
      closeOnBackdrop={!busy}
      footer={
        <>
          <button
            type="button"
            onClick={onClose}
            disabled={busy}
            className="rounded-lg border border-stone-300 bg-white px-4 py-2 text-sm font-medium text-stone-700 transition hover:bg-stone-50 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {cancelLabel}
          </button>
          <button
            type="button"
            onClick={handleConfirm}
            disabled={busy}
            className="inline-flex items-center justify-center gap-2 rounded-lg bg-rose-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-rose-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {busy ? <Loader2 size={15} className="animate-spin" aria-hidden="true" /> : null}
            {busy ? 'Eliminando…' : confirmLabel}
          </button>
        </>
      }
    >
      <div className="space-y-3">
        <div className="flex items-start gap-3">
          <span className="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-rose-50">
            <TriangleAlert size={18} className="text-rose-600" aria-hidden="true" />
          </span>
          <p className="text-sm leading-relaxed text-stone-600">{message}</p>
        </div>
        {error ? (
          <div
            role="alert"
            className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm leading-snug text-rose-800"
          >
            {error}
          </div>
        ) : null}
      </div>
    </Modal>
  );
}
