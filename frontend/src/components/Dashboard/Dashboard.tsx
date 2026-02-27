import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { assetService, orderService } from '../../services/api';
import type { Asset, Order } from '../../types';

export default function Dashboard() {
  const { user } = useAuth();
  const [assets, setAssets] = useState<Asset[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [assetsData, ordersData] = await Promise.all([
          assetService.getAssets(user?.customerId),
          orderService.getOrders(user?.customerId),
        ]);
        setAssets(assetsData);
        setOrders(ordersData);
      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchData();
    }
  }, [user]);

  const tryBalance = assets.find(a => a.assetName === 'TRY');
  const pendingOrders = orders.filter(o => o.status === 'PENDING');
  const totalAssets = assets.filter(a => a.assetName !== 'TRY');

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-8">Hoş Geldiniz, {user?.username}!</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="card bg-gradient-to-br from-primary-500 to-primary-700 text-white">
          <h3 className="text-sm font-medium opacity-80">TRY Bakiye</h3>
          <p className="text-3xl font-bold mt-2">
            {tryBalance ? Number(tryBalance.usableSize).toLocaleString('tr-TR', { minimumFractionDigits: 2 }) : '0.00'} ₺
          </p>
          <p className="text-sm opacity-80 mt-1">
            Toplam: {tryBalance ? Number(tryBalance.size).toLocaleString('tr-TR', { minimumFractionDigits: 2 }) : '0.00'} ₺
          </p>
        </div>

        <div className="card bg-gradient-to-br from-green-500 to-green-700 text-white">
          <h3 className="text-sm font-medium opacity-80">Toplam Varlık</h3>
          <p className="text-3xl font-bold mt-2">{totalAssets.length}</p>
          <p className="text-sm opacity-80 mt-1">Farklı hisse</p>
        </div>

        <div className="card bg-gradient-to-br from-yellow-500 to-yellow-700 text-white">
          <h3 className="text-sm font-medium opacity-80">Bekleyen Emirler</h3>
          <p className="text-3xl font-bold mt-2">{pendingOrders.length}</p>
          <p className="text-sm opacity-80 mt-1">Aktif emir</p>
        </div>

        <div className="card bg-gradient-to-br from-purple-500 to-purple-700 text-white">
          <h3 className="text-sm font-medium opacity-80">Toplam İşlem</h3>
          <p className="text-3xl font-bold mt-2">{orders.length}</p>
          <p className="text-sm opacity-80 mt-1">Tüm emirler</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold">Varlıklarım</h2>
            <Link to="/assets" className="text-primary-600 hover:underline text-sm">
              Tümünü Gör
            </Link>
          </div>
          {assets.length === 0 ? (
            <p className="text-gray-500">Henüz varlık yok</p>
          ) : (
            <div className="space-y-3">
              {assets.slice(0, 5).map((asset) => (
                <div key={asset.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <span className="font-medium">{asset.assetName}</span>
                  <div className="text-right">
                    <p className="font-semibold">{Number(asset.size).toLocaleString('tr-TR')}</p>
                    <p className="text-sm text-gray-500">Kullanılabilir: {Number(asset.usableSize).toLocaleString('tr-TR')}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="card">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold">Son Emirler</h2>
            <Link to="/orders" className="text-primary-600 hover:underline text-sm">
              Tümünü Gör
            </Link>
          </div>
          {orders.length === 0 ? (
            <p className="text-gray-500">Henüz emir yok</p>
          ) : (
            <div className="space-y-3">
              {orders.slice(0, 5).map((order) => (
                <div key={order.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                  <div>
                    <span className={`px-2 py-1 rounded text-xs font-medium ${
                      order.orderSide === 'BUY' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                    }`}>
                      {order.orderSide === 'BUY' ? 'ALIŞ' : 'SATIŞ'}
                    </span>
                    <span className="ml-2 font-medium">{order.assetName}</span>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">{Number(order.size).toLocaleString('tr-TR')} @ {Number(order.price).toLocaleString('tr-TR')} ₺</p>
                    <span className={`text-xs px-2 py-1 rounded ${
                      order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                      order.status === 'MATCHED' ? 'bg-green-100 text-green-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>
                      {order.status === 'PENDING' ? 'Bekliyor' : order.status === 'MATCHED' ? 'Eşleşti' : 'İptal'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="mt-8 flex gap-4">
        <Link to="/orders/new" className="btn-primary">
          Yeni Emir Oluştur
        </Link>
        <Link to="/assets" className="btn-secondary">
          Para Yatır / Çek
        </Link>
      </div>
    </div>
  );
}
