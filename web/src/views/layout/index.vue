<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <span class="logo-icon">🌍</span>
        <span v-show="!isCollapse" class="logo-text">人生进度条</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>进度总览</template>
        </el-menu-item>
        <el-menu-item index="/dashboard/progress">
          <el-icon><Timer /></el-icon>
          <template #title>进度查看</template>
        </el-menu-item>
        <el-menu-item index="/dashboard/countdown">
          <el-icon><Clock /></el-icon>
          <template #title>倒计时</template>
        </el-menu-item>
        <el-menu-item index="/dashboard/goal">
          <el-icon><Target /></el-icon>
          <template #title>年度目标</template>
        </el-menu-item>
        <el-menu-item index="/dashboard/work">
          <el-icon><Briefcase /></el-icon>
          <template #title>职场进度</template>
        </el-menu-item>
        <el-menu-item index="/dashboard/talent">
          <el-icon><Star /></el-icon>
          <template #title>天赋加点</template>
        </el-menu-item>
        <el-menu-item index="/dashboard/life-event">
          <el-icon><Dice /></el-icon>
          <template #title>随机人生事件</template>
        </el-menu-item>
        <el-sub-menu v-if="isAdmin" index="admin-menu">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/dashboard/admin">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/dashboard/admin/talent">
            <el-icon><Star /></el-icon>
            <template #title>天赋管理</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container class="layout-main-container">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-tag v-if="isAdmin" type="danger" effect="dark" size="small">管理员</el-tag>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人设置
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const isAdmin = computed(() => userStore.isAdmin)

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  const matchedRoute = route.matched.slice(-1)[0]
  return matchedRoute?.meta?.title || '页面'
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/dashboard/user')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/login')
      }).catch(() => {})
      break
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  width: 100%;
  height: 100%;

  .layout-aside {
    background-color: #304156;
    transition: width 0.3s;

    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 16px;
      background-color: #263445;

      .logo-icon {
        font-size: 28px;
      }

      .logo-text {
        color: #fff;
        font-size: 18px;
        font-weight: 600;
        margin-left: 10px;
        white-space: nowrap;
      }
    }

    .el-menu {
      border-right: none;
    }
  }

  .layout-main-container {
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .layout-header {
      height: 60px;
      background: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;

      .header-left {
        display: flex;
        align-items: center;

        .collapse-btn {
          font-size: 20px;
          cursor: pointer;
          margin-right: 16px;
          padding: 4px;

          &:hover {
            color: #409EFF;
          }
        }
      }

      .header-right {
        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          padding: 0 10px;
          border-radius: 4px;

          &:hover {
            background: #f5f7fa;
          }

          .username {
            margin: 0 8px;
            font-size: 14px;
            color: #333;
          }

          .el-tag {
            margin-right: 8px;
          }
        }
      }
    }

    .layout-main {
      flex: 1;
      overflow-y: auto;
      background: #f5f7fa;
      padding: 20px;
    }
  }
}
</style>
