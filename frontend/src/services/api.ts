import axios from 'axios';
import type { 
  AuthResponse, 
  LoginRequest, 
  RegisterRequest, 
  Asset, 
  Order, 
  CreateOrderRequest,
  DepositRequest,
  WithdrawRequest,
  User
} from '../types';

const API_BASE_URL = import.meta.env.DEV 
  ? '/api' 
  : (import.meta.env.VITE_API_URL || 'http://localhost:8080/api');

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<User> => {
    const response = await api.post<User>('/auth/register', data);
    return response.data;
  },
};

export const assetService = {
  getAssets: async (customerId?: number): Promise<Asset[]> => {
    const params = customerId ? { customerId } : {};
    const response = await api.get<Asset[]>('/assets', { params });
    return response.data;
  },

  deposit: async (data: DepositRequest): Promise<Asset> => {
    const response = await api.post<Asset>('/assets/deposit', data);
    return response.data;
  },

  withdraw: async (data: WithdrawRequest): Promise<Asset> => {
    const response = await api.post<Asset>('/assets/withdraw', data);
    return response.data;
  },
};

export const orderService = {
  getOrders: async (customerId?: number, startDate?: string, endDate?: string): Promise<Order[]> => {
    const params: Record<string, string | number> = {};
    if (customerId) params.customerId = customerId;
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    const response = await api.get<Order[]>('/orders', { params });
    return response.data;
  },

  createOrder: async (data: CreateOrderRequest): Promise<Order> => {
    const response = await api.post<Order>('/orders', data);
    return response.data;
  },

  cancelOrder: async (orderId: number): Promise<void> => {
    await api.delete(`/orders/${orderId}`);
  },

  matchOrder: async (orderId: number): Promise<Order> => {
    const response = await api.post<Order>(`/orders/${orderId}/match`);
    return response.data;
  },
};

export const customerService = {
  getCustomers: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/customers');
    return response.data;
  },

  getCustomer: async (id: number): Promise<User> => {
    const response = await api.get<User>(`/customers/${id}`);
    return response.data;
  },

  deleteCustomer: async (id: number): Promise<void> => {
    await api.delete(`/customers/${id}`);
  },
};

export default api;
