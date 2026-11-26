import { useEffect, useState } from 'react';
import { User } from 'lucide-react';

const SIZES = {
  xs: 'h-6 w-6 text-[10px]',
  sm: 'h-8 w-8 text-xs',
  md: 'h-10 w-10 text-sm',
  lg: 'h-14 w-14 text-base',
};

function getInitials(label) {
  if (!label) return '';
  const parts = String(label).trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

export default function Thumbnail({
  src,
  alt = '',
  size = 'sm',
  rounded = 'full',
  className = '',
}) {
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    setFailed(false);
  }, [src]);

  const sizeClass = SIZES[size] || SIZES.sm;
  const radiusClass = rounded === 'lg' ? 'rounded-lg' : 'rounded-full';
  const showImage = Boolean(src) && !failed;
  const initials = getInitials(alt);

  return (
    <span
      className={`inline-flex shrink-0 items-center justify-center overflow-hidden border border-stone-200 bg-stone-100 font-semibold text-stone-600 select-none ${sizeClass} ${radiusClass} ${className}`}
    >
      {showImage ? (
        <img
          src={src}
          alt={alt}
          className="h-full w-full object-cover"
          onError={() => setFailed(true)}
        />
      ) : initials ? (
        <span aria-hidden="true">{initials}</span>
      ) : (
        <User size={14} className="text-stone-400" aria-hidden="true" />
      )}
    </span>
  );
}
