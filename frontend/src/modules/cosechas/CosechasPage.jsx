import { useMemo } from 'react';
import CrudPage from '../../components/common/CrudPage';
import cosechas from '../../services/cosechas';
import campanas from '../../services/campanas';
import useSelectOptions from '../../hooks/useSelectOptions';
import { DASH, formatDate, formatKg, truncate } from '../../utils/format';
import getCosechasFields from './CosechasForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'nombreCampana',
    header: 'Campaña',
    render: (row) => row.nombreCampana || DASH,
  },
  {
    key: 'fechaCosecha',
    header: 'Fecha',
    render: (row) => formatDate(row.fechaCosecha),
  },
  {
    key: 'cantidadKg',
    header: 'Cantidad',
    align: 'right',
    render: (row) => formatKg(row.cantidadKg),
  },
  {
    key: 'observacion',
    header: 'Observación',
    render: (row) => (row.observacion ? truncate(row.observacion, 60) : DASH),
  },
];

export default function CosechasPage() {
  const campanasOptions = useSelectOptions(campanas.list, (row) => ({
    value: row.id,
    label: row.nombreParcela ? `${row.nombre} · ${row.nombreParcela}` : row.nombre,
  }));

  const fields = useMemo(
    () => getCosechasFields({ campanas: campanasOptions }),
    [campanasOptions],
  );

  return (
    <CrudPage
      title="Cosechas"
      description="Producción cosechada por campaña, en kilogramos, con su fecha y observaciones."
      service={cosechas}
      columns={columns}
      fields={fields}
      entityLabel="cosecha"
      entityLabelPlural="cosechas"
      newLabel="Nueva"
      searchPlaceholder="Buscar cosecha…"
      defaultValues={{ idCampana: '', fechaCosecha: '', cantidadKg: '', observacion: '' }}
      filters={[
        {
          name: 'idCampana',
          placeholder: 'Todas las campañas',
          ariaLabel: 'Filtrar por campaña',
          options: campanasOptions.options,
          loading: campanasOptions.loading,
          error: campanasOptions.error,
        },
      ]}
    />
  );
}
