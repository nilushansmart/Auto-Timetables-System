import React from 'react';

export const Table = ({ children, className = '' }) => {
  return (
    <div className={`overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-xs ${className}`}>
      <table className="w-full text-left text-sm text-slate-600 border-collapse">
        {children}
      </table>
    </div>
  );
};

export const TableHeader = ({ children, className = '' }) => {
  return (
    <thead className={`bg-slate-50/90 text-xs uppercase tracking-wider text-slate-500 border-b border-slate-200 font-semibold ${className}`}>
      {children}
    </thead>
  );
};

export const TableBody = ({ children, className = '' }) => {
  return <tbody className={`divide-y divide-slate-100 ${className}`}>{children}</tbody>;
};

export const TableRow = ({ children, className = '', onClick }) => {
  return (
    <tr
      onClick={onClick}
      className={`hover:bg-slate-50/80 transition-colors ${onClick ? 'cursor-pointer' : ''} ${className}`}
    >
      {children}
    </tr>
  );
};

export const TableHead = ({ children, className = '' }) => {
  return <th className={`px-4 py-3.5 font-semibold text-slate-700 whitespace-nowrap ${className}`}>{children}</th>;
};

export const TableCell = ({ children, className = '' }) => {
  return <td className={`px-4 py-3.5 align-middle ${className}`}>{children}</td>;
};

export default Table;
