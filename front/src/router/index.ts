import { createRouter, createWebHistory } from 'vue-router'
import pinia from '@/pinia'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: false },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览' }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: () => import('@/views/Contract.vue'),
        meta: { title: '合同管理' }
      },
      {
        path: 'cylinder',
        name: 'CylinderManagement',
        component: () => import('@/views/CylinderManagement.vue'),
        meta: { title: '气瓶管理' }
      },
      {
        path: 'renewal',
        name: 'Renewal',
        component: () => import('@/views/Renewal.vue'),
        meta: { title: '续约管理' }
      },
      {
        path: 'unit-mgr',
        name: 'UnitMgr',
        component: () => import('@/views/UnitManagement.vue'),
        meta: { title: '单位管理', role: 'ADMIN' }
      },
      {
        path: 'user-mgr',
        name: 'UserMgr',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', role: 'ADMIN' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'message',
        name: 'Message',
        component: () => import('@/views/Message.vue'),
        meta: { title: '我的消息' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore(pinia)

  // 判断是否需要登录：任意匹配到的路由记录要求登录即可
  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth)
  if (requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 判断角色权限：以第一个声明 role 的记录为准
  const roleRecord = to.matched.find(record => record.meta?.role)
  const requiredRole = roleRecord?.meta?.role
  if (requiredRole && userStore.currentRole !== requiredRole) {
    next('/')
    return
  }

  next()
})

export default router

