export default function getCosechasFields({ campanas } = {}) {
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
      name: 'fechaCosecha',
      label: 'Fecha de cosecha',
      type: 'date',
      required: true,
    },
    {
      name: 'cantidadKg',
      label: 'Cantidad (kg)',
      type: 'number',
      required: true,
      min: 0,
      minMessage: 'La cantidad no puede ser negativa.',
      step: '0.01',
      placeholder: 'Ej. 1250.5',
    },
    {
      name: 'observacion',
      label: 'Observación',
      type: 'textarea',
      maxLength: 500,
      placeholder: 'Ej. Primera cosecha del lote 3',
      hint: 'Opcional.',
    },
  ];
}
