import FormField from './FormField';
import { Input, Select, Textarea } from './inputs';

function FieldControl({ field, value, invalid, disabled, onChange }) {
  const common = {
    id: field.name,
    name: field.name,
    disabled,
    invalid,
    'aria-invalid': invalid || undefined,
  };

  if (field.type === 'textarea') {
    return (
      <Textarea
        {...common}
        value={value ?? ''}
        placeholder={field.placeholder}
        onChange={(event) => onChange(field.name, event.target.value)}
      />
    );
  }

  if (field.type === 'select') {
    const options = field.options || [];
    return (
      <Select
        {...common}
        value={value == null ? '' : String(value)}
        onChange={(event) => {
          const raw = event.target.value;
          if (raw === '') {
            onChange(field.name, '');
          } else if (field.numeric === false) {
            onChange(field.name, raw);
          } else {
            onChange(field.name, Number(raw));
          }
        }}
      >
        <option value="">
          {field.optionsLoading
            ? 'Cargando opciones…'
            : (field.placeholder || 'Seleccione una opción')}
        </option>
        {options.map((option) => (
          <option key={String(option.value)} value={String(option.value)}>
            {option.label}
          </option>
        ))}
      </Select>
    );
  }

  if (field.type === 'number') {
    return (
      <Input
        {...common}
        type="number"
        inputMode="decimal"
        min={field.min}
        max={field.max}
        step={field.step ?? 'any'}
        value={value == null ? '' : value}
        placeholder={field.placeholder}
        onChange={(event) => onChange(field.name, event.target.value)}
      />
    );
  }

  if (field.type === 'date') {
    return (
      <Input
        {...common}
        type="date"
        value={value ?? ''}
        onChange={(event) => onChange(field.name, event.target.value)}
      />
    );
  }

  return (
    <Input
      {...common}
      type={field.inputType || 'text'}
      value={value ?? ''}
      placeholder={field.placeholder}
      maxLength={field.maxLength}
      onChange={(event) => onChange(field.name, event.target.value)}
    />
  );
}

export default function EntityForm({ fields, values, errors = {}, onChange, disabled = false }) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
      {fields.map((field) => {
        const invalid = Boolean(errors[field.name]);
        const full = field.full || field.type === 'textarea';
        return (
          <div key={field.name} className={full ? 'sm:col-span-2' : ''}>
            <FormField
              label={field.label}
              htmlFor={field.name}
              required={field.required}
              error={errors[field.name]}
              hint={field.hint}
            >
              <FieldControl
                field={field}
                value={values[field.name]}
                invalid={invalid}
                disabled={disabled || (field.type === 'select' && field.optionsLoading)}
                onChange={onChange}
              />
            </FormField>
            {field.optionsError ? (
              <p className="mt-1.5 text-xs font-medium text-rose-600">
                No se pudieron cargar las opciones. Verifique la conexión.
              </p>
            ) : null}
          </div>
        );
      })}
    </div>
  );
}
