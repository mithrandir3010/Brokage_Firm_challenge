import { useState } from 'react';
import { assetService, customerService } from '../../services/api';
import type { User } from '../../types';
import { useEffect } from 'react';

interface DepositModalProps {
  onClose: () => void;
  onSuccess: () => void;
}

export default function DepositModal({ onClose, onSuccess }: DepositModalProps) {
  const [customerId, setCustomerId] = useState('');
  const [amount, setAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [customers, setCustomers] = useState<User[]>([]);

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        const data = await customerService.getCustomers();
        setCustomers(data);
      } catch (err) {
        console.error('Error fetching customers:', err);
      }
    };
    fetchCustomers();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await assetService.deposit({
        customerId: parseInt(customerId),
        amount: parseFloat(amount),
      });
      onSuccess();
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosError = err as { response?: { data?: { message?: string } } };
        setError(axiosError.response?.data?.message || 'Para yatırma başarısız');
      } else {
        setError('Bağlantı hatası');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl p-6 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4">Para Yatır</h2>
        
        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Müşteri
            </label>
            <select
              value={customerId}
              onChange={(e) => setCustomerId(e.target.value)}
              className="input-field"
              required
            >
              <option value="">Müşteri seçin</option>
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} ({c.username}) - #{c.id}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tutar (₺)
            </label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="input-field"
              placeholder="0.00"
              min="0.01"
              step="0.01"
              required
            />
          </div>

          <div className="flex gap-4 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="btn-secondary flex-1"
            >
              İptal
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn-success flex-1 disabled:opacity-50"
            >
              {loading ? 'İşleniyor...' : 'Yatır'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
