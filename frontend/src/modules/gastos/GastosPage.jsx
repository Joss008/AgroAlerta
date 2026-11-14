import { useMemo } from 'react';
import CrudPage from '../../components/common/CrudPage';
import gastos from '../../services/gastos';
import campanas from '../../services/campanas';
import useSelectOptions from '../../hooks/useSelectOptions';
import { DASH, formatDate, formatSoles } from '../../utils/format';
import getGastosFields from './GastosForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'nombreCampana',
    header: 'Campaña',
    render: (row) => row.nombreCampana || DASH,
  },
  {
    key: 'concepto',
    header: 'Concepto',
    render: (row) => row.concepto || DASH,
  },
  {
    key: 'fechaGasto',
    header: 'Fecha',
    render: (row) => formatDate(row.fechaGasto),
  },
  {
    key: 'monto',
    header: 'Monto',
    align: 'right',
    render: (row) => formatSoles(row.monto),
  },
];

export default function GastosPage() {
  const campanasOptions = useSelectOptions(campanas.list, (row) => ({
    value: row.id,
    label: row.nombreParcela ? `${row.nombre} · ${row.nombreParcela}` : row.nombre,
  }));

  const fields = useMemo(
    () => getGastosFields({ campanas: campanasOptions }),
    [campanasOptions],
  );

  return (
    <CrudPage
      title="Gastos"
      description="Inversiones registradas por campaña: insumos, mano de obra y operación."
      service={gastos}
      columns={columns}
      fields={fields}
      entityLabel="gasto"
      entityLabelPlural="gastos"
      newLabel="Nuevo"
      searchPlaceholder="Buscar gasto…"
      defaultValues={{ idCampana: '', concepto: '', fechaGasto: '', monto: '' }}
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
