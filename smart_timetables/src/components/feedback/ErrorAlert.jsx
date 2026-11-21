import React from 'react';
import { AlertCircle, RefreshCw } from 'lucide-react';
import Button from '../common/Button';

export const ErrorAlert = ({
  message = 'An error occurred while loading data.',
  onRetry,
  className = '',
}) => {
  return (
    <div
      className={`flex items-start gap-3 p-4 rounded-xl border border-rose-200 bg-rose-50 text-rose-900 ${className}`}
      role="alert"
    >
      <AlertCircle className="w-5 h-5 text-rose-500 shrink-0 mt-0.5" />
      <div className="flex-1 text-sm font-medium">
        <p>{message}</p>
      </div>
      {onRetry && (
        <Button
          variant="outline"
          size="sm"
          onClick={onRetry}
          icon={RefreshCw}
          className="border-rose-300 text-rose-700 hover:bg-rose-100 bg-white"
        >
          Retry
        </Button>
      )}
    </div>
  );
};

export default ErrorAlert;
