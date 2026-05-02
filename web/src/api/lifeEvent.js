import request from './request'

export const getTodayEvent = () => {
  return request({
    url: '/life-event/today',
    method: 'get'
  })
}

export const applyEventImpact = (eventRecordId) => {
  return request({
    url: '/life-event/apply',
    method: 'post',
    data: { eventRecordId }
  })
}

export const getEventHistory = (limit) => {
  return request({
    url: '/life-event/history',
    method: 'get',
    params: { limit }
  })
}

export const getActiveConfigs = () => {
  return request({
    url: '/life-event/configs',
    method: 'get'
  })
}

export const getEventStats = () => {
  return request({
    url: '/life-event/stats',
    method: 'get'
  })
}

export const getAllEventConfigs = () => {
  return request({
    url: '/life-event/admin/configs',
    method: 'get'
  })
}

export const getEventConfigById = (id) => {
  return request({
    url: `/life-event/admin/config/${id}`,
    method: 'get'
  })
}

export const addEventConfig = (config) => {
  return request({
    url: '/life-event/admin/config',
    method: 'post',
    data: config
  })
}

export const updateEventConfig = (id, config) => {
  return request({
    url: `/life-event/admin/config/${id}`,
    method: 'put',
    data: config
  })
}

export const deleteEventConfig = (id) => {
  return request({
    url: `/life-event/admin/config/${id}`,
    method: 'delete'
  })
}
