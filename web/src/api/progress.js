import request from './request'

export const getLifeProgress = (userId) => {
  return request({
    url: '/progress/life',
    method: 'get',
    params: { userId }
  })
}

export const getYearProgress = () => {
  return request({
    url: '/progress/year',
    method: 'get'
  })
}

export const getSeasonProgress = () => {
  return request({
    url: '/progress/season',
    method: 'get'
  })
}

export const getWeekProgress = () => {
  return request({
    url: '/progress/week',
    method: 'get'
  })
}

export const getAllProgress = (userId) => {
  return request({
    url: '/progress/all',
    method: 'get',
    params: { userId }
  })
}
