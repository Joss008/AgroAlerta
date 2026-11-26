import { useMemo } from 'react';
import CrudPage from '../../components/common/CrudPage';
import Thumbnail from '../../components/common/Thumbnail';
import parcelas from '../../services/parcelas';
import agricultores from '../../services/agricultores';
import useSelectOptions from '../../hooks/useSelectOptions';
import { DASH, formatHectares } from '../../utils/format';
import getParcelasFields from './ParcelasForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  { key: 'nombre', header: 'Parcela' },
  {
    key: 'nombreAgricultor',
    header: 'Agricultor',
    render: (row) =>
      row.nombreAgricultor ? (
        <div className="flex items-center gap-2">
          <Thumbnail
            src={row.fotoAgricultor}
            alt={row.nombreAgricultor}
            size="xs"
          />
          <span>{row.nombreAgricultor}</span>
        </div>
      ) : (
        DASH
      ),
  },
  {
    key: 'areaHectareas',
    header: 'Área',
    align: 'right',
    render: (row) => formatHectares(row.areaHectareas),
  },
  {
    key: 'ubicacion',
    header: 'Ubicación',
    render: (row) => row.ubicacion || DASH,
  },
];

export default function ParcelasPage() {
  const agricultoresOptions = useSelectOptions(agricultores.list, (row) => ({
    value: row.id,
    label: `${row.nombres} ${row.apellidos}`,
    image: row.fotoUrl || '',
    subtitle: row.dni ? `DNI: ${row.dni}` : undefined,
  }));

  const fields = useMemo(
    () => getParcelasFields({ agricultores: agricultoresOptions }),
    [agricultoresOptions],
  );

  return (
    <CrudPage
      title="Parcelas"
      description="Terrenos agrícolas y su superficie, cada uno vinculado a un agricultor."
      service={parcelas}
      columns={columns}
      fields={fields}
      entityLabel="parcela"
      entityLabelPlural="parcelas"
      newLabel="Nueva"
      searchPlaceholder="Buscar por nombre o ubicación…"
      defaultValues={{ idAgricultor: '', nombre: '', areaHectareas: '', ubicacion: '' }}
      filters={[
        {
          name: 'idAgricultor',
          placeholder: 'Todos los agricultores',
          ariaLabel: 'Filtrar por agricultor',
          options: agricultoresOptions.options,
          loading: agricultoresOptions.loading,
          error: agricultoresOptions.error,
        },
      ]}
    />
  );
}
