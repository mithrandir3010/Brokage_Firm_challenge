import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { assetService } from '../../services/api';
import type { Asset } from '../../types';
import DepositModal from './DepositModal';
import WithdrawModal from './WithdrawModal';

export default function AssetList() {
  const { user, isAdmin } = useAuth();
  const [assets, setAssets] = useState<Asset[]>([]);
  const [loading, setLoading] = useState(true);
  const [showDeposit, setShowDeposit] = useState(false);
  const [showWithdraw, setShowWithdraw] = useState(false);

  const fetchAssets = async () => {
    try {
      const data = await assetService.getAssets(isAdmin ? undefined : user?.customerId);
      setAssets(data);
    } catch (error) {
      console.error('Error fetching assets:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssets();
  }, [user, isAdmin]);

  const tryAsset = assets.find(a => a.assetName === 'TRY');
  const stockAssets = assets.filter(a => a.assetName !== 'TRY');

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
        <h1 className="text-3xl font-bold">Varlıklarım</h1>
        <div className="flex gap-2">
          {isAdmin && (
            <button onClick={() => setShowDeposit(true)} className="btn-success">
              Para Yatır
            </button>
          )}
          <button onClick={() => setShowWithdraw(true)} className="btn-secondary">
            Para Çek
          </button>
        </div>
      </div>

      <div className="card mb-6 bg-gradient-to-r from-primary-600 to-primary-800 text-white">
        <h2 className="text-lg font-medium opacity-80">TRY Bakiye</h2>
        <div className="mt-4 grid grid-cols-2 gap-8">
          <div>
            <p className="text-sm opacity-80">Toplam Bakiye</p>
            <p className="text-4xl font-bold">
              {tryAsset ? Number(tryAsset.size).toLocaleString('tr-TR', { minimumFractionDigits: 2 }) : '0.00'} ₺
            </p>
          </div>
          <div>
            <p className="text-sm opacity-80">Kullanılabilir</p>
            <p className="text-4xl font-bold">
              {tryAsset ? Number(tryAsset.usableSize).toLocaleString('tr-TR', { minimumFractionDigits: 2 }) : '0.00'} ₺
            </p>
          </div>
        </div>
        {tryAsset && Number(tryAsset.size) !== Number(tryAsset.usableSize) && (
          <p className="mt-4 text-sm opacity-80">
            Bloke: {(Number(tryAsset.size) - Number(tryAsset.usableSize)).toLocaleString('tr-TR', { minimumFractionDigits: 2 })} ₺ (bekleyen emirlerde)
          </p>
        )}
      </div>

      <div className="card">
        <h2 className="text-xl font-bold mb-4">Hisse Senetleri</h2>
        {stockAssets.length === 0 ? (
          <p className="text-gray-500 text-center py-8">Henüz hisse senedi bulunmuyor</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {stockAssets.map((asset) => (
              <div key={asset.id} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-lg font-bold text-primary-600">{asset.assetName}</h3>
                    <p className="text-sm text-gray-500">Müşteri #{asset.customerId}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold">{Number(asset.size).toLocaleString('tr-TR')}</p>
                    <p className="text-sm text-gray-500">adet</p>
                  </div>
                </div>
                <div className="mt-4 pt-4 border-t">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Kullanılabilir:</span>
                    <span className="font-medium">{Number(asset.usableSize).toLocaleString('tr-TR')}</span>
                  </div>
                  {Number(asset.size) !== Number(asset.usableSize) && (
                    <div className="flex justify-between text-sm mt-1">
                      <span className="text-gray-600">Bloke:</span>
                      <span className="font-medium text-yellow-600">
                        {(Number(asset.size) - Number(asset.usableSize)).toLocaleString('tr-TR')}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {showDeposit && (
        <DepositModal
          onClose={() => setShowDeposit(false)}
          onSuccess={() => {
            setShowDeposit(false);
            fetchAssets();
          }}
        />
      )}

      {showWithdraw && (
        <WithdrawModal
          onClose={() => setShowWithdraw(false)}
          onSuccess={() => {
            setShowWithdraw(false);
            fetchAssets();
          }}
          maxAmount={tryAsset ? Number(tryAsset.usableSize) : 0}
        />
      )}
    </div>
  );
}
