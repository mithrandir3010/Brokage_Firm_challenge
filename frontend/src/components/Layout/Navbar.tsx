import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isAuthPage = ['/', '/login', '/register'].includes(location.pathname);

  return (
    <nav className={`${isAuthPage ? 'bg-transparent absolute top-0 left-0 right-0 z-50' : 'bg-white shadow-md'}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center gap-2">
              <div className={`w-8 h-8 ${isAuthPage ? 'bg-gradient-to-br from-blue-500 to-blue-700' : 'bg-gradient-to-br from-blue-600 to-blue-800'} rounded-lg flex items-center justify-center`}>
                <svg className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                </svg>
              </div>
              <span className={`text-xl font-bold ${isAuthPage ? 'text-white' : 'text-primary-600'}`}>
                BrokerApp
              </span>
            </Link>
            {user && !isAuthPage && (
              <div className="hidden md:flex ml-10 space-x-1">
                <Link
                  to="/dashboard"
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    location.pathname === '/dashboard' 
                      ? 'bg-primary-50 text-primary-600' 
                      : 'text-gray-600 hover:text-primary-600 hover:bg-gray-50'
                  }`}
                >
                  Dashboard
                </Link>
                <Link
                  to="/orders"
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    location.pathname.startsWith('/orders') 
                      ? 'bg-primary-50 text-primary-600' 
                      : 'text-gray-600 hover:text-primary-600 hover:bg-gray-50'
                  }`}
                >
                  Emirler
                </Link>
                <Link
                  to="/assets"
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    location.pathname === '/assets' 
                      ? 'bg-primary-50 text-primary-600' 
                      : 'text-gray-600 hover:text-primary-600 hover:bg-gray-50'
                  }`}
                >
                  Varlıklar
                </Link>
                {isAdmin && (
                  <Link
                    to="/admin"
                    className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                      location.pathname === '/admin' 
                        ? 'bg-purple-50 text-purple-600' 
                        : 'text-gray-600 hover:text-purple-600 hover:bg-purple-50'
                    }`}
                  >
                    Admin Panel
                  </Link>
                )}
              </div>
            )}
          </div>
          <div className="flex items-center">
            {user ? (
              <div className="flex items-center gap-4">
                <div className={`flex items-center gap-2 ${isAuthPage ? 'text-white' : ''}`}>
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${
                    isAdmin 
                      ? 'bg-purple-100 text-purple-600' 
                      : 'bg-blue-100 text-blue-600'
                  }`}>
                    {user.username.charAt(0).toUpperCase()}
                  </div>
                  <span className={`text-sm ${isAuthPage ? 'text-gray-300' : 'text-gray-600'}`}>
                    {user.username}
                  </span>
                  <span className={`px-2 py-0.5 text-xs rounded-full font-medium ${
                    isAdmin 
                      ? 'bg-purple-100 text-purple-700' 
                      : 'bg-blue-100 text-blue-700'
                  }`}>
                    {user.role}
                  </span>
                </div>
                <button
                  onClick={handleLogout}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    isAuthPage 
                      ? 'bg-white/10 text-white hover:bg-white/20 border border-white/20' 
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Çıkış
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-3">
                <Link 
                  to="/login" 
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    isAuthPage 
                      ? 'text-white hover:text-gray-200' 
                      : 'text-gray-600 hover:text-primary-600'
                  }`}
                >
                  Giriş Yap
                </Link>
                <Link 
                  to="/register" 
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                    isAuthPage 
                      ? 'bg-gradient-to-r from-blue-600 to-cyan-600 text-white shadow-lg shadow-blue-500/30 hover:shadow-blue-500/50' 
                      : 'bg-primary-600 text-white hover:bg-primary-700'
                  }`}
                >
                  Kayıt Ol
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
