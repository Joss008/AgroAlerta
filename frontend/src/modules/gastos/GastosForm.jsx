export default function getGastosFields({ campanas } = {}) {
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
      name: 'concepto',
      label: 'Concepto',
      type: 'text',
      required: true,
      maxLength: 150,
      placeholder: 'Ej. Fertilizante, mano de obra, abono',
    },
    {
      name: 'fechaGasto',
      label: 'Fecha del gasto',
      type: 'date',
      required: true,
    },
    {
      name: 'monto',
      label: 'Monto (S/)',
      type: 'number',
      required: true,
      min: 0,
      minMessage: 'El monto no puede ser negativo.',
      step: '0.01',
      placeholder: 'Ej. 350.00',
    },
  ];
}
