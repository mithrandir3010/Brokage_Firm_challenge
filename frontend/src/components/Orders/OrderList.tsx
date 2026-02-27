import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { orderService } from '../../services/api';
import type { Order } from '../../types';

export default function OrderList() {
  const { user, isAdmin } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'ALL' | 'PENDING' | 'MATCHED' | 'CANCELED'>('ALL');

  const fetchOrders = async () => {
    try {
      const data = await orderService.getOrders(isAdmin ? undefined : user?.customerId);
      setOrders(data);
    } catch (error) {
      console.error('Error fetching orders:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [user, isAdmin]);

  const handleCancel = async (orderId: number) => {
    if (!confirm('Bu emri iptal etmek istediğinize emin misiniz?')) return;
    
    try {
      await orderService.cancelOrder(orderId);
      fetchOrders();
    } catch (error) {
      console.error('Error canceling order:', error);
      alert('Emir iptal edilemedi');
    }
  };

  const handleMatch = async (orderId: number) => {
    if (!confirm('Bu emri eşleştirmek istediğinize emin misiniz?')) return;
    
    try {
      await orderService.matchOrder(orderId);
      fetchOrders();
    } catch (error) {
      console.error('Error matching order:', error);
      alert('Emir eşleştirilemedi');
    }
  };

  const filteredOrders = filter === 'ALL' 
    ? orders 
    : orders.filter(o => o.status === filter);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Emirler</h1>
        <Link to="/orders/new" className="btn-primary">
          Yeni Emir
        </Link>
      </div>

      <div className="card mb-6">
        <div className="flex gap-2">
          {(['ALL', 'PENDING', 'MATCHED', 'CANCELED'] as const).map((status) => (
            <button
              key={status}
              onClick={() => setFilter(status)}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                filter === status
                  ? 'bg-primary-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {status === 'ALL' ? 'Tümü' : 
               status === 'PENDING' ? 'Bekleyen' : 
               status === 'MATCHED' ? 'Eşleşen' : 'İptal'}
              <span className="ml-2 px-2 py-0.5 rounded-full bg-white/20 text-sm">
                {status === 'ALL' ? orders.length : orders.filter(o => o.status === status).length}
              </span>
            </button>
          ))}
        </div>
      </div>

      {filteredOrders.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-gray-500 text-lg">Henüz emir bulunmuyor</p>
          <Link to="/orders/new" className="btn-primary mt-4 inline-block">
            İlk Emrinizi Oluşturun
          </Link>
        </div>
      ) : (
        <div className="card overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">ID</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Tip</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Hisse</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Miktar</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Fiyat</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Toplam</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Durum</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Tarih</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">İşlem</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filteredOrders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 text-sm">#{order.id}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${
                      order.orderSide === 'BUY' 
                        ? 'bg-green-100 text-green-700' 
                        : 'bg-red-100 text-red-700'
                    }`}>
                      {order.orderSide === 'BUY' ? 'ALIŞ' : 'SATIŞ'}
                    </span>
                  </td>
                  <td className="px-4 py-3 font-medium">{order.assetName}</td>
                  <td className="px-4 py-3">{Number(order.size).toLocaleString('tr-TR')}</td>
                  <td className="px-4 py-3">{Number(order.price).toLocaleString('tr-TR')} ₺</td>
                  <td className="px-4 py-3 font-medium">
                    {(Number(order.size) * Number(order.price)).toLocaleString('tr-TR')} ₺
                  </td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${
                      order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                      order.status === 'MATCHED' ? 'bg-green-100 text-green-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>
                      {order.status === 'PENDING' ? 'Bekliyor' : 
                       order.status === 'MATCHED' ? 'Eşleşti' : 'İptal'}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-500">
                    {new Date(order.createDate).toLocaleDateString('tr-TR')}
                  </td>
                  <td className="px-4 py-3">
                    {order.status === 'PENDING' && (
                      <div className="flex gap-2">
                        {isAdmin && (
                          <button
                            onClick={() => handleMatch(order.id)}
                            className="text-green-600 hover:text-green-800 text-sm font-medium"
                          >
                            Eşleştir
                          </button>
                        )}
                        <button
                          onClick={() => handleCancel(order.id)}
                          className="text-red-600 hover:text-red-800 text-sm font-medium"
                        >
                          İptal
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
