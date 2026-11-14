import { useCallback, useEffect, useRef, useState } from 'react';

export default function useFetch(fetcher) {
  const fetcherRef = useRef(fetcher);
  fetcherRef.current = fetcher;

  const [state, setState] = useState({ data: null, loading: true, error: null });
  const [nonce, setNonce] = useState(0);

  useEffect(() => {
    let cancelled = false;
    setState((current) => ({ ...current, loading: true, error: null }));

    (async () => {
      try {
        const response = await fetcherRef.current();
        if (cancelled) return;
        setState({ data: response.data ?? null, loading: false, error: null });
      } catch (error) {
        if (cancelled) return;
        setState((current) => ({ data: current.data, loading: false, error }));
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [nonce]);

  const reload = useCallback(() => setNonce((value) => value + 1), []);

  return { ...state, reload };
}
