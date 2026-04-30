import request from './request'

export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export const checkUsername = (username) => {
  return request({
    url: '/auth/check-username',
    method: 'get',
    params: { username }
  })
}

export const checkEmail = (email) => {
  return request({
    url: '/auth/check-email',
    method: 'get',
    params: { email }
  })
}

export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export const updateUserInfo = (data) => {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

export const updateBirthdate = (birthDate) => {
  return request({
    url: '/user/update-birthdate',
    method: 'put',
    params: { birthDate }
  })
}
