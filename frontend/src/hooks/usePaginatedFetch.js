import { useCallback, useEffect, useRef, useState } from 'react';

export default function usePaginatedFetch(fetcher, { page = 0, size = 10, params = {} } = {}) {
  const fetcherRef = useRef(fetcher);
  fetcherRef.current = fetcher;

  const paramsRef = useRef(params);
  paramsRef.current = params;
  const paramsKey = JSON.stringify(params);

  const [state, setState] = useState({
    items: [],
    totalElements: 0,
    totalPages: 0,
    loading: true,
    error: null,
  });
  const [nonce, setNonce] = useState(0);

  useEffect(() => {
    let cancelled = false;
    setState((current) => ({ ...current, loading: true, error: null }));

    (async () => {
      try {
        const response = await fetcherRef.current({ page, size, ...paramsRef.current });
        if (cancelled) return;
        const data = response.data ?? {};
        setState({
          items: Array.isArray(data.content) ? data.content : [],
          totalElements: data.totalElements ?? 0,
          totalPages: data.totalPages ?? 0,
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
  }, [page, size, paramsKey, nonce]);

  const reload = useCallback(() => setNonce((value) => value + 1), []);

  return { ...state, reload };
}
