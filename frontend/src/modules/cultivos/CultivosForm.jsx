export const cultivosFields = [
  {
    name: 'fotoUrl',
    label: 'Foto del cultivo',
    type: 'image',
    hint: 'Opcional. Se mostrará en miniatura al crear campañas.',
  },
  {
    name: 'nombre',
    label: 'Nombre del cultivo',
    type: 'text',
    required: true,
    maxLength: 100,
    placeholder: 'Ej. Espárrago',
  },
  {
    name: 'variedad',
    label: 'Variedad',
    type: 'text',
    maxLength: 100,
    placeholder: 'Ej. Violet de Lucerna',
    hint: 'Opcional.',
  },
];
