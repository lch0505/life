import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '首页', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'DashboardHome',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '进度总览' }
      },
      {
        path: 'progress',
        name: 'Progress',
        component: () => import('@/views/progress/index.vue'),
        meta: { title: '进度查看' }
      },
      {
        path: 'countdown',
        name: 'Countdown',
        component: () => import('@/views/countdown/index.vue'),
        meta: { title: '倒计时' }
      },
      {
        path: 'goal',
        name: 'Goal',
        component: () => import('@/views/goal/index.vue'),
        meta: { title: '年度目标' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '个人设置' }
      },
      {
        path: 'work',
        name: 'Work',
        component: () => import('@/views/work/index.vue'),
        meta: { title: '职场进度' }
      },
      {
        path: 'talent',
        name: 'Talent',
        component: () => import('@/views/talent/index.vue'),
        meta: { title: '天赋加点' }
      },
      {
        path: 'life-event',
        name: 'LifeEvent',
        component: () => import('@/views/life-event/index.vue'),
        meta: { title: '随机人生事件' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/admin/index.vue'),
        meta: { title: '系统管理', requiresAdmin: true }
      },
      {
        path: 'admin/talent',
        name: 'AdminTalent',
        component: () => import('@/views/admin/talent.vue'),
        meta: { title: '天赋管理', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 人生进度条` : '人生进度条'

  const userStore = useUserStore()
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    return
  }

  if (to.meta.requiresAdmin && userStore.userInfo?.role !== 0) {
    next('/dashboard')
    return
  }

  if ((to.path === '/login' || to.path === '/register') && token) {
    next('/dashboard')
    return
  }

  next()
})

export default router
