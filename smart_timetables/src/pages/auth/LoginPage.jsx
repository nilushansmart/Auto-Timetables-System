import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../hooks/useToast';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import { CalendarDays, ShieldCheck, Sparkles, School } from 'lucide-react';
import { motion } from 'motion/react';

export const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/timetable';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const user = await login(email, password);
      toast.success(`Welcome back, ${user.fullName}!`);
      navigate(from, { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message || 'Invalid email or password';
      setError(msg);
      toast.error(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFillDemo = () => {
    setEmail('admin@demoschool.lk');
    setPassword('admin123');
    setError('');
  };

  return (
    <div className="min-h-screen bg-linear-to-br from-indigo-900 via-slate-900 to-slate-950 flex flex-col justify-center py-12 sm:px-6 lg:px-8 text-slate-100 relative overflow-hidden">
      {/* Decorative ambient gradients */}
      <div className="absolute top-0 -left-40 w-96 h-96 bg-indigo-600/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-0 -right-40 w-96 h-96 bg-purple-600/20 rounded-full blur-3xl pointer-events-none" />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
        className="sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0"
      >
        <div className="flex justify-center mb-4">
          <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white flex items-center justify-center shadow-xl shadow-indigo-500/25 border border-indigo-400/30">
            <CalendarDays className="w-8 h-8" />
          </div>
        </div>
        <h2 className="text-center text-3xl font-black tracking-tight text-white">
          Smart Timetables
        </h2>
        <p className="mt-2 text-center text-sm text-slate-400">
          Multi-School Timetable Management System
        </p>
      </motion.div>

      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.3, delay: 0.1 }}
        className="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0"
      >
        <div className="bg-white/95 text-slate-900 py-8 px-6 shadow-2xl rounded-3xl border border-white/20 backdrop-blur-xl sm:px-10">
          {error && (
            <div className="mb-5 p-3 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-xs font-semibold flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-rose-500 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Email Address"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="e.g. admin@demoschool.lk"
              required
              autoFocus
            />

            <Input
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />

            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full mt-2"
              isLoading={isLoading}
            >
              Sign In to Dashboard
            </Button>
          </form>

          <div className="mt-6 pt-6 border-t border-slate-100">
            <div className="text-xs text-slate-500 mb-3 text-center font-medium">
              Quick Demonstration Access
            </div>
            <button
              type="button"
              onClick={handleFillDemo}
              className="w-full flex items-center justify-center gap-2 p-2.5 rounded-xl border border-indigo-200 bg-indigo-50/70 text-indigo-700 hover:bg-indigo-100 text-xs font-bold transition-all cursor-pointer"
            >
              <Sparkles className="w-4 h-4 text-indigo-600" />
              Use Demo Admin Account (admin@demoschool.lk)
            </button>
          </div>
        </div>

        <div className="mt-6 text-center text-xs text-slate-500 flex items-center justify-center gap-1.5">
          <School className="w-3.5 h-3.5" />
          <span>Tenant row-level security enabled & enforced by PostgreSQL</span>
        </div>
      </motion.div>
    </div>
  );
};

export default LoginPage;
