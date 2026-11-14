import { useMemo } from 'react';
import CrudPage from '../../components/common/CrudPage';
import ventas from '../../services/ventas';
import cosechas from '../../services/cosechas';
import useSelectOptions from '../../hooks/useSelectOptions';
import { DASH, formatDate, formatKg, formatSoles } from '../../utils/format';
import getVentasFields, { mapCosechaOption } from './VentasForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'nombreCampana',
    header: 'Campaña',
    render: (row) => row.nombreCampana || DASH,
  },
  {
    key: 'idCosecha',
    header: 'Cosecha',
    align: 'right',
    render: (row) => `#${row.idCosecha}`,
  },
  {
    key: 'fechaVenta',
    header: 'Fecha de venta',
    render: (row) => formatDate(row.fechaVenta),
  },
  {
    key: 'cantidadKg',
    header: 'Cantidad',
    align: 'right',
    render: (row) => formatKg(row.cantidadKg),
  },
  {
    key: 'precioTotal',
    header: 'Monto',
    align: 'right',
    render: (row) => formatSoles(row.precioTotal),
  },
  {
    key: 'comprador',
    header: 'Comprador',
    render: (row) => row.comprador || DASH,
  },
];

export default function VentasPage() {
  const cosechasOptions = useSelectOptions(cosechas.list, mapCosechaOption);

  const fields = useMemo(
    () => getVentasFields({ cosechas: cosechasOptions }),
    [cosechasOptions],
  );

  return (
    <CrudPage
      title="Ventas"
      description="Ventas de la producción, vinculadas a la cosecha de la que provienen."
      service={ventas}
      columns={columns}
      fields={fields}
      entityLabel="venta"
      entityLabelPlural="ventas"
      newLabel="Nueva"
      searchPlaceholder="Buscar venta…"
      defaultValues={{
        idCosecha: '',
        fechaVenta: '',
        cantidadKg: '',
        precioTotal: '',
        comprador: '',
      }}
      filters={[
        {
          name: 'idCosecha',
          placeholder: 'Todas las cosechas',
          ariaLabel: 'Filtrar por cosecha',
          options: cosechasOptions.options,
          loading: cosechasOptions.loading,
          error: cosechasOptions.error,
        },
      ]}
    />
  );
}
