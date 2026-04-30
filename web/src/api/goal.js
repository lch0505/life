import request from './request'

export const getGoalList = (year) => {
  return request({
    url: '/goal/list',
    method: 'get',
    params: { year }
  })
}

export const getGoalListWithProgress = (year) => {
  return request({
    url: '/goal/list-with-progress',
    method: 'get',
    params: { year }
  })
}

export const getGoalById = (id) => {
  return request({
    url: `/goal/${id}`,
    method: 'get'
  })
}

export const createGoal = (data) => {
  return request({
    url: '/goal/create',
    method: 'post',
    data
  })
}

export const updateGoal = (id, data) => {
  return request({
    url: `/goal/update/${id}`,
    method: 'put',
    data
  })
}

export const updateGoalProgress = (id, progress) => {
  return request({
    url: `/goal/progress/${id}`,
    method: 'put',
    params: { progress }
  })
}

export const deleteGoal = (id) => {
  return request({
    url: `/goal/${id}`,
    method: 'delete'
  })
}

export const getGoalTasks = (goalId) => {
  return request({
    url: `/goal/${goalId}/tasks`,
    method: 'get'
  })
}

export const createGoalTask = (goalId, data) => {
  return request({
    url: `/goal/${goalId}/task`,
    method: 'post',
    data
  })
}

export const updateGoalTask = (taskId, data) => {
  return request({
    url: `/goal/task/${taskId}`,
    method: 'put',
    data
  })
}

export const updateGoalTaskProgress = (taskId, progress) => {
  return request({
    url: `/goal/task/progress/${taskId}`,
    method: 'put',
    params: { progress }
  })
}

export const deleteGoalTask = (taskId) => {
  return request({
    url: `/goal/task/${taskId}`,
    method: 'delete'
  })
}
