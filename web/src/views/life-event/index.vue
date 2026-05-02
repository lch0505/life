<template>
  <div class="life-event-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="today-event-card">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <span class="title-icon">🎲</span>
                <span class="title-text">今日随机事件</span>
              </div>
              <div class="header-actions">
                <el-tag :type="getEventTypeTagType(todayEvent?.eventType)" size="large">
                  {{ eventTypeText }}
                </el-tag>
              </div>
            </div>
          </template>

          <div v-if="loadingTodayEvent" class="loading-container">
            <el-empty description="加载中..." />
          </div>

          <div v-else-if="todayEvent" class="event-content">
            <div class="event-header">
              <div class="event-icon-wrapper" :style="{ background: getEventGradient(todayEvent.eventType) }">
                <span class="event-icon">{{ todayEvent.event.icon || '🎲' }}</span>
              </div>
              <div class="event-info">
                <h3 class="event-name">{{ todayEvent.event.eventName }}</h3>
                <p class="event-date">{{ formatDate(todayEvent.event.eventDate) }}</p>
              </div>
              <div class="event-status">
                <el-tag v-if="todayEvent.event.isApplied === 1" type="success" effect="dark">
                  <el-icon><CircleCheckFilled /></el-icon>
                  已应用
                </el-tag>
                <el-tag v-else type="warning">
                  <el-icon><WarningFilled /></el-icon>
                  待应用
                </el-tag>
              </div>
            </div>

            <div class="event-description">
              <p>{{ todayEvent.event.eventDescription }}</p>
            </div>

            <div class="event-impact">
              <h4>事件影响</h4>
              <div class="impact-item">
                <span class="impact-label">影响属性</span>
                <span class="impact-value">
                  <el-tag :type="todayEvent.event.impactValue > 0 ? 'success' : 'danger'">
                    {{ impactTypeText }}
                  </el-tag>
                </span>
              </div>
              <div class="impact-item">
                <span class="impact-label">影响数值</span>
                <span class="impact-value">
                  <span :class="['impact-number', todayEvent.event.impactValue > 0 ? 'positive' : 'negative']">
                    {{ todayEvent.event.impactValue > 0 ? '+' : '' }}{{ todayEvent.event.impactValue }}
                  </span>
                  点
                </span>
              </div>
            </div>

            <div class="event-actions">
              <el-button 
                v-if="todayEvent.event.isApplied !== 1"
                type="primary" 
                size="large"
                :loading="applying"
                @click="handleApplyEvent"
              >
                <el-icon><Check /></el-icon>
                应用事件效果
              </el-button>
              <el-button v-else size="large" disabled>
                <el-icon><CircleCheckFilled /></el-icon>
                效果已应用
              </el-button>
            </div>
          </div>
        </el-card>

        <el-card class="history-card">
          <template #header>
            <div class="card-header">
              <span class="title-icon">📜</span>
              <span class="title-text">历史事件记录</span>
            </div>
          </template>

          <div v-if="loadingHistory" class="loading-container">
            <el-empty description="加载中..." />
          </div>

          <div v-else-if="historyEvents.length === 0" class="empty-container">
            <el-empty description="暂无历史事件记录" />
          </div>

          <div v-else class="history-list">
            <div 
              v-for="event in historyEvents" 
              :key="event.id" 
              class="history-item"
              :class="getEventTypeClass(event.eventType)"
            >
              <div class="history-icon-wrapper" :style="{ background: getEventGradient(event.eventType) }">
                <span class="history-icon">{{ getEventIcon(event.eventType) }}</span>
              </div>
              <div class="history-info">
                <div class="history-name-row">
                  <span class="history-name">{{ event.eventName }}</span>
                  <el-tag :type="getHistoryEventTypeTag(event.eventType)" size="small">
                    {{ getHistoryEventTypeText(event.eventType) }}
                  </el-tag>
                </div>
                <div class="history-detail">
                  <span class="history-date">{{ formatDate(event.eventDate) }}</span>
                  <span class="history-impact">
                    影响 {{ getImpactTypeText(event.impactType) }} 
                    <span :class="event.impactValue > 0 ? 'positive' : 'negative'">
                      {{ event.impactValue > 0 ? '+' : '' }}{{ event.impactValue }}
                    </span>
                  </span>
                </div>
              </div>
              <div class="history-status">
                <el-icon v-if="event.isApplied === 1" class="status-applied"><CircleCheckFilled /></el-icon>
                <el-icon v-else class="status-pending"><Clock /></el-icon>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <span class="title-icon">📊</span>
              <span class="title-text">事件统计</span>
            </div>
          </template>

          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-icon good">🍀</div>
              <div class="stat-info">
                <div class="stat-label">好运buff</div>
                <div class="stat-value">{{ eventStats.goodEvents || 0 }}</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon bad">🍂</div>
              <div class="stat-info">
                <div class="stat-label">小倒霉</div>
                <div class="stat-value">{{ eventStats.badEvents || 0 }}</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon surprise">✨</div>
              <div class="stat-info">
                <div class="stat-label">意外惊喜</div>
                <div class="stat-value">{{ eventStats.surpriseEvents || 0 }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="guide-card">
          <template #header>
            <div class="card-header">
              <span class="title-icon">📖</span>
              <span class="title-text">事件说明</span>
            </div>
          </template>

          <div class="guide-content">
            <div class="guide-item">
              <div class="guide-icon good">🍀</div>
              <div class="guide-info">
                <h4>好运buff</h4>
                <p>正面事件，会增加你的某个天赋属性值，让你的人生更加顺遂。</p>
              </div>
            </div>
            <div class="guide-item">
              <div class="guide-icon bad">🍂</div>
              <div class="guide-info">
                <h4>小倒霉</h4>
                <p>负面事件，会减少你的某个天赋属性值，但不要气馁，这只是暂时的。</p>
              </div>
            </div>
            <div class="guide-item">
              <div class="guide-icon surprise">✨</div>
              <div class="guide-info">
                <h4>意外惊喜</h4>
                <p>稀有事件，会大幅增加你的某个天赋属性值，遇到了就是赚到！</p>
              </div>
            </div>
          </div>

          <div class="guide-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>每天登录后会自动生成一个随机事件，点击"应用事件效果"即可将影响应用到你的天赋属性中。</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getTodayEvent,
  applyEventImpact,
  getEventHistory,
  getEventStats
} from '@/api/lifeEvent'

const loadingTodayEvent = ref(false)
const loadingHistory = ref(false)
const applying = ref(false)

const todayEvent = ref(null)
const historyEvents = ref([])
const eventStats = ref({})

const eventTypeText = computed(() => {
  if (!todayEvent.value) return ''
  switch (todayEvent.value.eventType) {
    case 1: return '好运buff'
    case 2: return '小倒霉'
    case 3: return '意外惊喜'
    default: return '未知'
  }
})

const impactTypeText = computed(() => {
  if (!todayEvent.value) return ''
  return getImpactTypeText(todayEvent.value.event.impactType)
})

const getImpactTypeText = (type) => {
  switch (type) {
    case 'appearance': return '颜值'
    case 'intelligence': return '智商'
    case 'wealth': return '财运'
    case 'health': return '健康'
    case 'emotional': return '情商'
    case 'luck': return '运气'
    default: return '未知'
  }
}

const getEventTypeTagType = (type) => {
  switch (type) {
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'warning'
    default: return 'info'
  }
}

const getEventGradient = (type) => {
  switch (type) {
    case 1: return 'linear-gradient(135deg, #67c23a 0%, #95d475 100%)'
    case 2: return 'linear-gradient(135deg, #f56c6c 0%, #f78989 100%)'
    case 3: return 'linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%)'
    default: return 'linear-gradient(135deg, #409EFF 0%, #79bbff 100%)'
  }
}

const getEventTypeClass = (type) => {
  switch (type) {
    case 1: return 'good-event'
    case 2: return 'bad-event'
    case 3: return 'surprise-event'
    default: return ''
  }
}

const getEventIcon = (type) => {
  switch (type) {
    case 1: return '🍀'
    case 2: return '🍂'
    case 3: return '✨'
    default: return '🎲'
  }
}

const getHistoryEventTypeTag = (type) => {
  switch (type) {
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'warning'
    default: return 'info'
  }
}

const getHistoryEventTypeText = (type) => {
  switch (type) {
    case 1: return '好运'
    case 2: return '倒霉'
    case 3: return '惊喜'
    default: return '未知'
  }
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekDay = weekDays[d.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
}

const fetchTodayEvent = async () => {
  loadingTodayEvent.value = true
  try {
    const res = await getTodayEvent()
    todayEvent.value = {
      event: res.data.event,
      eventType: res.data.event.eventType,
      eventTypeText: res.data.eventTypeText
    }
  } catch (error) {
    console.error('获取今日事件失败:', error)
    ElMessage.error('获取今日事件失败')
  } finally {
    loadingTodayEvent.value = false
  }
}

const fetchHistoryEvents = async () => {
  loadingHistory.value = true
  try {
    const res = await getEventHistory(10)
    historyEvents.value = res.data || []
  } catch (error) {
    console.error('获取历史事件失败:', error)
  } finally {
    loadingHistory.value = false
  }
}

const fetchEventStats = async () => {
  try {
    const res = await getEventStats()
    eventStats.value = res.data || {}
  } catch (error) {
    console.error('获取事件统计失败:', error)
  }
}

const handleApplyEvent = async () => {
  if (!todayEvent.value || todayEvent.value.event.isApplied === 1) {
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要应用此事件效果吗？\n\n将对 ${impactTypeText.value} 属性产生 ${todayEvent.value.event.impactValue > 0 ? '+' : ''}${todayEvent.value.event.impactValue} 点影响。`,
      '确认应用',
      {
        confirmButtonText: '确定应用',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    applying.value = true
    const res = await applyEventImpact(todayEvent.value.event.id)
    
    if (res.code === 200) {
      todayEvent.value.event.isApplied = 1
      ElMessage.success('事件效果已成功应用！')
      fetchHistoryEvents()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('应用失败，请重试')
    }
  } finally {
    applying.value = false
  }
}

onMounted(() => {
  fetchTodayEvent()
  fetchHistoryEvents()
  fetchEventStats()
})
</script>

<style lang="scss" scoped>
.life-event-container {
  .today-event-card,
  .history-card,
  .stats-card,
  .guide-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 16px;

    .header-title {
      display: flex;
      align-items: center;
    }

    .title-icon {
      margin-right: 8px;
      font-size: 18px;
    }

    .title-text {
      font-size: 16px;
    }
  }

  .loading-container,
  .empty-container {
    padding: 40px 0;
  }

  .event-content {
    .event-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #f0f0f0;

      .event-icon-wrapper {
        width: 64px;
        height: 64px;
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

        .event-icon {
          font-size: 32px;
        }
      }

      .event-info {
        flex: 1;

        .event-name {
          font-size: 20px;
          font-weight: 600;
          color: #333;
          margin: 0 0 4px 0;
        }

        .event-date {
          font-size: 14px;
          color: #909399;
        }
      }

      .event-status {
        margin-left: 16px;
      }
    }

    .event-description {
      margin-bottom: 24px;
      padding: 20px;
      background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
      border-radius: 12px;

      p {
        font-size: 15px;
        line-height: 1.8;
        color: #333;
        margin: 0;
      }
    }

    .event-impact {
      margin-bottom: 24px;

      h4 {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 16px;
        padding-bottom: 8px;
        border-bottom: 1px solid #f0f0f0;
      }

      .impact-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px dashed #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .impact-label {
          font-size: 14px;
          color: #666;
        }

        .impact-value {
          font-size: 14px;
          font-weight: 500;

          .impact-number {
            font-size: 20px;
            font-weight: 600;

            &.positive {
              color: #67c23a;
            }

            &.negative {
              color: #f56c6c;
            }
          }
        }
      }
    }

    .event-actions {
      display: flex;
      justify-content: center;
      padding-top: 16px;
    }
  }

  .history-list {
    .history-item {
      display: flex;
      align-items: center;
      padding: 16px;
      border-radius: 12px;
      margin-bottom: 12px;
      border: 2px solid #f0f0f0;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }

      &.good-event {
        border-left: 4px solid #67c23a;
      }

      &.bad-event {
        border-left: 4px solid #f56c6c;
      }

      &.surprise-event {
        border-left: 4px solid #e6a23c;
      }

      .history-icon-wrapper {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        flex-shrink: 0;

        .history-icon {
          font-size: 24px;
        }
      }

      .history-info {
        flex: 1;
        min-width: 0;

        .history-name-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 6px;

          .history-name {
            font-size: 14px;
            font-weight: 500;
            color: #333;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }

        .history-detail {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 12px;
          color: #909399;

          .history-date {
            flex-shrink: 0;
          }

          .history-impact {
            flex-shrink: 0;

            .positive {
              color: #67c23a;
              font-weight: 500;
            }

            .negative {
              color: #f56c6c;
              font-weight: 500;
            }
          }
        }
      }

      .history-status {
        margin-left: 12px;

        .status-applied {
          color: #67c23a;
          font-size: 20px;
        }

        .status-pending {
          color: #e6a23c;
          font-size: 20px;
        }
      }
    }
  }

  .stats-content {
    .stat-item {
      display: flex;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px dashed #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .stat-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        font-size: 20px;

        &.good {
          background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
        }

        &.bad {
          background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
        }

        &.surprise {
          background: linear-gradient(135deg, #fdf6ec 0%, #faecd8 100%);
        }
      }

      .stat-info {
        .stat-label {
          font-size: 13px;
          color: #666;
          margin-bottom: 2px;
        }

        .stat-value {
          font-size: 20px;
          font-weight: 600;
          color: #333;
        }
      }
    }
  }

  .guide-content {
    .guide-item {
      display: flex;
      align-items: flex-start;
      padding: 12px 0;
      border-bottom: 1px dashed #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .guide-icon {
        width: 36px;
        height: 36px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;
        font-size: 18px;
        flex-shrink: 0;

        &.good {
          background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
        }

        &.bad {
          background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
        }

        &.surprise {
          background: linear-gradient(135deg, #fdf6ec 0%, #faecd8 100%);
        }
      }

      .guide-info {
        flex: 1;
        min-width: 0;

        h4 {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin: 0 0 4px 0;
        }

        p {
          font-size: 12px;
          color: #666;
          margin: 0;
          line-height: 1.6;
        }
      }
    }
  }

  .guide-tip {
    margin-top: 16px;
    padding: 12px;
    background: linear-gradient(135deg, #ecf5ff 0%, #daecff 100%);
    border-radius: 8px;
    display: flex;
    align-items: flex-start;
    gap: 8px;

    .el-icon {
      color: #409EFF;
      font-size: 16px;
      flex-shrink: 0;
      margin-top: 2px;
    }

    span {
      font-size: 12px;
      color: #409EFF;
      line-height: 1.6;
    }
  }
}
</style>
