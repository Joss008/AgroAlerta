import StatusMark from '../motion/StatusMark';

export default function Loading({ label = 'Cargando…', className = '', compact = false }) {
  return (
    <div
      className={`flex flex-col items-center justify-center gap-3 ${compact ? 'py-6' : 'py-16'} ${className}`}
      role="status"
    >
      <StatusMark
        status="running"
        size={compact ? 22 : 26}
        strokeWidth={2.2}
        color="#059669"
      />
      <p className="text-sm text-stone-500">{label}</p>
    </div>
  );
}
