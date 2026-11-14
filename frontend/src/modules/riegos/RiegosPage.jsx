import { useMemo } from 'react';
import CrudPage from '../../components/common/CrudPage';
import riegos from '../../services/riegos';
import campanas from '../../services/campanas';
import useSelectOptions from '../../hooks/useSelectOptions';
import { DASH, formatDate, formatM3, truncate } from '../../utils/format';
import getRiegosFields from './RiegosForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'nombreCampana',
    header: 'Campaña',
    render: (row) => row.nombreCampana || DASH,
  },
  {
    key: 'fechaRiego',
    header: 'Fecha',
    render: (row) => formatDate(row.fechaRiego),
  },
  {
    key: 'cantidadM3',
    header: 'Cantidad',
    align: 'right',
    render: (row) => formatM3(row.cantidadM3),
  },
  {
    key: 'observacion',
    header: 'Observación',
    render: (row) => (row.observacion ? truncate(row.observacion, 60) : DASH),
  },
];

export default function RiegosPage() {
  const campanasOptions = useSelectOptions(campanas.list, (row) => ({
    value: row.id,
    label: row.nombreParcela ? `${row.nombre} · ${row.nombreParcela}` : row.nombre,
  }));

  const fields = useMemo(
    () => getRiegosFields({ campanas: campanasOptions }),
    [campanasOptions],
  );

  return (
    <CrudPage
      title="Riegos"
      description="Registro de riegos aplicados por campaña con su fecha y volumen de agua."
      service={riegos}
      columns={columns}
      fields={fields}
      entityLabel="riego"
      entityLabelPlural="riegos"
      newLabel="Nuevo"
      searchPlaceholder="Buscar riego…"
      defaultValues={{ idCampana: '', fechaRiego: '', cantidadM3: '', observacion: '' }}
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
