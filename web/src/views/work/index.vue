<template>
  <div class="work-container">
    <div class="page-header">
      <h2>💼 职场进度</h2>
      <el-button type="primary" @click="openSettingDialog">
        <el-icon><Setting /></el-icon>
        职场设置
      </el-button>
    </div>

    <el-empty v-if="!hasSetting" description="请先设置职场信息" :image-size="80">
      <el-button type="primary" @click="openSettingDialog">立即设置</el-button>
    </el-empty>

    <template v-else>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="work-card">
            <template #header>
              <div class="card-header">
                <span>📈 职场工龄进度</span>
              </div>
            </template>
            <div class="progress-content">
              <div class="progress-display">
                <el-progress
                  type="dashboard"
                  :percentage="parseFloat(workProgress?.workProgress || 0)"
                  :width="180"
                  :stroke-width="12"
                  color="#67c23a"
                >
                  <div class="dashboard-text">
                    <span class="percentage">{{ workProgress?.workProgress || '0.00' }}%</span>
                    <span class="label">工龄进度</span>
                  </div>
                </el-progress>
              </div>
              <div class="progress-details">
                <div class="detail-row">
                  <span class="label">入职日期</span>
                  <span class="value">{{ workProgress?.joinDate || '-' }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">退休年龄</span>
                  <span class="value">{{ workProgress?.retirementAge || 65 }} 岁</span>
                </div>
                <div class="detail-row">
                  <span class="label">已工作</span>
                  <span class="value highlight">{{ workProgress?.workedYears || 0 }} 年 {{ displayWorkedMonths || 0 }} 个月</span>
                </div>
                <div class="detail-row">
                  <span class="label">已工作天数</span>
                  <span class="value">{{ workProgress?.workedDays || 0 }} 天</span>
                </div>
                <div class="detail-row">
                  <span class="label">剩余工作天数</span>
                  <span class="value warning">{{ workProgress?.remainingWorkDays || 0 }} 天</span>
                </div>
                <div class="detail-row">
                  <span class="label">预计退休日期</span>
                  <span class="value">{{ workProgress?.retirementDate || '-' }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="work-card">
            <template #header>
              <div class="card-header">
                <span>🎯 退休倒计时</span>
              </div>
            </template>
            <div class="countdown-content">
              <div class="countdown-display" :class="{ retired: retirementCountdown?.isRetired }">
                <div class="countdown-title">
                  {{ retirementCountdown?.isRetired ? '🎉 已退休' : '距离退休还有' }}
                </div>
                <div class="countdown-main">
                  <span class="years">{{ retirementCountdown?.years || 0 }}</span>
                  <span class="unit">年</span>
                  <span class="months">{{ retirementCountdown?.months || 0 }}</span>
                  <span class="unit">月</span>
                  <span class="days">{{ retirementCountdown?.days || 0 }}</span>
                  <span class="unit">天</span>
                </div>
                <div class="countdown-time">
                  <span class="time-value">{{ String(retirementCountdown?.hours || 0).padStart(2, '0') }}</span>
                  <span class="time-separator">:</span>
                  <span class="time-value">{{ String(retirementCountdown?.minutes || 0).padStart(2, '0') }}</span>
                  <span class="time-separator">:</span>
                  <span class="time-value">{{ String(retirementCountdown?.seconds || 0).padStart(2, '0') }}</span>
                </div>
              </div>
              <div class="countdown-info">
                <div class="info-row">
                  <span class="label">出生日期</span>
                  <span class="value">{{ retirementCountdown?.birthDate || '-' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">退休年龄</span>
                  <span class="value">{{ retirementCountdown?.retirementAge || 65 }} 岁</span>
                </div>
                <div class="info-row">
                  <span class="label">退休日期</span>
                  <span class="value">{{ retirementCountdown?.retirementDate || '-' }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="second-row">
        <el-col :span="12">
          <el-card class="work-card">
            <template #header>
              <div class="card-header">
                <span>📊 年度工作统计</span>
                <el-select v-model="selectedYear" @change="fetchYearlyStats" style="width: 120px; margin-left: auto">
                  <el-option
                    v-for="year in yearOptions"
                    :key="year"
                    :label="year + ' 年'"
                    :value="year"
                  />
                </el-select>
              </div>
            </template>
            <div class="stats-content">
              <div class="stats-grid">
                <div class="stat-item work">
                  <div class="stat-icon">💼</div>
                  <div class="stat-value">{{ yearlyStats?.workDays || 0 }}</div>
                  <div class="stat-label">上班天数</div>
                </div>
                <div class="stat-item fish">
                  <div class="stat-icon">🐟</div>
                  <div class="stat-value">{{ yearlyStats?.fishDays || 0 }}</div>
                  <div class="stat-label">摸鱼天数</div>
                </div>
                <div class="stat-item vacation">
                  <div class="stat-icon">🏖️</div>
                  <div class="stat-value">{{ yearlyStats?.vacationDays || 0 }}</div>
                  <div class="stat-label">休假天数</div>
                </div>
              </div>
              <el-divider />
              <div class="stats-chart">
                <el-progress
                  :percentage="calculateWorkPercentage"
                  :stroke-width="20"
                  color="#67c23a"
                  :show-text="false"
                />
                <div class="chart-labels">
                  <span>上班: {{ yearlyStats?.workDays || 0 }} 天</span>
                  <span>摸鱼: {{ yearlyStats?.fishDays || 0 }} 天</span>
                  <span>休假: {{ yearlyStats?.vacationDays || 0 }} 天</span>
                </div>
              </div>
              <el-divider />
              <div class="stats-info">
                <div class="info-row">
                  <span class="label">已记录天数</span>
                  <span class="value">{{ yearlyStats?.totalRecordedDays || 0 }} 天</span>
                </div>
                <div class="info-row">
                  <span class="label">年度总天数</span>
                  <span class="value">{{ yearlyStats?.totalDays || 0 }} 天</span>
                </div>
                <div class="info-row">
                  <span class="label">预估上班天数</span>
                  <span class="value">{{ yearlyStats?.estimatedWorkDays || 0 }} 天</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="work-card">
            <template #header>
              <div class="card-header">
                <span>💰 打工收入统计</span>
              </div>
            </template>
            <div class="earnings-content">
              <div class="earnings-main">
                <div class="earnings-total">
                  <div class="total-label">累计总收入</div>
                  <div class="total-value">¥ {{ formatNumber(earnings?.totalEarnings || 0) }}</div>
                </div>
              </div>
              <el-divider />
              <div class="earnings-grid">
                <div class="earnings-item">
                  <div class="item-icon">📅</div>
                  <div class="item-content">
                    <div class="item-label">月薪</div>
                    <div class="item-value">¥ {{ formatNumber(earnings?.monthlySalary || 0) }}</div>
                  </div>
                </div>
                <div class="earnings-item">
                  <div class="item-icon">📆</div>
                  <div class="item-content">
                    <div class="item-label">日薪</div>
                    <div class="item-value">¥ {{ formatNumber(earnings?.dailySalary || 0) }}</div>
                  </div>
                </div>
                <div class="earnings-item">
                  <div class="item-icon">📊</div>
                  <div class="item-content">
                    <div class="item-label">已工作月数</div>
                    <div class="item-value">{{ earnings?.workedMonths || 0 }} 个月</div>
                  </div>
                </div>
                <div class="earnings-item">
                  <div class="item-icon">⏱️</div>
                  <div class="item-content">
                    <div class="item-label">已工作天数</div>
                    <div class="item-value">{{ earnings?.workedDays || 0 }} 天</div>
                  </div>
                </div>
              </div>
              <el-divider />
              <div class="earnings-year">
                <h4>📈 今年收入</h4>
                <div class="year-stats">
                  <div class="year-stat">
                    <span class="year-label">已工作</span>
                    <span class="year-value">{{ earnings?.monthsThisYear || 0 }} 个月</span>
                  </div>
                  <div class="year-stat">
                    <span class="year-label">今年收入</span>
                    <span class="year-value highlight">¥ {{ formatNumber(earnings?.earningsThisYear || 0) }}</span>
                  </div>
                </div>
              </div>
              <el-divider />
              <div class="earnings-today">
                <div class="today-label">今日收入</div>
                <div class="today-value">¥ {{ formatNumber(earnings?.earningsToday || 0) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-dialog
      v-model="settingDialogVisible"
      title="职场设置"
      width="500px"
    >
      <el-form
        ref="settingFormRef"
        :model="settingForm"
        :rules="settingRules"
        label-width="100px"
      >
        <el-form-item label="入职日期" prop="joinDate">
          <el-date-picker
            v-model="settingForm.joinDate"
            type="date"
            placeholder="选择入职日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="退休年龄" prop="retirementAge">
          <el-input-number
            v-model="settingForm.retirementAge"
            :min="50"
            :max="75"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="月薪" prop="monthlySalary">
          <el-input-number
            v-model="settingForm.monthlySalary"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="每周工作天数" prop="workDaysPerWeek">
          <el-select v-model="settingForm.workDaysPerWeek" style="width: 100%">
            <el-option label="5 天" :value="5" />
            <el-option label="6 天" :value="6" />
            <el-option label="7 天" :value="7" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="settingDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitSetting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import {
  getWorkSetting,
  saveWorkSetting,
  getAllWorkProgress,
  getYearlyStats
} from '@/api/work'

const hasSetting = ref(false)
const workProgress = ref({})
const retirementCountdown = ref({})
const yearlyStats = ref({})
const earnings = ref({})
const setting = ref({})
const selectedYear = ref(new Date().getFullYear())
let countdownTimer = null

const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  const years = []
  for (let i = currentYear; i >= 2000; i--) {
    years.push(i)
  }
  return years
})

const calculateWorkPercentage = computed(() => {
  const total = (yearlyStats.value?.workDays || 0) + (yearlyStats.value?.fishDays || 0) + (yearlyStats.value?.vacationDays || 0)
  if (total === 0) return 0
  return Math.round((yearlyStats.value?.workDays || 0) / total * 100)
})

const displayWorkedMonths = computed(() => {
  if (!workProgress.value?.workedMonths) return 0
  return workProgress.value.workedMonths % 12
})

const settingDialogVisible = ref(false)
const submitLoading = ref(false)
const settingFormRef = ref(null)

const settingForm = reactive({
  joinDate: '',
  retirementAge: 65,
  monthlySalary: 0,
  workDaysPerWeek: 5
})

const settingRules = {
  joinDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ],
  retirementAge: [
    { required: true, message: '请输入退休年龄', trigger: 'blur' }
  ],
  monthlySalary: [
    { required: true, message: '请输入月薪', trigger: 'blur' }
  ]
}

const formatNumber = (num) => {
  if (typeof num === 'string') {
    num = parseFloat(num)
  }
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const openSettingDialog = async () => {
  try {
    const res = await getWorkSetting()
    if (res.data) {
      settingForm.joinDate = res.data.joinDate || ''
      settingForm.retirementAge = res.data.retirementAge || 65
      settingForm.monthlySalary = res.data.monthlySalary || 0
      settingForm.workDaysPerWeek = res.data.workDaysPerWeek || 5
    }
  } catch (error) {
    console.error('获取设置失败:', error)
  }
  settingDialogVisible.value = true
}

const submitSetting = async () => {
  const valid = await settingFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      joinDate: settingForm.joinDate,
      retirementAge: settingForm.retirementAge,
      monthlySalary: settingForm.monthlySalary,
      workDaysPerWeek: settingForm.workDaysPerWeek
    }
    await saveWorkSetting(data)
    ElMessage.success('保存成功')
    settingDialogVisible.value = false
    fetchAllData()
  } catch (error) {
    console.error('保存设置失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const fetchAllData = async () => {
  try {
    const res = await getAllWorkProgress()
    setting.value = res.data.setting || {}
    hasSetting.value = !!res.data.setting?.joinDate
    workProgress.value = res.data.progress || {}
    retirementCountdown.value = res.data.retirement || {}
    yearlyStats.value = res.data.yearlyStats || {}
    earnings.value = res.data.earnings || {}
    startCountdownTimer()
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

const fetchYearlyStats = async () => {
  try {
    const res = await getYearlyStats(selectedYear.value)
    yearlyStats.value = res.data || {}
  } catch (error) {
    console.error('获取年度统计失败:', error)
  }
}

const startCountdownTimer = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  
  countdownTimer = setInterval(() => {
    if (retirementCountdown.value && !retirementCountdown.value.isRetired) {
      if (retirementCountdown.value.seconds > 0) {
        retirementCountdown.value.seconds--
      } else {
        retirementCountdown.value.seconds = 59
        if (retirementCountdown.value.minutes > 0) {
          retirementCountdown.value.minutes--
        } else {
          retirementCountdown.value.minutes = 59
          if (retirementCountdown.value.hours > 0) {
            retirementCountdown.value.hours--
          } else {
            retirementCountdown.value.hours = 23
            if (retirementCountdown.value.days > 0) {
              retirementCountdown.value.days--
            }
          }
        }
      }
    }
  }, 1000)
}

onMounted(() => {
  fetchAllData()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style lang="scss" scoped>
.work-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 20px;
      color: #333;
      margin: 0;
    }
  }

  .work-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
      font-size: 16px;
    }
  }

  .progress-content,
  .countdown-content {
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

    .progress-details,
    .countdown-info {
      flex: 1;

      .detail-row,
      .info-row {
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

    .countdown-display {
      flex: 1;
      text-align: center;
      padding: 20px 0;

      &.retired {
        .countdown-title {
          color: #67c23a;
          font-size: 24px;
        }
      }

      .countdown-title {
        font-size: 16px;
        color: #666;
        margin-bottom: 16px;
      }

      .countdown-main {
        margin-bottom: 12px;

        .years,
        .months,
        .days {
          font-size: 48px;
          font-weight: 600;
          color: #409eff;
        }

        .unit {
          font-size: 18px;
          color: #666;
          margin: 0 4px;
        }
      }

      .countdown-time {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 4px;

        .time-value {
          font-size: 24px;
          font-weight: 500;
          color: #666;
          font-family: 'Courier New', monospace;
        }

        .time-separator {
          font-size: 20px;
          color: #999;
        }
      }
    }
  }

  .second-row {
    .stats-content {
      .stats-grid {
        display: flex;
        justify-content: space-around;
        margin-bottom: 20px;

        .stat-item {
          text-align: center;

          .stat-icon {
            font-size: 32px;
            margin-bottom: 8px;
          }

          .stat-value {
            font-size: 32px;
            font-weight: 600;
            color: #333;
          }

          .stat-label {
            font-size: 14px;
            color: #666;
            margin-top: 4px;
          }

          &.work .stat-value {
            color: #67c23a;
          }

          &.fish .stat-value {
            color: #e6a23c;
          }

          &.vacation .stat-value {
            color: #409eff;
          }
        }
      }

      .stats-chart {
        .chart-labels {
          display: flex;
          justify-content: space-between;
          margin-top: 12px;
          font-size: 13px;
          color: #666;
        }
      }

      .stats-info {
        .info-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 6px 0;

          .label {
            color: #666;
            font-size: 14px;
          }

          .value {
            color: #333;
            font-size: 14px;
            font-weight: 500;
          }
        }
      }
    }

    .earnings-content {
      .earnings-main {
        .earnings-total {
          text-align: center;
          padding: 16px 0;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 12px;
          color: #fff;

          .total-label {
            font-size: 14px;
            opacity: 0.9;
            margin-bottom: 8px;
          }

          .total-value {
            font-size: 32px;
            font-weight: 600;
          }
        }
      }

      .earnings-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        margin-top: 16px;

        .earnings-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px;
          background: #f5f7fa;
          border-radius: 8px;

          .item-icon {
            font-size: 24px;
          }

          .item-content {
            .item-label {
              font-size: 12px;
              color: #666;
            }

            .item-value {
              font-size: 16px;
              font-weight: 600;
              color: #333;
              margin-top: 4px;
            }
          }
        }
      }

      .earnings-year {
        h4 {
          margin-bottom: 12px;
          color: #333;
        }

        .year-stats {
          display: flex;
          justify-content: space-between;

          .year-stat {
            .year-label {
              font-size: 12px;
              color: #666;
            }

            .year-value {
              font-size: 18px;
              font-weight: 600;
              color: #333;
              margin-top: 4px;

              &.highlight {
                color: #67c23a;
              }
            }
          }
        }
      }

      .earnings-today {
        text-align: center;
        padding: 16px 0;

        .today-label {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;
        }

        .today-value {
          font-size: 28px;
          font-weight: 600;
          color: #67c23a;
        }
      }
    }
  }

}
</style>
