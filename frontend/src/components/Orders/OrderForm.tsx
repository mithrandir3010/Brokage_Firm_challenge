import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { orderService } from '../../services/api';

const POPULAR_STOCKS = ['THYAO', 'GARAN', 'AKBNK', 'EREGL', 'SISE', 'KCHOL', 'TUPRS', 'SAHOL', 'BIMAS', 'ASELS'];

export default function OrderForm() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    assetName: '',
    orderSide: 'BUY' as 'BUY' | 'SELL',
    size: '',
    price: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await orderService.createOrder({
        customerId: user!.customerId,
        assetName: formData.assetName.toUpperCase(),
        orderSide: formData.orderSide,
        size: parseFloat(formData.size),
        price: parseFloat(formData.price),
      });
      navigate('/orders');
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosError = err as { response?: { data?: { message?: string } } };
        setError(axiosError.response?.data?.message || 'Emir oluşturulamadı');
      } else {
        setError('Bağlantı hatası');
      }
    } finally {
      setLoading(false);
    }
  };

  const totalAmount = (parseFloat(formData.size) || 0) * (parseFloat(formData.price) || 0);

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Yeni Emir Oluştur</h1>

      <div className="card">
        {error && (
          <div className="bg-red-50 text-red-600 p-3 rounded-lg mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              İşlem Tipi
            </label>
            <div className="flex gap-4">
              <button
                type="button"
                onClick={() => setFormData({ ...formData, orderSide: 'BUY' })}
                className={`flex-1 py-4 rounded-lg font-medium transition-all ${
                  formData.orderSide === 'BUY'
                    ? 'bg-green-600 text-white shadow-lg scale-105'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                ALIŞ (BUY)
              </button>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, orderSide: 'SELL' })}
                className={`flex-1 py-4 rounded-lg font-medium transition-all ${
                  formData.orderSide === 'SELL'
                    ? 'bg-red-600 text-white shadow-lg scale-105'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                SATIŞ (SELL)
              </button>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Hisse Senedi
            </label>
            <input
              type="text"
              value={formData.assetName}
              onChange={(e) => setFormData({ ...formData, assetName: e.target.value.toUpperCase() })}
              className="input-field"
              placeholder="Örn: THYAO"
              required
            />
            <div className="flex flex-wrap gap-2 mt-2">
              {POPULAR_STOCKS.map((stock) => (
                <button
                  key={stock}
                  type="button"
                  onClick={() => setFormData({ ...formData, assetName: stock })}
                  className={`px-3 py-1 rounded-full text-sm transition-colors ${
                    formData.assetName === stock
                      ? 'bg-primary-600 text-white'
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                >
                  {stock}
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Miktar (Adet)
              </label>
              <input
                type="number"
                value={formData.size}
                onChange={(e) => setFormData({ ...formData, size: e.target.value })}
                className="input-field"
                placeholder="0"
                min="1"
                step="1"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Fiyat (₺)
              </label>
              <input
                type="number"
                value={formData.price}
                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                className="input-field"
                placeholder="0.00"
                min="0.01"
                step="0.01"
                required
              />
            </div>
          </div>

          <div className="bg-gray-50 p-4 rounded-lg">
            <div className="flex justify-between items-center">
              <span className="text-gray-600">Toplam Tutar:</span>
              <span className="text-2xl font-bold text-primary-600">
                {totalAmount.toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺
              </span>
            </div>
          </div>

          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => navigate('/orders')}
              className="btn-secondary flex-1"
            >
              İptal
            </button>
            <button
              type="submit"
              disabled={loading}
              className={`flex-1 font-medium py-2 px-4 rounded-lg transition-colors ${
                formData.orderSide === 'BUY'
                  ? 'bg-green-600 hover:bg-green-700 text-white'
                  : 'bg-red-600 hover:bg-red-700 text-white'
              } disabled:opacity-50`}
            >
              {loading ? 'İşleniyor...' : `${formData.orderSide === 'BUY' ? 'Satın Al' : 'Sat'}`}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
