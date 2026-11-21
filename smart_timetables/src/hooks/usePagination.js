import { useState, useCallback } from 'react';

export const usePagination = (initialSize = 10) => {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(initialSize);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const updatePagination = useCallback((pageData) => {
    if (!pageData) return;
    setTotalElements(pageData.totalElements ?? 0);
    setTotalPages(pageData.totalPages ?? 0);
    setPage(pageData.number ?? 0);
  }, []);

  const reset = useCallback(() => {
    setPage(0);
  }, []);

  return {
    page,
    setPage,
    size,
    setSize,
    totalElements,
    totalPages,
    updatePagination,
    reset,
  };
};

export default usePagination;
