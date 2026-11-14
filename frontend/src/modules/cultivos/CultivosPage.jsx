import CrudPage from '../../components/common/CrudPage';
import cultivos from '../../services/cultivos';
import { DASH } from '../../utils/format';
import { cultivosFields } from './CultivosForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  { key: 'nombre', header: 'Cultivo' },
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
      defaultValues={{ nombre: '', variedad: '' }}
    />
  );
}
