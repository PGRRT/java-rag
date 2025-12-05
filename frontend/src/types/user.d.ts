export interface Credentials {
  email: string;
  password: string;
}

export interface User extends Credentials {
  id?: number;
  role: 'USER' | 'ADMIN' | 'MODERATOR';
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

// export interface RegisterData extends User {
//   confirmPassword: string;
//   otp: string;
// }

export interface RegisterData {
  email: string;
  password: string;
  confirmPassword: string;
  name?: string;
  otp: string;
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  isLoading: boolean;
  error: string | null;
}
