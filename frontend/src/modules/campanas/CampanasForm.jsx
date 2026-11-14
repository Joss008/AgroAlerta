export function validateCampanas(values) {
  const errors = {};
  if (values.fechaInicio && values.fechaFin && values.fechaFin < values.fechaInicio) {
    errors.fechaFin = 'La fecha de fin no puede ser anterior a la fecha de inicio.';
  }
  return errors;
}

export default function getCampanasFields({ parcelas, cultivos } = {}) {
  return [
    {
      name: 'nombre',
      label: 'Nombre de la campaña',
      type: 'text',
      required: true,
      maxLength: 120,
      placeholder: 'Ej. Campana Espárrago 2026',
    },
    {
      name: 'idParcela',
      label: 'Parcela',
      type: 'select',
      required: true,
      placeholder: 'Seleccione una parcela',
      options: parcelas?.options || [],
      optionsLoading: Boolean(parcelas?.loading),
      optionsError: Boolean(parcelas?.error),
    },
    {
      name: 'idCultivo',
      label: 'Cultivo',
      type: 'select',
      required: true,
      placeholder: 'Seleccione un cultivo',
      options: cultivos?.options || [],
      optionsLoading: Boolean(cultivos?.loading),
      optionsError: Boolean(cultivos?.error),
    },
    {
      name: 'fechaInicio',
      label: 'Fecha de inicio',
      type: 'date',
      required: true,
    },
    {
      name: 'fechaFin',
      label: 'Fecha de fin',
      type: 'date',
      hint: 'Déjela vacía si la campaña sigue en curso.',
    },
  ];
}
