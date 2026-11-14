import { useCallback, useEffect, useRef, useState } from 'react';

const OPTION_PAGE_SIZE = 1000;

export default function useSelectOptions(listFn, mapOption, params = {}) {
  const listRef = useRef(listFn);
  listRef.current = listFn;

  const mapRef = useRef(mapOption);
  mapRef.current = mapOption;

  const paramsRef = useRef(params);
  paramsRef.current = params;
  const paramsKey = JSON.stringify(params);

  const [state, setState] = useState({
    rows: [],
    options: [],
    loading: true,
    error: null,
  });
  const [nonce, setNonce] = useState(0);

  useEffect(() => {
    let cancelled = false;
    setState((current) => ({ ...current, loading: true, error: null }));

    (async () => {
      try {
        const response = await listRef.current({
          page: 0,
          size: OPTION_PAGE_SIZE,
          ...paramsRef.current,
        });
        if (cancelled) return;
        const content = response.data?.content;
        const rows = Array.isArray(content) ? content : [];
        setState({
          rows,
          options: rows.map((row) => mapRef.current(row)),
          loading: false,
          error: null,
        });
      } catch (error) {
        if (cancelled) return;
        setState((current) => ({ ...current, loading: false, error }));
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [paramsKey, nonce]);

  const reload = useCallback(() => setNonce((value) => value + 1), []);

  return { ...state, reload };
}
