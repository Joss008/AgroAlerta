const OFFLINE_MESSAGE =
  'No se pudo conectar con el servidor. Verifique que el backend esté disponible e inténtelo de nuevo.';

export function getErrorData(error) {
  return error?.response?.data ?? null;
}

export function getApiMessage(error) {
  const data = getErrorData(error);
  if (data && typeof data.message === 'string' && data.message.trim()) {
    return data.message;
  }
  if (error?.code === 'ERR_NETWORK' || !error?.response) {
    return OFFLINE_MESSAGE;
  }
  if (error.response.status >= 500) {
    return 'El servidor encontró un error al procesar la solicitud. Inténtelo de nuevo.';
  }
  return 'La solicitud no se pudo completar. Inténtelo de nuevo.';
}

export function getApiFieldErrors(error) {
  const fieldErrors = getErrorData(error)?.fieldErrors;
  if (!Array.isArray(fieldErrors)) return {};
  const mapped = {};
  for (const item of fieldErrors) {
    if (item?.field) mapped[item.field] = item.message || 'Valor no válido.';
  }
  return mapped;
}

export function isNotFoundError(error) {
  return error?.response?.status === 404;
}
