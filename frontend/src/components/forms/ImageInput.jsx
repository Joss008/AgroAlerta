import { useRef, useState } from 'react';
import { ImagePlus, Trash2 } from 'lucide-react';
import Thumbnail from '../common/Thumbnail';
import { Input } from './inputs';

const MAX_DIMENSION = 280;
const JPEG_QUALITY = 0.8;

function compressImage(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = () => reject(new Error('No se pudo leer el archivo.'));
    reader.onload = () => {
      const img = new Image();
      img.onerror = () => reject(new Error('El archivo no es una imagen válida.'));
      img.onload = () => {
        const scale = Math.min(1, MAX_DIMENSION / Math.max(img.width, img.height));
        const width = Math.max(1, Math.round(img.width * scale));
        const height = Math.max(1, Math.round(img.height * scale));
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          reject(new Error('No se pudo procesar la imagen.'));
          return;
        }
        ctx.drawImage(img, 0, 0, width, height);
        resolve(canvas.toDataURL('image/jpeg', JPEG_QUALITY));
      };
      img.src = String(reader.result || '');
    };
    reader.readAsDataURL(file);
  });
}

export default function ImageInput({
  id,
  name,
  value = '',
  disabled = false,
  invalid = false,
  placeholder = 'https://… o suba una foto',
  onChange,
}) {
  const fileRef = useRef(null);
  const [busy, setBusy] = useState(false);
  const [localError, setLocalError] = useState('');

  const handleFile = async (event) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;
    setLocalError('');
    setBusy(true);
    try {
      const dataUrl = await compressImage(file);
      onChange(dataUrl);
    } catch (error) {
      setLocalError(error.message || 'No se pudo cargar la imagen.');
    } finally {
      setBusy(false);
    }
  };

  const hasValue = Boolean(String(value || '').trim());

  return (
    <div className="space-y-2">
      <div className="flex items-center gap-3">
        <Thumbnail src={value} alt="Vista previa" size="lg" />

        <div className="flex min-w-0 flex-1 flex-col gap-2">
          <div className="flex flex-wrap items-center gap-2">
            <input
              ref={fileRef}
              type="file"
              accept="image/*"
              className="hidden"
              disabled={disabled || busy}
              onChange={handleFile}
            />
            <button
              type="button"
              disabled={disabled || busy}
              onClick={() => fileRef.current?.click()}
              className="inline-flex items-center gap-1.5 rounded-lg border border-stone-300 bg-white px-3 py-1.5 text-xs font-medium text-stone-700 transition hover:bg-stone-50 disabled:opacity-50"
            >
              <ImagePlus size={14} aria-hidden="true" />
              {busy ? 'Procesando…' : hasValue ? 'Cambiar foto' : 'Subir foto'}
            </button>

            {hasValue ? (
              <button
                type="button"
                disabled={disabled || busy}
                onClick={() => {
                  setLocalError('');
                  onChange('');
                }}
                className="inline-flex items-center gap-1 rounded-lg border border-stone-200 px-2.5 py-1.5 text-xs font-medium text-stone-600 transition hover:bg-rose-50 hover:text-rose-700 disabled:opacity-50"
              >
                <Trash2 size={13} aria-hidden="true" />
                Quitar
              </button>
            ) : null}
          </div>

          <Input
            id={id}
            name={name}
            type="text"
            disabled={disabled || busy}
            invalid={invalid}
            value={value ?? ''}
            placeholder={placeholder}
            onChange={(event) => {
              setLocalError('');
              onChange(event.target.value);
            }}
          />
        </div>
      </div>

      {localError ? (
        <p className="text-xs font-medium text-rose-600">{localError}</p>
      ) : null}
    </div>
  );
}
