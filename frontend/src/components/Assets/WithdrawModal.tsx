import { useState } from 'react';
import { assetService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

interface WithdrawModalProps {
  onClose: () => void;
  onSuccess: () => void;
  maxAmount: number;
}

export default function WithdrawModal({ onClose, onSuccess, maxAmount }: WithdrawModalProps) {
  const { user } = useAuth();
  const [amount, setAmount] = useState('');
  const [iban, setIban] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await assetService.withdraw({
        customerId: user!.customerId,
        amount: parseFloat(amount),
        iban: iban.replace(/\s/g, ''),
      });
      onSuccess();
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosError = err as { response?: { data?: { message?: string } } };
        setError(axiosError.response?.data?.message || 'Para çekme başarısız');
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
        <h2 className="text-2xl font-bold mb-4">Para Çek</h2>
        
        <div className="bg-gray-50 p-3 rounded-lg mb-4">
          <p className="text-sm text-gray-600">Kullanılabilir Bakiye:</p>
          <p className="text-xl font-bold text-primary-600">
            {maxAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
          </p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
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
              max={maxAmount}
              step="0.01"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              IBAN
            </label>
            <input
              type="text"
              value={iban}
              onChange={(e) => setIban(e.target.value)}
              className="input-field"
              placeholder="TR00 0000 0000 0000 0000 0000 00"
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
              disabled={loading || parseFloat(amount) > maxAmount}
              className="btn-danger flex-1 disabled:opacity-50"
            >
              {loading ? 'İşleniyor...' : 'Çek'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
