import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="relative min-h-[85vh] -mx-4 sm:-mx-6 lg:-mx-8 -mt-8 overflow-hidden">
      {/* Background with gradient overlay */}
      <div className="absolute inset-0 bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900">
        {/* Animated grid pattern */}
        <div className="absolute inset-0 opacity-20">
          <svg className="w-full h-full" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
                <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255,255,255,0.1)" strokeWidth="1"/>
              </pattern>
            </defs>
            <rect width="100%" height="100%" fill="url(#grid)" />
          </svg>
        </div>
        
        {/* Floating stock chart lines */}
        <svg className="absolute inset-0 w-full h-full opacity-30" viewBox="0 0 1200 600" preserveAspectRatio="none">
          <path 
            d="M0,400 Q150,350 300,380 T600,320 T900,360 T1200,300" 
            fill="none" 
            stroke="url(#gradient1)" 
            strokeWidth="2"
            className="animate-pulse"
          />
          <path 
            d="M0,450 Q200,400 400,430 T800,370 T1200,400" 
            fill="none" 
            stroke="url(#gradient2)" 
            strokeWidth="2"
            className="animate-pulse"
            style={{ animationDelay: '0.5s' }}
          />
          <path 
            d="M0,350 Q100,300 250,330 T500,280 T750,320 T1000,270 T1200,290" 
            fill="none" 
            stroke="url(#gradient3)" 
            strokeWidth="1.5"
            className="animate-pulse"
            style={{ animationDelay: '1s' }}
          />
          <defs>
            <linearGradient id="gradient1" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#22c55e" stopOpacity="0.5"/>
              <stop offset="50%" stopColor="#3b82f6" stopOpacity="0.8"/>
              <stop offset="100%" stopColor="#22c55e" stopOpacity="0.5"/>
            </linearGradient>
            <linearGradient id="gradient2" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#ef4444" stopOpacity="0.4"/>
              <stop offset="50%" stopColor="#f59e0b" stopOpacity="0.6"/>
              <stop offset="100%" stopColor="#22c55e" stopOpacity="0.4"/>
            </linearGradient>
            <linearGradient id="gradient3" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stopColor="#8b5cf6" stopOpacity="0.3"/>
              <stop offset="100%" stopColor="#06b6d4" stopOpacity="0.5"/>
            </linearGradient>
          </defs>
        </svg>

        {/* Glowing orbs */}
        <div className="absolute top-20 left-20 w-72 h-72 bg-blue-500 rounded-full filter blur-[120px] opacity-20 animate-pulse"></div>
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-purple-500 rounded-full filter blur-[150px] opacity-15 animate-pulse" style={{ animationDelay: '1s' }}></div>
        <div className="absolute top-1/2 left-1/2 w-64 h-64 bg-green-500 rounded-full filter blur-[100px] opacity-10 animate-pulse" style={{ animationDelay: '2s' }}></div>
      </div>

      {/* Content */}
      <div className="relative z-10 flex flex-col items-center justify-center min-h-[85vh] text-center px-4">
        {/* Logo and Title */}
        <div className="mb-8">
          <div className="flex items-center justify-center mb-4">
            <div className="w-16 h-16 bg-gradient-to-br from-blue-500 to-blue-700 rounded-2xl flex items-center justify-center shadow-2xl shadow-blue-500/30">
              <svg className="w-10 h-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
              </svg>
            </div>
          </div>
          <h1 className="text-6xl md:text-7xl font-bold mb-4">
            <span className="bg-gradient-to-r from-blue-400 via-cyan-400 to-blue-500 bg-clip-text text-transparent">Broker</span>
            <span className="text-white">App</span>
          </h1>
          <p className="text-xl md:text-2xl text-gray-300 max-w-2xl mx-auto leading-relaxed">
            Güvenli ve hızlı borsa işlemleri için profesyonel aracı kurum platformu.
            <br />
            <span className="text-gray-400">Hisse alım-satım emirlerinizi kolayca yönetin.</span>
          </p>
        </div>

        {/* Stats */}
        <div className="flex flex-wrap justify-center gap-8 mb-12">
          <div className="text-center">
            <div className="text-3xl font-bold text-green-400">+24.5%</div>
            <div className="text-sm text-gray-400">BIST100</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-blue-400">₺1.2M+</div>
            <div className="text-sm text-gray-400">İşlem Hacmi</div>
          </div>
          <div className="text-center">
            <div className="text-3xl font-bold text-purple-400">500+</div>
            <div className="text-sm text-gray-400">Aktif Kullanıcı</div>
          </div>
        </div>

        {/* Feature Cards */}
        <div className="flex flex-wrap gap-6 justify-center mb-12 max-w-4xl">
          <div className="bg-white/5 backdrop-blur-lg border border-white/10 p-6 rounded-2xl w-64 hover:bg-white/10 hover:border-white/20 transition-all duration-300 hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl flex items-center justify-center mb-4 shadow-lg shadow-green-500/20">
              <svg className="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
              </svg>
            </div>
            <h3 className="font-bold text-lg mb-2 text-white">Hisse Al/Sat</h3>
            <p className="text-gray-400 text-sm">Borsa İstanbul hisselerini kolayca alın ve satın</p>
          </div>
          
          <div className="bg-white/5 backdrop-blur-lg border border-white/10 p-6 rounded-2xl w-64 hover:bg-white/10 hover:border-white/20 transition-all duration-300 hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-yellow-500 to-orange-600 rounded-xl flex items-center justify-center mb-4 shadow-lg shadow-yellow-500/20">
              <svg className="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 className="font-bold text-lg mb-2 text-white">TRY Bakiye</h3>
            <p className="text-gray-400 text-sm">Hesabınıza para yatırın ve çekin</p>
          </div>
          
          <div className="bg-white/5 backdrop-blur-lg border border-white/10 p-6 rounded-2xl w-64 hover:bg-white/10 hover:border-white/20 transition-all duration-300 hover:-translate-y-1">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center mb-4 shadow-lg shadow-blue-500/20">
              <svg className="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
            </div>
            <h3 className="font-bold text-lg mb-2 text-white">Portföy Takibi</h3>
            <p className="text-gray-400 text-sm">Varlıklarınızı ve emirlerinizi takip edin</p>
          </div>
        </div>

        {/* CTA Buttons */}
        {isAuthenticated ? (
          <Link 
            to="/dashboard" 
            className="group relative px-8 py-4 bg-gradient-to-r from-blue-600 to-blue-700 rounded-xl font-semibold text-lg text-white shadow-2xl shadow-blue-500/30 hover:shadow-blue-500/50 transition-all duration-300 hover:-translate-y-1"
          >
            <span className="relative z-10">Dashboard'a Git</span>
            <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </Link>
        ) : (
          <div className="flex flex-col sm:flex-row gap-4">
            <Link 
              to="/login" 
              className="px-8 py-4 bg-white/10 backdrop-blur-lg border border-white/20 rounded-xl font-semibold text-lg text-white hover:bg-white/20 transition-all duration-300 hover:-translate-y-1"
            >
              Giriş Yap
            </Link>
            <Link 
              to="/register" 
              className="group relative px-8 py-4 bg-gradient-to-r from-blue-600 to-cyan-600 rounded-xl font-semibold text-lg text-white shadow-2xl shadow-blue-500/30 hover:shadow-blue-500/50 transition-all duration-300 hover:-translate-y-1 overflow-hidden"
            >
              <span className="relative z-10">Ücretsiz Kayıt Ol</span>
              <div className="absolute inset-0 bg-gradient-to-r from-cyan-500 to-blue-500 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
            </Link>
          </div>
        )}

        {/* Trust badges */}
        <div className="mt-12 flex flex-wrap justify-center gap-6 text-gray-500 text-sm">
          <div className="flex items-center gap-2">
            <svg className="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
            <span>SSL Güvenli</span>
          </div>
          <div className="flex items-center gap-2">
            <svg className="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
            <span>7/24 İşlem</span>
          </div>
          <div className="flex items-center gap-2">
            <svg className="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
            <span>Anlık Eşleşme</span>
          </div>
        </div>
      </div>
    </div>
  );
}
