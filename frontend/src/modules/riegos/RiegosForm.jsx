export default function getRiegosFields({ campanas } = {}) {
  return [
    {
      name: 'idCampana',
      label: 'Campaña',
      type: 'select',
      required: true,
      placeholder: 'Seleccione una campaña',
      options: campanas?.options || [],
      optionsLoading: Boolean(campanas?.loading),
      optionsError: Boolean(campanas?.error),
    },
    {
      name: 'fechaRiego',
      label: 'Fecha de riego',
      type: 'date',
      required: true,
    },
    {
      name: 'cantidadM3',
      label: 'Cantidad (m³)',
      type: 'number',
      required: true,
      min: 0,
      minMessage: 'La cantidad no puede ser negativa.',
      step: '0.01',
      placeholder: 'Ej. 45.5',
    },
    {
      name: 'observacion',
      label: 'Observación',
      type: 'textarea',
      maxLength: 500,
      placeholder: 'Ej. Riego por goteo, turno de la mañana',
      hint: 'Opcional.',
    },
  ];
}
