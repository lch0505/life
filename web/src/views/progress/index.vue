<template>
  <div class="progress-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>👤 人生进度</span>
            </div>
          </template>
          <div class="life-progress-content">
            <div class="progress-display">
              <el-progress
                type="dashboard"
                :percentage="parseFloat(lifeProgress?.progress || 0)"
                :width="180"
                :stroke-width="12"
                color="#67c23a"
              >
                <div class="dashboard-text">
                  <span class="percentage">{{ lifeProgress?.progress || '0.00' }}%</span>
                  <span class="label">人生进度</span>
                </div>
              </el-progress>
            </div>
            <div class="progress-details">
              <div class="detail-row">
                <span class="label">出生日期</span>
                <span class="value">{{ lifeProgress?.birthDate || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">预期寿命</span>
                <span class="value">{{ lifeProgress?.lifeExpectancy || 80 }} 岁</span>
              </div>
              <div class="detail-row">
                <span class="label">已活年龄</span>
                <span class="value highlight">{{ lifeProgress?.livedYears || 0 }} 岁</span>
              </div>
              <div class="detail-row">
                <span class="label">已活天数</span>
                <span class="value">{{ lifeProgress?.livedDays || 0 }} 天</span>
              </div>
              <div class="detail-row">
                <span class="label">已活月数</span>
                <span class="value">{{ lifeProgress?.livedMonths || 0 }} 个月</span>
              </div>
              <div class="detail-row">
                <span class="label">已活周数</span>
                <span class="value">{{ lifeProgress?.livedWeeks || 0 }} 周</span>
              </div>
              <div class="detail-row">
                <span class="label">剩余天数</span>
                <span class="value warning">{{ lifeProgress?.remainingDays || 0 }} 天</span>
              </div>
            </div>
          </div>
          <el-divider />
          <div class="progress-bar-section">
            <div class="progress-bar-container">
              <div class="progress-bar">
                <div 
                  class="progress-bar-inner" 
                  :style="{ width: lifeProgress?.progress || '0%', background: 'linear-gradient(90deg, #67c23a, #95d475)' }"
                ></div>
              </div>
              <div class="progress-labels">
                <span>出生</span>
                <span>现在</span>
                <span>80岁</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>📅 年度进度</span>
            </div>
          </template>
          <div class="year-progress-content">
            <div class="progress-display">
              <el-progress
                type="dashboard"
                :percentage="parseFloat(yearProgress?.progress || 0)"
                :width="180"
                :stroke-width="12"
                color="#409EFF"
              >
                <div class="dashboard-text">
                  <span class="percentage">{{ yearProgress?.progress || '0.00' }}%</span>
                  <span class="label">年度进度</span>
                </div>
              </el-progress>
            </div>
            <div class="progress-details">
              <div class="detail-row">
                <span class="label">当前年份</span>
                <span class="value">{{ yearProgress?.year }} 年</span>
              </div>
              <div class="detail-row">
                <span class="label">当前日期</span>
                <span class="value highlight">{{ yearProgress?.month }}月{{ yearProgress?.day }}日</span>
              </div>
              <div class="detail-row">
                <span class="label">当前周数</span>
                <span class="value">第 {{ yearProgress?.weekOfYear || 0 }} 周</span>
              </div>
              <div class="detail-row">
                <span class="label">本周第几天</span>
                <span class="value">{{ getWeekDayName(yearProgress?.dayOfWeek) }}</span>
              </div>
              <div class="detail-row">
                <span class="label">已过去</span>
                <span class="value">{{ yearProgress?.pastDays || 0 }} 天</span>
              </div>
              <div class="detail-row">
                <span class="label">剩余</span>
                <span class="value warning">{{ yearProgress?.remainingDays || 0 }} 天</span>
              </div>
            </div>
          </div>
          <el-divider />
          <div class="month-progress-section">
            <h4>📆 本月进度</h4>
            <div class="progress-bar-container">
              <el-progress
                :percentage="parseFloat(yearProgress?.monthProgress || 0)"
                :stroke-width="20"
                :show-text="true"
                color="#409EFF"
              />
            </div>
            <div class="month-info">
              <span>本月共 {{ yearProgress?.daysInMonth || 0 }} 天</span>
              <span>已过 {{ yearProgress?.daysPassedInMonth || 0 }} 天</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="second-row">
      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>🌿 季节进度</span>
            </div>
          </template>
          <div class="season-progress">
            <div class="season-info">
              <div class="current-season">
                <span class="season-icon">{{ getSeasonIcon(seasonProgress?.season) }}</span>
                <span class="season-name">{{ seasonProgress?.season || '-' }}</span>
              </div>
              <div class="season-date">
                <span>{{ seasonProgress?.seasonStart }} ~ {{ seasonProgress?.seasonEnd }}</span>
              </div>
            </div>
            <div class="progress-bar-container" style="margin-top: 20px">
              <el-progress
                :percentage="parseFloat(seasonProgress?.progress || 0)"
                :stroke-width="16"
                :text-inside="true"
                :color="getSeasonColor(seasonProgress?.season)"
              />
            </div>
            <div class="season-stats">
              <div class="stat-item">
                <span class="stat-label">已过</span>
                <span class="stat-value">{{ seasonProgress?.pastDays || 0 }} 天</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">剩余</span>
                <span class="stat-value warning">{{ seasonProgress?.remainingDays || 0 }} 天</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">共计</span>
                <span class="stat-value">{{ seasonProgress?.totalDays || 0 }} 天</span>
              </div>
            </div>
            <el-divider />
            <div class="next-season">
              <span>下一个季节：</span>
              <span class="next-name">{{ seasonProgress?.nextSeason?.name || '-' }}</span>
              <span class="next-date">（{{ seasonProgress?.nextSeason?.start || '-' }} 开始）</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="progress-card">
          <template #header>
            <div class="card-header">
              <span>📋 本周进度</span>
            </div>
          </template>
          <div class="week-progress">
            <div class="week-info">
              <div class="week-range">
                <span class="week-label">本周日期</span>
                <span class="week-value">{{ weekProgress?.startOfWeek }} ~ {{ weekProgress?.endOfWeek }}</span>
              </div>
              <div class="today-info">
                <span class="today-label">今天</span>
                <span class="today-value highlight">{{ weekProgress?.dayOfWeekName || '-' }}</span>
              </div>
            </div>
            <div class="progress-bar-container" style="margin-top: 20px">
              <el-progress
                :percentage="parseFloat(weekProgress?.progress || 0)"
                :stroke-width="16"
                :text-inside="true"
                color="#e6a23c"
              />
            </div>
            <div class="week-stats">
              <div class="stat-item">
                <span class="stat-label">已过</span>
                <span class="stat-value">{{ weekProgress?.pastDays || 0 }} 天</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">剩余</span>
                <span class="stat-value warning">{{ weekProgress?.remainingDays || 0 }} 天</span>
              </div>
            </div>
            <el-divider />
            <div class="week-days">
              <div 
                class="day-item" 
                v-for="(day, index) in weekDays" 
                :key="index"
                :class="{ active: index + 1 <= (weekProgress?.dayOfWeek || 0), today: index + 1 === weekProgress?.dayOfWeek }"
              >
                <span class="day-name">{{ day }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getAllProgress } from '@/api/progress'

const userStore = useUserStore()

const lifeProgress = ref({})
const yearProgress = ref({})
const seasonProgress = ref({})
const weekProgress = ref({})

const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

const getWeekDayName = (day) => {
  const names = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  return day ? names[day - 1] : '-'
}

const getSeasonIcon = (season) => {
  const icons = {
    '春季': '🌸',
    '夏季': '☀️',
    '秋季': '🍂',
    '冬季': '❄️'
  }
  return icons[season] || '🍀'
}

const getSeasonColor = (season) => {
  const colors = {
    '春季': '#67c23a',
    '夏季': '#e6a23c',
    '秋季': '#f56c6c',
    '冬季': '#409EFF'
  }
  return colors[season] || '#409EFF'
}

const fetchData = async () => {
  try {
    const userId = userStore.userInfo?.userId
    const res = await getAllProgress(userId)
    
    lifeProgress.value = res.data.life
    yearProgress.value = res.data.year
    seasonProgress.value = res.data.season
    weekProgress.value = res.data.week
  } catch (error) {
    console.error('获取进度数据失败:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="scss" scoped>
.progress-container {
  .progress-card {
    margin-bottom: 20px;

    .card-header {
      font-weight: 600;
      font-size: 16px;
    }

    .life-progress-content,
    .year-progress-content {
      display: flex;
      gap: 32px;

      .progress-display {
        flex-shrink: 0;
        display: flex;
        justify-content: center;
        align-items: center;

        .dashboard-text {
          text-align: center;

          .percentage {
            display: block;
            font-size: 28px;
            font-weight: 600;
            color: #333;
          }

          .label {
            display: block;
            font-size: 12px;
            color: #999;
          }
        }
      }

      .progress-details {
        flex: 1;

        .detail-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .label {
            color: #666;
            font-size: 14px;
          }

          .value {
            color: #333;
            font-size: 14px;
            font-weight: 500;

            &.highlight {
              color: #409eff;
            }

            &.warning {
              color: #e6a23c;
            }
          }
        }
      }
    }

    .progress-bar-section {
      .progress-bar-container {
        .progress-bar {
          width: 100%;
          height: 24px;
          background: #e8e8e8;
          border-radius: 12px;
          overflow: hidden;
          position: relative;

          .progress-bar-inner {
            height: 100%;
            border-radius: 12px;
            transition: width 0.3s ease;
          }
        }

        .progress-labels {
          display: flex;
          justify-content: space-between;
          margin-top: 8px;
          font-size: 12px;
          color: #999;
        }
      }
    }

    .month-progress-section {
      h4 {
        margin-bottom: 16px;
        color: #333;
      }

      .month-info {
        display: flex;
        justify-content: space-between;
        margin-top: 12px;
        font-size: 14px;
        color: #666;
      }
    }
  }

  .second-row {
    .season-progress {
      .season-info {
        text-align: center;

        .current-season {
          margin-bottom: 8px;

          .season-icon {
            font-size: 32px;
            margin-right: 8px;
          }

          .season-name {
            font-size: 24px;
            font-weight: 600;
            color: #333;
          }
        }

        .season-date {
          color: #999;
          font-size: 14px;
        }
      }

      .season-stats {
        display: flex;
        justify-content: space-around;
        margin-top: 20px;

        .stat-item {
          text-align: center;

          .stat-label {
            display: block;
            font-size: 12px;
            color: #999;
          }

          .stat-value {
            display: block;
            font-size: 20px;
            font-weight: 600;
            color: #333;

            &.warning {
              color: #e6a23c;
            }
          }
        }
      }

      .next-season {
        text-align: center;
        font-size: 14px;
        color: #666;

        .next-name {
          color: #409eff;
          font-weight: 500;
        }

        .next-date {
          color: #999;
        }
      }
    }

    .week-progress {
      .week-info {
        .week-range,
        .today-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          .week-label,
          .today-label {
            color: #666;
            font-size: 14px;
          }

          .week-value,
          .today-value {
            color: #333;
            font-size: 14px;
            font-weight: 500;

            &.highlight {
              color: #409eff;
            }
          }
        }
      }

      .week-stats {
        display: flex;
        justify-content: space-around;
        margin-top: 20px;

        .stat-item {
          text-align: center;

          .stat-label {
            display: block;
            font-size: 12px;
            color: #999;
          }

          .stat-value {
            display: block;
            font-size: 20px;
            font-weight: 600;
            color: #333;

            &.warning {
              color: #e6a23c;
            }
          }
        }
      }

      .week-days {
        display: flex;
        justify-content: space-between;
        margin-top: 16px;

        .day-item {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          display: flex;
          justify-content: center;
          align-items: center;
          background: #f5f7fa;
          color: #999;
          font-size: 12px;

          &.active {
            background: #ecf5ff;
            color: #409eff;
          }

          &.today {
            background: #409eff;
            color: #fff;
            font-weight: 600;
          }
        }
      }
    }
  }
}
</style>
