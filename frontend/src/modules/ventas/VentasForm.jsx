import { formatDate, formatKg } from '../../utils/format';

export default function getVentasFields({ cosechas } = {}) {
  return [
    {
      name: 'idCosecha',
      label: 'Cosecha',
      type: 'select',
      required: true,
      placeholder: 'Seleccione una cosecha',
      options: cosechas?.options || [],
      optionsLoading: Boolean(cosechas?.loading),
      optionsError: Boolean(cosechas?.error),
    },
    {
      name: 'fechaVenta',
      label: 'Fecha de venta',
      type: 'date',
      required: true,
    },
    {
      name: 'cantidadKg',
      label: 'Cantidad vendida (kg)',
      type: 'number',
      required: true,
      min: 0,
      minMessage: 'La cantidad no puede ser negativa.',
      step: '0.01',
      placeholder: 'Ej. 500',
    },
    {
      name: 'precioTotal',
      label: 'Precio total (S/)',
      type: 'number',
      required: true,
      min: 0,
      minMessage: 'El precio no puede ser negativo.',
      step: '0.01',
      placeholder: 'Ej. 4250.00',
    },
    {
      name: 'comprador',
      label: 'Comprador',
      type: 'text',
      required: true,
      maxLength: 150,
      placeholder: 'Ej. Exportadora Pacífico S.A.',
    },
  ];
}

export function mapCosechaOption(row) {
  const fecha = formatDate(row.fechaCosecha);
  const campana = row.nombreCampana || 'Sin campaña';
  return {
    value: row.id,
    label: `${fecha} · ${campana} (${formatKg(row.cantidadKg)})`,
  };
}
