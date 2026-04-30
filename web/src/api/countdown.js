import request from './request'

export const getCountdownList = (category) => {
  return request({
    url: '/countdown/list',
    method: 'get',
    params: { category }
  })
}

export const getCountdownListWithRemaining = (category) => {
  return request({
    url: '/countdown/list-with-remaining',
    method: 'get',
    params: { category }
  })
}

export const getCountdownById = (id) => {
  return request({
    url: `/countdown/${id}`,
    method: 'get'
  })
}

export const createCountdown = (data) => {
  return request({
    url: '/countdown/create',
    method: 'post',
    data
  })
}

export const updateCountdown = (id, data) => {
  return request({
    url: `/countdown/update/${id}`,
    method: 'put',
    data
  })
}

export const updateCountdownTop = (id, isTop) => {
  return request({
    url: `/countdown/top/${id}`,
    method: 'put',
    params: { isTop }
  })
}

export const deleteCountdown = (id) => {
  return request({
    url: `/countdown/${id}`,
    method: 'delete'
  })
}

export const getCountdownCategories = () => {
  return request({
    url: '/countdown/categories',
    method: 'get'
  })
}
