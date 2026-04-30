<template>
  <div class="dashboard-container">
    <div class="welcome-card">
      <div class="welcome-info">
        <h2>你好，{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}！ 👋</h2>
        <p class="welcome-date">今天是 {{ currentDate }}</p>
        <p class="welcome-slogan">地球online持续进行中...</p>
      </div>
      <div class="welcome-stats">
        <div class="stat-item">
          <span class="stat-label">本周进度</span>
          <span class="stat-value">{{ weekProgress }}%</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">本月进度</span>
          <span class="stat-value">{{ monthProgress }}%</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">本年进度</span>
          <span class="stat-value">{{ yearProgress }}%</span>
        </div>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>📊 人生进度</span>
              <router-link to="/dashboard/progress" class="more-link">查看详情</router-link>
            </div>
          </template>
          <div class="life-progress">
            <div class="progress-circle">
              <el-progress
                type="circle"
                :percentage="parseFloat(lifeProgress?.progress || 0)"
                :width="120"
                color="#67c23a"
              />
            </div>
            <div class="progress-info">
              <p><strong>预期寿命：</strong>{{ lifeProgress?.lifeExpectancy || 80 }} 岁</p>
              <p><strong>已活天数：</strong>{{ lifeProgress?.livedDays || 0 }} 天</p>
              <p><strong>剩余天数：</strong>{{ lifeProgress?.remainingDays || 0 }} 天</p>
              <p><strong>已活年龄：</strong>{{ lifeProgress?.livedYears || 0 }} 岁</p>
            </div>
          </div>
          <div class="progress-bar-container">
            <div class="progress-bar">
              <div 
                class="progress-bar-inner" 
                :style="{ width: lifeProgress?.progress || '0%', background: 'linear-gradient(90deg, #67c23a, #95d475)' }"
              ></div>
            </div>
            <div class="progress-bar-text">人生进度: {{ lifeProgress?.progress || '0.00' }}%</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>📅 年度进度</span>
              <router-link to="/dashboard/progress" class="more-link">查看详情</router-link>
            </div>
          </template>
          <div class="life-progress">
            <div class="progress-circle">
              <el-progress
                type="circle"
                :percentage="parseFloat(yearProgressData?.progress || 0)"
                :width="120"
                color="#409EFF"
              />
            </div>
            <div class="progress-info">
              <p><strong>当前年份：</strong>{{ yearProgressData?.year }} 年</p>
              <p><strong>已过去：</strong>{{ yearProgressData?.pastDays || 0 }} 天</p>
              <p><strong>剩余：</strong>{{ yearProgressData?.remainingDays || 0 }} 天</p>
              <p><strong>当前日期：</strong>{{ yearProgressData?.month }}月{{ yearProgressData?.day }}日</p>
            </div>
          </div>
          <div class="progress-bar-container">
            <div class="progress-bar">
              <div 
                class="progress-bar-inner" 
                :style="{ width: yearProgressData?.progress || '0%', background: 'linear-gradient(90deg, #409EFF, #79bbff)' }"
              ></div>
            </div>
            <div class="progress-bar-text">年度进度: {{ yearProgressData?.progress || '0.00' }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="second-row">
      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>⏱️ 倒计时</span>
              <router-link to="/dashboard/countdown" class="more-link">查看全部</router-link>
            </div>
          </template>
          <div class="countdown-list">
            <el-empty v-if="countdownList.length === 0" description="暂无倒计时" />
            <div v-else class="countdown-item" v-for="item in countdownList.slice(0, 4)" :key="item.id">
              <div class="countdown-left">
                <span class="countdown-title">{{ item.title }}</span>
                <span class="countdown-category">{{ item.category }}</span>
              </div>
              <div class="countdown-right">
                <div class="countdown-days">{{ item.days }}<span class="unit">天</span></div>
                <div class="countdown-time">{{ item.hours }}:{{ item.minutes.toString().padStart(2, '0') }}:{{ item.seconds.toString().padStart(2, '0') }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>🎯 年度目标</span>
              <router-link to="/dashboard/goal" class="more-link">查看全部</router-link>
            </div>
          </template>
          <div class="goal-list">
            <el-empty v-if="goalList.length === 0" description="暂无年度目标" />
            <div v-else class="goal-item" v-for="item in goalList.slice(0, 4)" :key="item.id">
              <div class="goal-info">
                <span class="goal-title">{{ item.title }}</span>
                <span class="goal-status" :class="'status-' + item.status">
                  {{ getGoalStatusText(item.status) }}
                </span>
              </div>
              <el-progress
                :percentage="parseFloat(item.progress || 0)"
                :stroke-width="8"
                :show-text="false"
                style="margin-top: 8px"
              />
              <span class="goal-progress">{{ item.progress || '0.00' }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAllProgress } from '@/api/progress'
import { getCountdownListWithRemaining } from '@/api/countdown'
import { getGoalListWithProgress } from '@/api/goal'
import dayjs from 'dayjs'

const userStore = useUserStore()

const currentDate = ref('')
const weekProgress = ref('0.00')
const monthProgress = ref('0.00')
const yearProgress = ref('0.00')

const lifeProgress = ref({})
const yearProgressData = ref({})
const countdownList = ref([])
const goalList = ref([])

let countdownTimer = null

const updateCurrentDate = () => {
  currentDate.value = dayjs().format('YYYY年MM月DD日 dddd HH:mm:ss')
}

const fetchProgressData = async () => {
  try {
    const userId = userStore.userInfo?.userId
    const res = await getAllProgress(userId)
    
    lifeProgress.value = res.data.life
    yearProgressData.value = res.data.year
    yearProgress.value = res.data.year.progress
    monthProgress.value = res.data.year.monthProgress
    weekProgress.value = res.data.week.progress
  } catch (error) {
    console.error('获取进度数据失败:', error)
  }
}

const fetchCountdownList = async () => {
  try {
    const res = await getCountdownListWithRemaining()
    countdownList.value = res.data
  } catch (error) {
    console.error('获取倒计时列表失败:', error)
  }
}

const fetchGoalList = async () => {
  try {
    const res = await getGoalListWithProgress(dayjs().year())
    goalList.value = res.data
  } catch (error) {
    console.error('获取目标列表失败:', error)
  }
}

const getGoalStatusText = (status) => {
  const statusMap = {
    0: '未开始',
    1: '进行中',
    2: '已完成',
    3: '已取消'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  updateCurrentDate()
  fetchProgressData()
  fetchCountdownList()
  fetchGoalList()

  countdownTimer = setInterval(() => {
    updateCurrentDate()
    countdownList.value.forEach(item => {
      if (item.totalSeconds > 0) {
        item.totalSeconds--
        item.days = Math.floor(item.totalSeconds / (24 * 60 * 60))
        item.hours = Math.floor((item.totalSeconds % (24 * 60 * 60)) / (60 * 60))
        item.minutes = Math.floor((item.totalSeconds % (60 * 60)) / 60)
        item.seconds = item.totalSeconds % 60
      }
    })
  }, 1000)
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .welcome-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 20px;
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .welcome-info {
      h2 {
        font-size: 24px;
        margin-bottom: 8px;
      }

      .welcome-date {
        font-size: 16px;
        opacity: 0.9;
        margin-bottom: 4px;
      }

      .welcome-slogan {
        font-size: 14px;
        opacity: 0.8;
      }
    }

    .welcome-stats {
      display: flex;
      gap: 32px;

      .stat-item {
        text-align: center;

        .stat-label {
          display: block;
          font-size: 14px;
          opacity: 0.8;
          margin-bottom: 4px;
        }

        .stat-value {
          display: block;
          font-size: 24px;
          font-weight: 600;
        }
      }
    }
  }

  .progress-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      font-size: 16px;

      .more-link {
        font-size: 14px;
        color: #409eff;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .life-progress {
      display: flex;
      align-items: center;
      gap: 32px;
      margin-bottom: 20px;

      .progress-circle {
        flex-shrink: 0;
      }

      .progress-info {
        flex: 1;

        p {
          margin: 8px 0;
          color: #666;
          font-size: 14px;

          strong {
            color: #333;
          }
        }
      }
    }

    .progress-bar-container {
      .progress-bar {
        width: 100%;
        height: 20px;
        background: #e8e8e8;
        border-radius: 10px;
        overflow: hidden;
        position: relative;

        .progress-bar-inner {
          height: 100%;
          border-radius: 10px;
          transition: width 0.3s ease;
        }
      }

      .progress-bar-text {
        text-align: center;
        font-size: 14px;
        color: #666;
        margin-top: 8px;
        font-weight: 500;
      }
    }
  }

  .second-row {
    .countdown-list {
      .countdown-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .countdown-left {
          .countdown-title {
            display: block;
            font-size: 14px;
            color: #333;
            font-weight: 500;
          }

          .countdown-category {
            display: block;
            font-size: 12px;
            color: #999;
            margin-top: 4px;
          }
        }

        .countdown-right {
          text-align: right;

          .countdown-days {
            font-size: 24px;
            font-weight: 600;
            color: #409eff;

            .unit {
              font-size: 14px;
              margin-left: 4px;
            }
          }

          .countdown-time {
            font-size: 12px;
            color: #999;
            font-family: 'Courier New', monospace;
          }
        }
      }
    }

    .goal-list {
      .goal-item {
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .goal-info {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .goal-title {
            font-size: 14px;
            color: #333;
            font-weight: 500;
          }

          .goal-status {
            font-size: 12px;
            padding: 2px 8px;
            border-radius: 4px;

            &.status-0 {
              background: #f5f7fa;
              color: #909399;
            }

            &.status-1 {
              background: #ecf5ff;
              color: #409eff;
            }

            &.status-2 {
              background: #f0f9eb;
              color: #67c23a;
            }

            &.status-3 {
              background: #fef0f0;
              color: #f56c6c;
            }
          }
        }

        .goal-progress {
          font-size: 12px;
          color: #999;
          display: block;
          text-align: right;
          margin-top: 4px;
        }
      }
    }
  }
}
</style>
