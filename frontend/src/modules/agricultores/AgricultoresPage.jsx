import CrudPage from '../../components/common/CrudPage';
import Thumbnail from '../../components/common/Thumbnail';
import agricultores from '../../services/agricultores';
import { DASH } from '../../utils/format';
import { agricultoresFields, validateAgricultores } from './AgricultoresForm';

const columns = [
  { key: 'id', header: 'Código', align: 'right', className: 'text-stone-500', render: (row) => `#${row.id}` },
  {
    key: 'fotoUrl',
    header: 'Agricultor',
    render: (row) => (
      <div className="flex items-center gap-2.5">
        <Thumbnail
          src={row.fotoUrl}
          alt={`${row.nombres} ${row.apellidos}`.trim()}
          size="sm"
        />
        <span className="font-medium text-stone-800">
          {`${row.nombres} ${row.apellidos}`.trim()}
        </span>
      </div>
    ),
  },
  {
    key: 'dni',
    header: 'DNI',
    render: (row) => row.dni || DASH,
  },
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
      defaultValues={{ fotoUrl: '', dni: '', nombres: '', apellidos: '', telefono: '' }}
      validate={validateAgricultores}
    />
  );
}
