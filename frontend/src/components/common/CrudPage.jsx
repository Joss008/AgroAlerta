import { useEffect, useMemo, useState } from 'react';
import { Loader2, Pencil, Plus, Search, Trash2 } from 'lucide-react';
import PageHeader from './PageHeader';
import Modal from './Modal';
import ConfirmDialog from './ConfirmDialog';
import ErrorAlert from './ErrorAlert';
import Loading from './Loading';
import EntityForm from '../forms/EntityForm';
import { Select } from '../forms/inputs';
import DataTable from '../tables/DataTable';
import { useToast } from '../../contexts/ToastContext';
import useDebouncedValue from '../../hooks/useDebouncedValue';
import usePaginatedFetch from '../../hooks/usePaginatedFetch';
import { getApiFieldErrors, getApiMessage } from '../../utils/errors';
import { buildPayload, mergeErrors, validateFields } from '../../utils/forms';

const PAGE_SIZE = 10;

const iconButtonClasses =
  'rounded-lg p-1.5 text-stone-500 transition hover:bg-stone-100 hover:text-stone-800 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600 focus-visible:ring-offset-1';

export default function CrudPage({
  title,
  description,
  service,
  columns,
  fields,
  entityLabel = 'registro',
  entityLabelPlural = 'registros',
  newLabel = 'Nuevo',
  searchPlaceholder = 'Buscar…',
  defaultValues = {},
  validate,
  listParams = {},
  filters = [],
}) {
  const toast = useToast();

  const [search, setSearch] = useState('');
  const debouncedSearch = useDebouncedValue(search, 300);
  const [page, setPage] = useState(0);
  const [filterValues, setFilterValues] = useState({});

  const handleSearchChange = (value) => {
    setSearch(value);
    setPage(0);
  };

  const handleFilterChange = (name, value) => {
    setFilterValues((current) => ({ ...current, [name]: value }));
    setPage(0);
  };

  const clearFilters = () => {
    setFilterValues({});
    setPage(0);
  };

  const params = useMemo(() => {
    const merged = { ...listParams };
    for (const filter of filters) {
      const value = filterValues[filter.name];
      if (value == null || value === '') continue;
      merged[filter.name] = filter.numeric === false ? value : Number(value);
    }
    if (debouncedSearch) merged.q = debouncedSearch;
    return merged;
  }, [listParams, filters, filterValues, debouncedSearch]);

  const list = usePaginatedFetch(service.list, { page, size: PAGE_SIZE, params });

  useEffect(() => {
    if (!list.loading && list.totalPages > 0 && page > list.totalPages - 1) {
      setPage(list.totalPages - 1);
    }
  }, [list.loading, list.totalPages, page]);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [values, setValues] = useState(defaultValues);
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState(null);

  const [deleteTarget, setDeleteTarget] = useState(null);

  const closeModal = () => {
    if (submitting) return;
    setModalOpen(false);
    setEditing(null);
    setFieldErrors({});
    setFormError(null);
    setDetailError(null);
  };

  const openCreate = () => {
    setEditing(null);
    setValues({ ...defaultValues });
    setFieldErrors({});
    setFormError(null);
    setDetailError(null);
    setModalOpen(true);
  };

  const openEdit = async (row) => {
    setEditing(row);
    setValues({ ...defaultValues, ...row });
    setFieldErrors({});
    setFormError(null);
    setDetailError(null);
    setModalOpen(true);
    setDetailLoading(true);
    try {
      const response = await service.get(row.id);
      const data = response.data ?? row;
      setEditing(data);
      setValues({ ...defaultValues, ...data });
    } catch (error) {
      setDetailError(getApiMessage(error));
    } finally {
      setDetailLoading(false);
    }
  };

  const handleChange = (name, value) => {
    setValues((current) => ({ ...current, [name]: value }));
    setFieldErrors((current) => {
      if (!current[name]) return current;
      const next = { ...current };
      delete next[name];
      return next;
    });
    setFormError(null);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setFormError(null);

    const clientErrors = validateFields(fields, values);
    const customErrors = validate?.(values) ?? {};
    const errors = mergeErrors(clientErrors, customErrors);

    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    const payload = buildPayload(fields, values);
    setSubmitting(true);
    try {
      if (editing) {
        await service.update(editing.id, payload);
        toast.success('Los cambios se guardaron correctamente.');
      } else {
        await service.create(payload);
        toast.success(`Se registró el ${entityLabel} correctamente.`);
      }
      setModalOpen(false);
      setEditing(null);
      list.reload();
    } catch (error) {
      const backendFieldErrors = getApiFieldErrors(error);
      if (Object.keys(backendFieldErrors).length > 0) {
        setFieldErrors(backendFieldErrors);
      } else {
        setFormError(getApiMessage(error));
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    await service.remove(deleteTarget.id);
    toast.success('El registro se eliminó correctamente.');
    setDeleteTarget(null);
    if (list.items.length === 1 && page > 0) {
      setPage(page - 1);
    }
    list.reload();
  };

  const tableColumns = useMemo(
    () => [
      ...columns,
      {
        key: '_actions',
        header: 'Acciones',
        align: 'right',
        headerClassName: 'w-28',
        render: (row) => (
          <div className="flex items-center justify-end gap-1">
            <button
              type="button"
              className={iconButtonClasses}
              onClick={() => openEdit(row)}
              title="Editar"
            >
              <Pencil size={15} aria-hidden="true" />
              <span className="sr-only">Editar registro</span>
            </button>
            <button
              type="button"
              className={`${iconButtonClasses} hover:bg-rose-50 hover:text-rose-600`}
              onClick={() => setDeleteTarget(row)}
              title="Eliminar"
            >
              <Trash2 size={15} aria-hidden="true" />
              <span className="sr-only">Eliminar registro</span>
            </button>
          </div>
        ),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [columns, entityLabel],
  );

  const searching = debouncedSearch.trim().length > 0;

  return (
    <div>
      <PageHeader
        title={title}
        description={description}
        actions={
          <button
            type="button"
            onClick={openCreate}
            className="inline-flex items-center justify-center gap-1.5 rounded-lg bg-emerald-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-600 focus-visible:ring-offset-2"
          >
            <Plus size={16} aria-hidden="true" />
            {newLabel}
          </button>
        }
      />

      <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-1 flex-wrap items-center gap-3">
          <div className="relative w-full sm:max-w-xs">
            <Search
              size={16}
              className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-stone-400"
              aria-hidden="true"
            />
            <input
              type="search"
              value={search}
              onChange={(event) => handleSearchChange(event.target.value)}
              placeholder={searchPlaceholder}
              aria-label="Buscar"
              className="block w-full rounded-lg border border-stone-300 bg-white py-2 pl-9 pr-3 text-sm text-stone-900 placeholder:text-stone-500 transition focus:border-emerald-500 focus:outline-none focus:ring-2 focus:ring-emerald-600/15"
            />
          </div>
          {filters.map((filter) => {
            const value = filterValues[filter.name] ?? '';
            return (
              <div key={filter.name} className="w-full sm:w-56">
                <Select
                  value={String(value)}
                  disabled={Boolean(filter.loading)}
                  aria-label={filter.ariaLabel || filter.placeholder}
                  onChange={(event) => {
                    handleFilterChange(filter.name, event.target.value);
                  }}
                >
                  <option value="">
                    {filter.loading
                      ? 'Cargando opciones…'
                      : (filter.placeholder || 'Sin filtro')}
                  </option>
                  {(filter.options || []).map((option) => (
                    <option key={String(option.value)} value={String(option.value)}>
                      {option.label}
                    </option>
                  ))}
                </Select>
                {filter.error ? (
                  <p className="mt-1 text-xs font-medium text-rose-600">
                    No se pudieron cargar las opciones.
                  </p>
                ) : null}
              </div>
            );
          })}
          {filters.some((filter) => filterValues[filter.name]) ? (
            <button
              type="button"
              onClick={clearFilters}
              className="text-sm font-medium text-emerald-700 transition hover:text-emerald-800"
            >
              Limpiar filtros
            </button>
          ) : null}
        </div>
        {list.loading && list.items.length > 0 ? (
          <p className="shrink-0 text-xs text-stone-500">Actualizando…</p>
        ) : null}
      </div>

      <DataTable
        columns={tableColumns}
        rows={list.items}
        loading={list.loading}
        error={list.error}
        onRetry={list.reload}
        emptyTitle={searching ? 'Sin resultados' : `No hay ${entityLabelPlural}`}
        emptyDescription={
          searching
            ? `Ningún registro coincide con «${debouncedSearch}». Pruebe con otro término de búsqueda.`
            : `Todavía no hay ${entityLabelPlural} registrados. Cree el primero con el botón «${newLabel}».`
        }
        page={page}
        totalPages={list.totalPages}
        totalElements={list.totalElements}
        size={PAGE_SIZE}
        onPageChange={setPage}
      />

      <Modal
        open={modalOpen}
        onClose={closeModal}
        title={editing ? `Editar ${entityLabel}` : `Nuevo ${entityLabel}`}
        description={
          editing
            ? 'Modifique los campos necesarios y guarde los cambios.'
            : `Complete los datos para registrar un nuevo ${entityLabel}.`
        }
        busy={submitting || detailLoading}
        closeOnBackdrop={!submitting}
        footer={
          <>
            <button
              type="button"
              onClick={closeModal}
              disabled={submitting}
              className="rounded-lg border border-stone-300 bg-white px-4 py-2 text-sm font-medium text-stone-700 transition hover:bg-stone-50 disabled:cursor-not-allowed disabled:opacity-60"
            >
              Cancelar
            </button>
            <button
              type="submit"
              form="crud-entity-form"
              disabled={submitting || detailLoading}
              className="inline-flex items-center justify-center gap-2 rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {submitting ? (
                <Loader2 size={15} className="animate-spin" aria-hidden="true" />
              ) : null}
              {submitting ? 'Guardando…' : 'Guardar'}
            </button>
          </>
        }
      >
        {detailLoading ? (
          <Loading label="Cargando registro…" />
        ) : detailError ? (
          <div className="space-y-4">
            <ErrorAlert
              message={detailError}
              onRetry={() => {
                if (editing) openEdit(editing);
              }}
            />
          </div>
        ) : (
          <form id="crud-entity-form" onSubmit={handleSubmit} noValidate>
            <div className="space-y-4">
              {formError ? <ErrorAlert message={formError} /> : null}
              <EntityForm
                fields={fields}
                values={values}
                errors={fieldErrors}
                onChange={handleChange}
                disabled={submitting}
              />
            </div>
          </form>
        )}
      </Modal>

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title={`Eliminar ${entityLabel}`}
        message={`Esta acción no se puede deshacer. ¿Desea eliminar definitivamente este ${entityLabel}?`}
        onConfirm={handleDelete}
        onClose={() => setDeleteTarget(null)}
      />
    </div>
  );
}
