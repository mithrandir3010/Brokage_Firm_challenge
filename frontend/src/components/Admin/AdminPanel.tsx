import { useEffect, useState } from 'react';
import { customerService, orderService } from '../../services/api';
import type { User, Order } from '../../types';

export default function AdminPanel() {
  const [customers, setCustomers] = useState<User[]>([]);
  const [pendingOrders, setPendingOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'customers' | 'orders'>('customers');

  const fetchData = async () => {
    try {
      const [customersData, ordersData] = await Promise.all([
        customerService.getCustomers(),
        orderService.getOrders(),
      ]);
      setCustomers(customersData);
      setPendingOrders(ordersData.filter(o => o.status === 'PENDING'));
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleDeleteCustomer = async (id: number) => {
    if (!confirm('Bu müşteriyi silmek istediğinize emin misiniz?')) return;
    
    try {
      await customerService.deleteCustomer(id);
      fetchData();
    } catch (error) {
      console.error('Error deleting customer:', error);
      alert('Müşteri silinemedi');
    }
  };

  const handleMatchOrder = async (orderId: number) => {
    if (!confirm('Bu emri eşleştirmek istediğinize emin misiniz?')) return;
    
    try {
      await orderService.matchOrder(orderId);
      fetchData();
    } catch (error) {
      console.error('Error matching order:', error);
      alert('Emir eşleştirilemedi');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Admin Panel</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="card bg-gradient-to-br from-blue-500 to-blue-700 text-white">
          <h3 className="text-sm font-medium opacity-80">Toplam Müşteri</h3>
          <p className="text-4xl font-bold mt-2">{customers.length}</p>
        </div>
        <div className="card bg-gradient-to-br from-yellow-500 to-yellow-700 text-white">
          <h3 className="text-sm font-medium opacity-80">Bekleyen Emirler</h3>
          <p className="text-4xl font-bold mt-2">{pendingOrders.length}</p>
        </div>
        <div className="card bg-gradient-to-br from-green-500 to-green-700 text-white">
          <h3 className="text-sm font-medium opacity-80">Admin Sayısı</h3>
          <p className="text-4xl font-bold mt-2">{customers.filter(c => c.role === 'ADMIN').length}</p>
        </div>
      </div>

      <div className="card">
        <div className="flex border-b mb-4">
          <button
            onClick={() => setActiveTab('customers')}
            className={`px-4 py-2 font-medium -mb-px ${
              activeTab === 'customers'
                ? 'border-b-2 border-primary-600 text-primary-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Müşteriler ({customers.length})
          </button>
          <button
            onClick={() => setActiveTab('orders')}
            className={`px-4 py-2 font-medium -mb-px ${
              activeTab === 'orders'
                ? 'border-b-2 border-primary-600 text-primary-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Bekleyen Emirler ({pendingOrders.length})
          </button>
        </div>

        {activeTab === 'customers' && (
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">ID</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Ad</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Kullanıcı Adı</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">E-posta</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Rol</th>
                <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">İşlem</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {customers.map((customer) => (
                <tr key={customer.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3">#{customer.id}</td>
                  <td className="px-4 py-3 font-medium">{customer.name}</td>
                  <td className="px-4 py-3">{customer.username}</td>
                  <td className="px-4 py-3 text-gray-500">{customer.email}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded text-xs font-medium ${
                      customer.role === 'ADMIN' 
                        ? 'bg-purple-100 text-purple-700' 
                        : 'bg-blue-100 text-blue-700'
                    }`}>
                      {customer.role}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    {customer.role !== 'ADMIN' && (
                      <button
                        onClick={() => handleDeleteCustomer(customer.id)}
                        className="text-red-600 hover:text-red-800 text-sm font-medium"
                      >
                        Sil
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {activeTab === 'orders' && (
          <>
            {pendingOrders.length === 0 ? (
              <p className="text-gray-500 text-center py-8">Bekleyen emir bulunmuyor</p>
            ) : (
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">ID</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Müşteri</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Tip</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Hisse</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Miktar</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">Fiyat</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-600">İşlem</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {pendingOrders.map((order) => (
                    <tr key={order.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3">#{order.id}</td>
                      <td className="px-4 py-3">#{order.customerId}</td>
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
                      <td className="px-4 py-3">
                        <button
                          onClick={() => handleMatchOrder(order.id)}
                          className="btn-success text-sm py-1 px-3"
                        >
                          Eşleştir
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </>
        )}
      </div>
    </div>
  );
}
