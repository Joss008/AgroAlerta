export const agricultoresFields = [
  {
    name: 'dni',
    label: 'DNI',
    type: 'text',
    required: true,
    maxLength: 8,
    placeholder: 'Ej. 71234567',
    hint: '8 dígitos numéricos.',
  },
  {
    name: 'nombres',
    label: 'Nombres',
    type: 'text',
    required: true,
    maxLength: 100,
    placeholder: 'Ej. María Elena',
  },
  {
    name: 'apellidos',
    label: 'Apellidos',
    type: 'text',
    required: true,
    maxLength: 100,
    placeholder: 'Ej. Quispe Ramos',
  },
  {
    name: 'telefono',
    label: 'Teléfono',
    type: 'text',
    maxLength: 30,
    placeholder: 'Ej. 999 888 777',
    hint: 'Opcional.',
  },
];

export function validateAgricultores(values) {
  const errors = {};
  const dni = String(values.dni ?? '').trim();
  if (dni && !/^\d{8}$/.test(dni)) {
    errors.dni = 'El DNI debe tener exactamente 8 dígitos numéricos.';
  }
  return errors;
}
