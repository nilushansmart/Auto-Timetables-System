import React from 'react';

export const PageHeader = ({
  title,
  subtitle,
  children,
  badge,
  className = '',
}) => {
  return (
    <div
      className={`flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 pb-4 border-b border-slate-200/80 ${className}`}
    >
      <div>
        <div className="flex items-center gap-2.5">
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">
            {title}
          </h1>
          {badge}
        </div>
        {subtitle && (
          <p className="text-sm text-slate-500 mt-1">{subtitle}</p>
        )}
      </div>
      {children && (
        <div className="flex items-center gap-2.5 flex-wrap">{children}</div>
      )}
    </div>
  );
};

export default PageHeader;
