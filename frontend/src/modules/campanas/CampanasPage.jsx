import { useMemo } from 'react';
import CrudPage from '../../components/common/CrudPage';
import Badge from '../../components/common/Badge';
import StatusMark from '../../components/motion/StatusMark';
import campanas from '../../services/campanas';
import parcelas from '../../services/parcelas';
import cultivos from '../../services/cultivos';
import useSelectOptions from '../../hooks/useSelectOptions';
import { DASH, formatDate } from '../../utils/format';
import getCampanasFields, { validateCampanas } from './CampanasForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  { key: 'nombre', header: 'Campaña' },
  {
    key: 'nombreParcela',
    header: 'Parcela',
    render: (row) => row.nombreParcela || DASH,
  },
  {
    key: 'nombreCultivo',
    header: 'Cultivo',
    render: (row) => row.nombreCultivo || DASH,
  },
  {
    key: 'fechaInicio',
    header: 'Inicio',
    render: (row) => formatDate(row.fechaInicio),
  },
  {
    key: 'fechaFin',
    header: 'Fin',
    render: (row) => (row.fechaFin ? formatDate(row.fechaFin) : DASH),
  },
  {
    key: 'estado',
    header: 'Estado',
    render: (row) =>
      row.fechaFin ? (
        <Badge variant="default">
          <StatusMark
            status="done"
            label="Finalizada"
            size={14}
            fontSize={12}
            strokeWidth={2.2}
            strike={false}
            color="#57534e"
            doneColor="#57534e"
          />
        </Badge>
      ) : (
        <Badge variant="success">
          <StatusMark
            status="running"
            label="En curso"
            size={14}
            fontSize={12}
            strokeWidth={2.2}
            strike={false}
            color="#047857"
          />
        </Badge>
      ),
  },
];

export default function CampanasPage() {
  const parcelasOptions = useSelectOptions(parcelas.list, (row) => ({
    value: row.id,
    label: row.nombreAgricultor ? `${row.nombre} · ${row.nombreAgricultor}` : row.nombre,
    image: row.fotoUrl || '',
    subtitle: row.areaHectareas != null ? `${row.areaHectareas} ha` : undefined,
  }));

  const cultivosOptions = useSelectOptions(cultivos.list, (row) => ({
    value: row.id,
    label: row.variedad ? `${row.nombre} · ${row.variedad}` : row.nombre,
    image: row.fotoUrl || '',
    subtitle: row.variedad ? `Variedad: ${row.variedad}` : undefined,
  }));

  const fields = useMemo(
    () => getCampanasFields({ parcelas: parcelasOptions, cultivos: cultivosOptions }),
    [parcelasOptions, cultivosOptions],
  );

  return (
    <CrudPage
      title="Campañas"
      description="Ciclos agrícolas que vinculan una parcela con un cultivo y su período de siembra."
      service={campanas}
      columns={columns}
      fields={fields}
      entityLabel="campaña"
      entityLabelPlural="campañas"
      newLabel="Nueva"
      searchPlaceholder="Buscar campaña…"
      defaultValues={{
        nombre: '',
        idParcela: '',
        idCultivo: '',
        fechaInicio: '',
        fechaFin: '',
      }}
      validate={validateCampanas}
      filters={[
        {
          name: 'idParcela',
          placeholder: 'Todas las parcelas',
          ariaLabel: 'Filtrar por parcela',
          options: parcelasOptions.options,
          loading: parcelasOptions.loading,
          error: parcelasOptions.error,
        },
        {
          name: 'idCultivo',
          placeholder: 'Todos los cultivos',
          ariaLabel: 'Filtrar por cultivo',
          options: cultivosOptions.options,
          loading: cultivosOptions.loading,
          error: cultivosOptions.error,
        },
      ]}
    />
  );
}
