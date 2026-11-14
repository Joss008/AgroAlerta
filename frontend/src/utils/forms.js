export function isFieldEmpty(value) {
  return value == null || value === '';
}

export function validateFields(fields, values) {
  const errors = {};

  for (const field of fields) {
    const value = values[field.name];

    if (isFieldEmpty(value)) {
      if (field.required) errors[field.name] = 'Este campo es obligatorio.';
      continue;
    }

    if (field.type === 'number') {
      const number = typeof value === 'number' ? value : Number(value);
      if (!Number.isFinite(number)) {
        errors[field.name] = 'Ingrese un número válido.';
        continue;
      }
      if (field.positive && number <= 0) {
        errors[field.name] = field.positiveMessage || 'El valor debe ser mayor que 0.';
        continue;
      }
      if (field.min != null && number < field.min) {
        errors[field.name] =
          field.minMessage || `El valor debe ser mayor o igual a ${field.min}.`;
        continue;
      }
      if (field.max != null && number > field.max) {
        errors[field.name] =
          field.maxMessage || `El valor debe ser menor o igual a ${field.max}.`;
        continue;
      }
    }

    if (field.type === 'text' || field.type === 'textarea') {
      const text = String(value);
      if (field.maxLength && text.length > field.maxLength) {
        errors[field.name] = `Máximo ${field.maxLength} caracteres.`;
        continue;
      }
      if (field.pattern && !field.pattern.test(text)) {
        errors[field.name] = field.patternMessage || 'El formato no es válido.';
        continue;
      }
    }

    if (typeof field.validate === 'function') {
      const message = field.validate(value, values);
      if (message) errors[field.name] = message;
    }
  }

  return errors;
}

export function mergeErrors(...errorObjects) {
  return Object.assign({}, ...errorObjects.filter(Boolean));
}

export function buildPayload(fields, values) {
  const payload = {};

  for (const field of fields) {
    const value = values[field.name];

    if (field.type === 'number') {
      payload[field.name] = isFieldEmpty(value) ? null : Number(value);
    } else if (field.type === 'date') {
      payload[field.name] = isFieldEmpty(value) ? null : value;
    } else if (field.type === 'select') {
      if (isFieldEmpty(value)) {
        payload[field.name] = null;
      } else if (field.numeric === false) {
        payload[field.name] = value;
      } else {
        payload[field.name] = Number(value);
      }
    } else {
      payload[field.name] = value == null ? '' : String(value).trim();
    }
  }

  return payload;
}
