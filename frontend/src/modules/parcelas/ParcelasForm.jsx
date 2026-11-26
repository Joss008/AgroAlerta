export default function getParcelasFields({ agricultores } = {}) {
  return [
    {
      name: 'fotoUrl',
      label: 'Foto de la parcela',
      type: 'image',
      hint: 'Opcional. Se mostrará en el catálogo de parcelas y al crear campañas.',
    },
    {
      name: 'idAgricultor',
      label: 'Agricultor',
      type: 'select',
      required: true,
      showPreview: true,
      placeholder: 'Seleccione un agricultor',
      options: agricultores?.options || [],
      optionsLoading: Boolean(agricultores?.loading),
      optionsError: Boolean(agricultores?.error),
    },
    {
      name: 'nombre',
      label: 'Nombre de la parcela',
      type: 'text',
      required: true,
      maxLength: 100,
      placeholder: 'Ej. Lote 3 – Pampa Linda',
    },
    {
      name: 'areaHectareas',
      label: 'Área (hectáreas)',
      type: 'number',
      required: true,
      min: 0,
      minMessage: 'El área no puede ser negativa.',
      step: '0.01',
      placeholder: 'Ej. 12.5',
    },
    {
      name: 'ubicacion',
      label: 'Ubicación',
      type: 'text',
      maxLength: 200,
      placeholder: 'Ej. Distrito de Imperial, Cañete',
      hint: 'Opcional.',
    },
  ];
}
