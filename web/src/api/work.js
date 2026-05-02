import request from './request'

export const getWorkSetting = () => {
  return request({
    url: '/work/setting',
    method: 'get'
  })
}

export const saveWorkSetting = (data) => {
  return request({
    url: '/work/setting',
    method: 'post',
    data
  })
}

export const getWorkProgress = () => {
  return request({
    url: '/work/progress',
    method: 'get'
  })
}

export const getRetirementCountdown = () => {
  return request({
    url: '/work/retirement/countdown',
    method: 'get'
  })
}

export const getYearlyStats = (year) => {
  return request({
    url: '/work/yearly/stats',
    method: 'get',
    params: { year }
  })
}

export const getEarnings = () => {
  return request({
    url: '/work/earnings',
    method: 'get'
  })
}

export const getAllWorkProgress = () => {
  return request({
    url: '/work/all',
    method: 'get'
  })
}
