import CrudPage from '../../components/common/CrudPage';
import agricultores from '../../services/agricultores';
import { DASH } from '../../utils/format';
import { agricultoresFields, validateAgricultores } from './AgricultoresForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'dni',
    header: 'DNI',
    render: (row) => row.dni || DASH,
  },
  { key: 'nombres', header: 'Nombres' },
  { key: 'apellidos', header: 'Apellidos' },
  {
    key: 'telefono',
    header: 'Teléfono',
    render: (row) => row.telefono || DASH,
  },
];

export default function AgricultoresPage() {
  return (
    <CrudPage
      title="Agricultores"
      description="Personas responsables de las parcelas registradas en Cañete."
      service={agricultores}
      columns={columns}
      fields={agricultoresFields}
      entityLabel="agricultor"
      entityLabelPlural="agricultores"
      newLabel="Nuevo"
      searchPlaceholder="Buscar por nombre, apellido o DNI…"
      defaultValues={{ dni: '', nombres: '', apellidos: '', telefono: '' }}
      validate={validateAgricultores}
    />
  );
}
