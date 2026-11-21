import React, { useState, useEffect } from 'react';
import PageHeader from '../../components/layout/PageHeader';
import Button from '../../components/common/Button';
import Table, { TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import Input from '../../components/common/Input';
import Modal from '../../components/common/Modal';
import Spinner from '../../components/common/Spinner';
import EmptyState from '../../components/common/EmptyState';
import { useToast } from '../../hooks/useToast';
import { Plus, Calendar, CheckCircle2 } from 'lucide-react';
import academicService from '../../services/academicService';

export const AcademicYearsPage = () => {
  const [academicYears, setAcademicYears] = useState([]);
  const [terms, setTerms] = useState([]);
  const [loading, setLoading] = useState(true);

  // Year Modal
  const [yearModalOpen, setYearModalOpen] = useState(false);
  const [yearFormData, setYearFormData] = useState({
    year: '2026',
    startDate: '2026-01-01',
    endDate: '2026-12-31',
    active: true,
  });

  // Term Modal
  const [termModalOpen, setTermModalOpen] = useState(false);
  const [termFormData, setTermFormData] = useState({
    academicYearId: '',
    termNo: '1',
    startDate: '2026-01-05',
    endDate: '2026-04-10',
  });

  const [isSaving, setIsSaving] = useState(false);
  const toast = useToast();

  const fetchData = async () => {
    setLoading(true);
    try {
      const [ayData, tData] = await Promise.all([
        academicService.getAcademicYears(),
        academicService.getTerms({ size: 100 }),
      ]);
      setAcademicYears(ayData.content || []);
      setTerms(tData.content || []);
    } catch (err) {
      toast.error('Failed to load academic years');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateYear = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    try {
      await academicService.createAcademicYear({
        year: Number(yearFormData.year),
        startDate: yearFormData.startDate,
        endDate: yearFormData.endDate,
        active: yearFormData.active,
      });
      toast.success('Academic year created');
      setYearModalOpen(false);
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create academic year');
    } finally {
      setIsSaving(false);
    }
  };

  const handleCreateTerm = async (e) => {
    e.preventDefault();
    if (!termFormData.academicYearId) return;
    setIsSaving(true);
    try {
      await academicService.createTerm({
        academicYearId: Number(termFormData.academicYearId),
        termNo: Number(termFormData.termNo),
        startDate: termFormData.startDate,
        endDate: termFormData.endDate,
      });
      toast.success('Term created');
      setTermModalOpen(false);
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create term');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-8">
      <PageHeader
        title="Academic Years & School Terms"
        subtitle="Manage calendar school years and term date intervals."
      >
        <div className="flex items-center gap-2">
          <Button variant="outline" icon={Plus} onClick={() => setTermModalOpen(true)}>
            Add Term
          </Button>
          <Button variant="primary" icon={Plus} onClick={() => setYearModalOpen(true)}>
            Add Academic Year
          </Button>
        </div>
      </PageHeader>

      {/* Academic Years Section */}
      <div className="space-y-3">
        <h3 className="text-sm font-bold uppercase tracking-wider text-slate-500">
          Academic Years
        </h3>
        {loading ? (
          <Spinner />
        ) : (
          <Table>
            <TableHeader>
              <tr>
                <TableHead>Year</TableHead>
                <TableHead>Start Date</TableHead>
                <TableHead>End Date</TableHead>
                <TableHead>Status</TableHead>
              </tr>
            </TableHeader>
            <TableBody>
              {academicYears.map((ay) => (
                <TableRow key={ay.id}>
                  <TableCell className="font-bold text-slate-900 text-base">
                    {ay.year}
                  </TableCell>
                  <TableCell>{ay.startDate}</TableCell>
                  <TableCell>{ay.endDate}</TableCell>
                  <TableCell>
                    <Badge variant={ay.active ? 'emerald' : 'slate'}>
                      {ay.active ? 'Active Year' : 'Archived'}
                    </Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </div>

      {/* Terms Section */}
      <div className="space-y-3 pt-4 border-t border-slate-200">
        <h3 className="text-sm font-bold uppercase tracking-wider text-slate-500">
          School Terms ({terms.length})
        </h3>
        <Table>
          <TableHeader>
            <tr>
              <TableHead>Academic Year</TableHead>
              <TableHead>Term Number</TableHead>
              <TableHead>Start Date</TableHead>
              <TableHead>End Date</TableHead>
            </tr>
          </TableHeader>
          <TableBody>
            {terms.map((term) => (
              <TableRow key={term.id}>
                <TableCell className="font-bold text-slate-800">
                  Year ID: {term.academicYearId}
                </TableCell>
                <TableCell>
                  <Badge variant="indigo">Term {term.termNo}</Badge>
                </TableCell>
                <TableCell>{term.startDate}</TableCell>
                <TableCell>{term.endDate}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* Year Modal */}
      <Modal
        isOpen={yearModalOpen}
        onClose={() => setYearModalOpen(false)}
        title="Add Academic Year"
        description="Record a new school academic year calendar."
      >
        <form onSubmit={handleCreateYear} className="space-y-4">
          <Input
            label="Year"
            type="number"
            min="2020"
            max="2040"
            value={yearFormData.year}
            onChange={(e) => setYearFormData({ ...yearFormData, year: e.target.value })}
            required
          />
          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Start Date"
              type="date"
              value={yearFormData.startDate}
              onChange={(e) => setYearFormData({ ...yearFormData, startDate: e.target.value })}
              required
            />
            <Input
              label="End Date"
              type="date"
              value={yearFormData.endDate}
              onChange={(e) => setYearFormData({ ...yearFormData, endDate: e.target.value })}
              required
            />
          </div>
          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="active-ay"
              checked={yearFormData.active}
              onChange={(e) => setYearFormData({ ...yearFormData, active: e.target.checked })}
              className="w-4 h-4 rounded text-indigo-600 focus:ring-indigo-500"
            />
            <label htmlFor="active-ay" className="text-sm font-medium text-slate-700">
              Set as active academic year
            </label>
          </div>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" onClick={() => setYearModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" isLoading={isSaving}>
              Create Year
            </Button>
          </div>
        </form>
      </Modal>

      {/* Term Modal */}
      <Modal
        isOpen={termModalOpen}
        onClose={() => setTermModalOpen(false)}
        title="Add School Term"
        description="Configure term dates for an academic year."
      >
        <form onSubmit={handleCreateTerm} className="space-y-4">
          <Select
            label="Academic Year"
            value={termFormData.academicYearId}
            onChange={(e) => setTermFormData({ ...termFormData, academicYearId: e.target.value })}
            options={academicYears.map((ay) => ({
              value: String(ay.id),
              label: `Year ${ay.year} ${ay.active ? '(Active)' : ''}`,
            }))}
            placeholder="Select Academic Year"
            required
          />
          <Select
            label="Term Number"
            value={termFormData.termNo}
            onChange={(e) => setTermFormData({ ...termFormData, termNo: e.target.value })}
            options={[
              { value: '1', label: 'Term 1' },
              { value: '2', label: 'Term 2' },
              { value: '3', label: 'Term 3' },
            ]}
            required
          />
          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Start Date"
              type="date"
              value={termFormData.startDate}
              onChange={(e) => setTermFormData({ ...termFormData, startDate: e.target.value })}
              required
            />
            <Input
              label="End Date"
              type="date"
              value={termFormData.endDate}
              onChange={(e) => setTermFormData({ ...termFormData, endDate: e.target.value })}
              required
            />
          </div>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-100">
            <Button variant="outline" onClick={() => setTermModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" isLoading={isSaving}>
              Create Term
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default AcademicYearsPage;
