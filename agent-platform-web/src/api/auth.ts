import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email?: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  roleCode: string
  permissions: string[]
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: UserInfo
}

export const authApi = {
  login(data: LoginRequest) {
    return request.post<any, { data: LoginResponse }>('/v1/auth/login', data)
  },
  register(data: RegisterRequest) {
    return request.post<any, { data: UserInfo }>('/v1/auth/register', data)
  },
  logout() {
    return request.post('/v1/auth/logout')
  },
  me() {
    return request.get<any, { data: UserInfo }>('/v1/auth/me')
  },
}