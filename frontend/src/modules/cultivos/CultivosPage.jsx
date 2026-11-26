import CrudPage from '../../components/common/CrudPage';
import Thumbnail from '../../components/common/Thumbnail';
import cultivos from '../../services/cultivos';
import { DASH } from '../../utils/format';
import { cultivosFields } from './CultivosForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'nombre',
    header: 'Cultivo',
    render: (row) => (
      <div className="flex items-center gap-2.5">
        <Thumbnail src={row.fotoUrl} alt={row.nombre} size="sm" rounded="lg" />
        <span className="font-medium text-stone-800">{row.nombre}</span>
      </div>
    ),
  },
  {
    key: 'variedad',
    header: 'Variedad',
    render: (row) => row.variedad || DASH,
  },
];

export default function CultivosPage() {
  return (
    <CrudPage
      title="Cultivos"
      description="Catálogo de cultivos y variedades que se siembran en las parcelas."
      service={cultivos}
      columns={columns}
      fields={cultivosFields}
      entityLabel="cultivo"
      entityLabelPlural="cultivos"
      newLabel="Nuevo"
      searchPlaceholder="Buscar cultivo…"
      defaultValues={{ fotoUrl: '', nombre: '', variedad: '' }}
    />
  );
}
