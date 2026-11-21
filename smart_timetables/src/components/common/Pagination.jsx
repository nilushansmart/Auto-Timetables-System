import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import Button from './Button';

export const Pagination = ({
  page = 0,
  totalPages = 0,
  totalElements = 0,
  size = 10,
  onPageChange,
  className = '',
}) => {
  if (totalPages <= 1 && totalElements <= size) return null;

  const start = totalElements === 0 ? 0 : page * size + 1;
  const end = Math.min((page + 1) * size, totalElements);

  return (
    <div className={`flex flex-col sm:flex-row items-center justify-between gap-3 pt-4 text-sm text-slate-500 ${className}`}>
      <div>
        Showing <span className="font-semibold text-slate-800">{start}</span> to{' '}
        <span className="font-semibold text-slate-800">{end}</span> of{' '}
        <span className="font-semibold text-slate-800">{totalElements}</span> items
      </div>

      <div className="flex items-center gap-1.5">
        <Button
          variant="outline"
          size="sm"
          disabled={page <= 0}
          onClick={() => onPageChange(page - 1)}
          icon={ChevronLeft}
        >
          Previous
        </Button>
        <span className="px-3 text-xs font-medium text-slate-600">
          Page {page + 1} of {Math.max(totalPages, 1)}
        </span>
        <Button
          variant="outline"
          size="sm"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          Next
          <ChevronRight className="w-4 h-4 ml-1" />
        </Button>
      </div>
    </div>
  );
};

export default Pagination;
