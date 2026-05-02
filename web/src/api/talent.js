import request from './request'

// 获取当前用户的天赋数据
export const getMyTalent = () => {
  return request({
    url: '/talent/my',
    method: 'get'
  })
}

// 获取天赋系统配置
export const getTalentConfig = () => {
  return request({
    url: '/talent/config',
    method: 'get'
  })
}

// 分配天赋点
export const allocateTalentPoints = (talentPoints) => {
  return request({
    url: '/talent/allocate',
    method: 'post',
    data: talentPoints
  })
}

// 重置天赋点
export const resetTalentPoints = () => {
  return request({
    url: '/talent/reset',
    method: 'post'
  })
}

// 模拟事业
export const simulateCareer = () => {
  return request({
    url: '/talent/simulate/career',
    method: 'get'
  })
}

// 模拟感情
export const simulateRelationship = () => {
  return request({
    url: '/talent/simulate/relationship',
    method: 'get'
  })
}

// 模拟健康
export const simulateHealth = () => {
  return request({
    url: '/talent/simulate/health',
    method: 'get'
  })
}

// 模拟财富
export const simulateWealth = () => {
  return request({
    url: '/talent/simulate/wealth',
    method: 'get'
  })
}

// 模拟综合
export const simulateOverall = () => {
  return request({
    url: '/talent/simulate/overall',
    method: 'get'
  })
}

// 获取模拟日志
export const getSimulationLogs = (simulationType) => {
  return request({
    url: '/talent/logs',
    method: 'get',
    params: { simulationType }
  })
}

// ========== 管理员API ==========

// 获取所有天赋配置
export const getAllConfigs = () => {
  return request({
    url: '/talent/admin/configs',
    method: 'get'
  })
}

// 更新天赋配置
export const updateConfig = (configKey, data) => {
  return request({
    url: `/talent/admin/config/${configKey}`,
    method: 'put',
    data
  })
}

// 获取所有用户的天赋数据
export const getAllUserTalents = () => {
  return request({
    url: '/talent/admin/users',
    method: 'get'
  })
}

// 获取指定用户的天赋数据
export const getUserTalent = (userId) => {
  return request({
    url: `/talent/admin/user/${userId}`,
    method: 'get'
  })
}

// 为用户分配天赋点
export const allocateTalentForUser = (userId, talentPoints) => {
  return request({
    url: `/talent/admin/user/${userId}/allocate`,
    method: 'put',
    data: talentPoints
  })
}

// 获取天赋统计数据
export const getTalentStatistics = () => {
  return request({
    url: '/talent/admin/stats',
    method: 'get'
  })
}

// 获取用户的模拟日志
export const getUserSimulationLogs = (userId, simulationType) => {
  return request({
    url: `/talent/admin/logs/${userId}`,
    method: 'get',
    params: { simulationType }
  })
}

// 初始化用户天赋
export const initUserTalent = (userId) => {
  return request({
    url: `/talent/admin/init/${userId}`,
    method: 'post'
  })
}
