export interface User {
  id: number;
  name: string;
  username: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  type: string;
  customerId: number;
  username: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  name: string;
}

export interface Asset {
  id: number;
  customerId: number;
  assetName: string;
  size: number;
  usableSize: number;
}

export interface Order {
  id: number;
  customerId: number;
  assetName: string;
  orderSide: 'BUY' | 'SELL';
  size: number;
  price: number;
  status: 'PENDING' | 'MATCHED' | 'CANCELED';
  createDate: string;
}

export interface CreateOrderRequest {
  customerId: number;
  assetName: string;
  orderSide: 'BUY' | 'SELL';
  size: number;
  price: number;
}

export interface DepositRequest {
  customerId: number;
  amount: number;
}

export interface WithdrawRequest {
  customerId: number;
  amount: number;
  iban: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
}
