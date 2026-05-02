<template>
  <div class="talent-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="talent-allocation-card">
          <template #header>
            <div class="card-header">
            <div class="header-title">
              <span class="title-icon">🌟</span>
              <span class="title-text">天赋加点</span>
            </div>
            <div class="points-info">
              <span class="points-label">已分配:</span>
              <span class="points-value used">{{ userTalent.totalPointsUsed || 0 }}</span>
              <span class="points-separator">/</span>
              <span class="points-value total">{{ talentConfig.totalPoints || 20 }}</span>
              <span class="points-label">点</span>
              <el-button 
                type="danger" 
                size="small" 
                plain
                @click="handleReset"
                :disabled="isAllocating"
              >
                重置
              </el-button>
            </div>
          </div>
          </template>

          <div class="talent-sliders">
            <div 
              v-for="talent in talentList" 
              :key="talent.key" 
              class="talent-slider-item"
              :class="{ 'active': activeTalent === talent.key }"
              @mouseenter="activeTalent = talent.key"
              @mouseleave="activeTalent = null"
            >
              <div class="talent-header">
                <div class="talent-left">
                  <span class="talent-icon">{{ talent.icon }}</span>
                  <span class="talent-name">{{ talent.name }}</span>
                </div>
                <div class="talent-right">
                  <el-button 
                    class="slider-btn" 
                    size="small" 
                    circle
                    :disabled="getTalentValue(talent.key) <= talentConfig.minPerTalent"
                    @click="adjustTalent(talent.key, -1)"
                  >
                    -
                  </el-button>
                  <span class="talent-value-display">{{ getTalentValue(talent.key) }}</span>
                  <el-button 
                    class="slider-btn" 
                    size="small" 
                    circle
                    :disabled="canAddMore(talent.key)"
                    @click="adjustTalent(talent.key, 1)"
                  >
                    +
                  </el-button>
                </div>
              </div>
              
              <el-slider
                v-model="tempTalent[talent.key]"
                :min="talentConfig.minPerTalent"
                :max="talentConfig.maxPerTalent"
                :show-tooltip="false"
                :marks="sliderMarks"
                @change="onSliderChange(talent.key)"
              />
              
              <div class="talent-description">
                {{ talent.description }}
              </div>
            </div>
          </div>

          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="large"
              :loading="isAllocating"
              @click="handleSave"
            >
              保存分配
            </el-button>
            <el-button 
              size="large"
              @click="handleCancel"
            >
              取消修改
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="talent-radar-card">
          <template #header>
            <div class="card-header">
              <span class="title-icon">📊</span>
              <span class="title-text">天赋雷达图</span>
            </div>
          </template>
          <div ref="radarChart" class="radar-chart"></div>
        </el-card>

        <el-card class="talent-summary-card">
          <template #header>
            <div class="card-header">
              <span class="title-icon">💡</span>
              <span class="title-text">天赋分析</span>
            </div>
          </template>
          <div class="talent-analysis">
            <div class="analysis-item" v-for="item in analysisList" :key="item.type">
              <div class="analysis-header">
                <span class="analysis-icon">{{ item.icon }}</span>
                <span class="analysis-title">{{ item.title }}</span>
              </div>
              <el-progress 
                :percentage="item.percentage" 
                :status="item.status"
                :stroke-width="10"
              />
              <div class="analysis-tip">{{ item.tip }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="simulation-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <span class="title-icon">🎮</span>
            <span class="title-text">人生模拟</span>
          </div>
          <el-tabs v-model="activeSimulationTab" type="card" @tab-change="onSimulationTabChange">
            <el-tab-pane label="综合评估" name="overall">
              <template #label>
                <span>🌐 综合评估</span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="事业" name="career">
              <template #label>
                <span>💼 事业</span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="感情" name="relationship">
              <template #label>
                <span>💕 感情</span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="健康" name="health">
              <template #label>
                <span>💪 健康</span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="财富" name="wealth">
              <template #label>
                <span>💰 财富</span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
      </template>

      <div class="simulation-content">
        <div v-if="!simulationResult" class="simulation-empty">
          <el-empty description="点击下方按钮开始模拟你的人生模拟">
            <el-button type="primary" @click="runSimulation">
              <span class="simulate-btn-text">开始模拟</span>
            </el-button>
          </el-empty>
        </div>

        <div v-else class="simulation-result">
          <div class="result-header">
            <div class="result-score">
              <span class="score-label">模拟得分</span>
              <span class="score-value">{{ simulationResult.score || simulationResult.totalScore }}</span>
              <span class="score-max">/{{ simulationResult.maxScore || simulationResult.maxPossible }}</span>
            </div>
            <el-tag :type="getLevelTagType(simulationResult.level)" size="large">
              {{ simulationResult.level }}
            </el-tag>
            <el-button type="primary" @click="runSimulation">
              重新模拟
            </el-button>
          </div>

          <div v-if="simulationResult.breakdown" class="result-breakdown">
            <h4>各维度得分</h4>
            <div class="breakdown-list">
              <div v-for="item in simulationResult.breakdown" :key="item.name" class="breakdown-item">
                <span class="breakdown-name">{{ item.name }}</span>
                <div class="breakdown-progress">
                  <el-progress 
                    :percentage="calculatePercentage(item.score, getMaxForType(item.name))"
                    :stroke-width="12"
                    :show-text="false"
                  />
                </div>
                <span class="breakdown-score">{{ item.score }}</span>
                <el-tag :type="getLevelTagType(item.level)" size="small">{{ item.level }}</el-tag>
              </div>
            </div>
          </div>

          <div v-if="simulationResult.talentBreakdown" class="talent-impact">
            <h4>天赋影响分析</h4>
            <div class="impact-list">
              <div v-for="item in simulationResult.talentBreakdown" :key="item.name" class="impact-item">
                <span class="impact-name">{{ item.name }}</span>
                <span class="impact-value">{{ item.value }}</span>
                <el-tag v-if="item.impact === '高'" type="danger" size="small">高影响</el-tag>
                <el-tag v-else-if="item.impact === '中'" type="warning" size="small">中影响</el-tag>
                <el-tag v-else type="info" size="small">低影响</el-tag>
              </div>
            </div>
          </div>

          <div class="result-events">
            <h4>模拟事件</h4>
            <div class="events-list">
              <div v-for="(event, index) in simulationResult.events" :key="index" class="event-item">
                <span class="event-text">{{ event }}</span>
              </div>
            </div>
          </div>

          <div v-if="simulationResult.summary" class="result-summary">
            <h4>综合评价</h4>
            <div class="summary-text">{{ simulationResult.summary }}</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  getMyTalent,
  getTalentConfig,
  allocateTalentPoints,
  resetTalentPoints,
  simulateCareer,
  simulateRelationship,
  simulateHealth,
  simulateWealth,
  simulateOverall
} from '@/api/talent'

const radarChart = ref(null)
let chartInstance = null

const userTalent = ref({
  appearance: 0,
  intelligence: 0,
  wealth: 0,
  health: 0,
  emotional: 0,
  luck: 0,
  totalPointsUsed: 0
})

const tempTalent = ref({
  appearance: 0,
  intelligence: 0,
  wealth: 0,
  health: 0,
  emotional: 0,
  luck: 0
})

const talentConfig = ref({
  totalPoints: 20,
  maxPerTalent: 10,
  minPerTalent: 0
})

const activeTalent = ref(null)
const isAllocating = ref(false)
const activeSimulationTab = ref('overall')
const simulationResult = ref(null)
const isSimulating = ref(false)

const talentList = [
  { key: 'appearance', name: '颜值', icon: '👤', description: '影响社交吸引力和第一印象' },
  { key: 'intelligence', name: '智商', icon: '🧠', description: '影响学习能力和问题解决能力' },
  { key: 'wealth', name: '财运', icon: '💎', description: '影响收入和投资回报率' },
  { key: 'health', name: '健康', icon: '❤️', description: '影响寿命和生活质量' },
  { key: 'emotional', name: '情商', icon: '🎭', description: '影响人际关系和情绪管理' },
  { key: 'luck', name: '运气', icon: '🍀', description: '影响遇到意外好事的概率' }
]

const sliderMarks = computed(() => {
  const marks = {}
  for (let i = 0; i <= talentConfig.value.maxPerTalent; i += 2) {
    marks[i] = i.toString()
  }
  return marks
})

const totalUsed = computed(() => {
  return Object.values(tempTalent.value).reduce((sum, val) => sum + val, 0)
})

const analysisList = computed(() => {
  const talent = tempTalent.value
  const max = talentConfig.value.maxPerTalent
  
  const careerScore = (talent.intelligence + talent.emotional + talent.wealth + talent.luck) / (max * 4) * 100
  const relationshipScore = (talent.appearance + talent.emotional + talent.luck) / (max * 3) * 100
  const healthScore = (talent.health + talent.luck) / (max * 2) * 100
  const wealthScore = (talent.wealth + talent.intelligence + talent.luck) / (max * 3) * 100
  
  return [
    {
      type: 'career',
      icon: '💼',
      title: '事业潜力',
      percentage: Math.round(careerScore),
      status: careerScore >= 60 ? 'success' : careerScore >= 40 ? '' : 'exception',
      tip: careerScore >= 60 ? '事业发展前景不错' : careerScore >= 40 ? '事业发展一般' : '需要提升相关天赋'
    },
    {
      type: 'relationship',
      icon: '💕',
      title: '感情运势',
      percentage: Math.round(relationshipScore),
      status: relationshipScore >= 60 ? 'success' : relationshipScore >= 40 ? '' : 'exception',
      tip: relationshipScore >= 60 ? '感情生活可期' : relationshipScore >= 40 ? '感情运势平稳' : '需要提升社交能力'
    },
    {
      type: 'health',
      icon: '💪',
      title: '健康状况',
      percentage: Math.round(healthScore),
      status: healthScore >= 60 ? 'success' : healthScore >= 40 ? '' : 'exception',
      tip: healthScore >= 60 ? '身体状况良好' : healthScore >= 40 ? '健康一般' : '需要关注健康'
    },
    {
      type: 'wealth',
      icon: '💰',
      title: '财富潜力',
      percentage: Math.round(wealthScore),
      status: wealthScore >= 60 ? 'success' : wealthScore >= 40 ? '' : 'exception',
      tip: wealthScore >= 60 ? '财运亨通' : wealthScore >= 40 ? '财运一般' : '需要提升财运'
    }
  ]
})

const getTalentValue = (key) => {
  return tempTalent.value[key] || 0
}

const canAddMore = (key) => {
  const currentValue = tempTalent.value[key]
  const maxPerTalent = talentConfig.value.maxPerTalent
  const totalPoints = talentConfig.value.totalPoints
  
  if (currentValue >= maxPerTalent) return true
  if (totalUsed.value >= totalPoints) return true
  return false
}

const adjustTalent = (key, delta) => {
  const newValue = tempTalent.value[key] + delta
  const maxPerTalent = talentConfig.value.maxPerTalent
  const minPerTalent = talentConfig.value.minPerTalent
  const totalPoints = talentConfig.value.totalPoints
  
  if (delta > 0) {
    if (newValue > maxPerTalent) return
    if (totalUsed.value >= totalPoints) return
  } else {
    if (newValue < minPerTalent) return
  }
  
  tempTalent.value[key] = newValue
  updateRadarChart()
}

const onSliderChange = (key) => {
  updateRadarChart()
}

const fetchUserTalent = async () => {
  try {
    const res = await getMyTalent()
    userTalent.value = res.data
    tempTalent.value = {
      appearance: res.data.appearance || 0,
      intelligence: res.data.intelligence || 0,
      wealth: res.data.wealth || 0,
      health: res.data.health || 0,
      emotional: res.data.emotional || 0,
      luck: res.data.luck || 0
    }
    await nextTick()
    initRadarChart()
  } catch (error) {
    console.error('获取天赋数据失败:', error)
  }
}

const fetchTalentConfig = async () => {
  try {
    const res = await getTalentConfig()
    talentConfig.value = {
      totalPoints: res.data.totalPoints || 20,
      maxPerTalent: res.data.maxPerTalent || 10,
      minPerTalent: res.data.minPerTalent || 0
    }
  } catch (error) {
    console.error('获取天赋配置失败:', error)
  }
}

const handleSave = async () => {
  try {
    isAllocating.value = true
    const res = await allocateTalentPoints(tempTalent.value)
    userTalent.value = res.data
    ElMessage.success('天赋点保存成功！')
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  } finally {
    isAllocating.value = false
  }
}

const handleReset = async () => {
  try {
    await ElMessageBox.confirm('确定要重置所有天赋点吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    isAllocating.value = true
    const res = await resetTalentPoints()
    userTalent.value = res.data
    tempTalent.value = {
      appearance: 0,
      intelligence: 0,
      wealth: 0,
      health: 0,
      emotional: 0,
      luck: 0
    }
    updateRadarChart()
    ElMessage.success('天赋点已重置！')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置失败，请重试')
    }
  } finally {
    isAllocating.value = false
  }
}

const handleCancel = () => {
  tempTalent.value = {
    appearance: userTalent.value.appearance || 0,
    intelligence: userTalent.value.intelligence || 0,
    wealth: userTalent.value.wealth || 0,
    health: userTalent.value.health || 0,
    emotional: userTalent.value.emotional || 0,
    luck: userTalent.value.luck || 0
  }
  updateRadarChart()
  ElMessage.info('已取消修改')
}

const initRadarChart = () => {
  if (!radarChart.value) return
  
  chartInstance = echarts.init(radarChart.value)
  
  const option = {
    radar: {
      indicator: [
        { name: '颜值', max: talentConfig.value.maxPerTalent },
        { name: '智商', max: talentConfig.value.maxPerTalent },
        { name: '财运', max: talentConfig.value.maxPerTalent },
        { name: '健康', max: talentConfig.value.maxPerTalent },
        { name: '情商', max: talentConfig.value.maxPerTalent },
        { name: '运气', max: talentConfig.value.maxPerTalent }
      ],
      shape: 'polygon',
      splitNumber: 5,
      axisName: {
        color: '#666',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      },
      splitArea: {
        show: false
      },
      axisLine: {
        lineStyle: {
          color: '#e0e0e0'
        }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: [
          tempTalent.value.appearance,
          tempTalent.value.intelligence,
          tempTalent.value.wealth,
          tempTalent.value.health,
          tempTalent.value.emotional,
          tempTalent.value.luck
        ],
        name: '天赋分布',
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.3)'
        },
        lineStyle: {
          color: '#409EFF',
          width: 2
        },
        itemStyle: {
          color: '#409EFF'
        }
      }]
    }]
  }
  
  chartInstance.setOption(option)
  
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

const updateRadarChart = () => {
  if (!chartInstance) return
  
  chartInstance.setOption({
    series: [{
      data: [{
        value: [
          tempTalent.value.appearance,
          tempTalent.value.intelligence,
          tempTalent.value.wealth,
          tempTalent.value.health,
          tempTalent.value.emotional,
          tempTalent.value.luck
        ]
      }]
    }]
  })
}

const runSimulation = async () => {
  try {
    isSimulating.value = true
    simulationResult.value = null
    
    let res
    switch (activeSimulationTab.value) {
      case 'career':
        res = await simulateCareer()
        break
      case 'relationship':
        res = await simulateRelationship()
        break
      case 'health':
        res = await simulateHealth()
        break
      case 'wealth':
        res = await simulateWealth()
        break
      case 'overall':
      default:
        res = await simulateOverall()
        break
    }
    
    simulationResult.value = res.data
  } catch (error) {
    ElMessage.error('模拟失败，请重试')
  } finally {
    isSimulating.value = false
  }
}

const onSimulationTabChange = () => {
  simulationResult.value = null
}

const getLevelTagType = (level) => {
  const typeMap = {
    '卓越': 'success',
    '优秀': 'primary',
    '良好': 'warning',
    '一般': 'info',
    '有待提升': 'danger'
  }
  return typeMap[level] || 'info'
}

const getMaxForType = (name) => {
  const maxMap = {
    '事业': 40,
    '感情': 30,
    '健康': 20,
    '财富': 30
  }
  return maxMap[name] || 30
}

const calculatePercentage = (score, max) => {
  return Math.round((score / max) * 100)
}

watch(tempTalent, () => {
  updateRadarChart()
}, { deep: true })

onMounted(() => {
  fetchTalentConfig()
  fetchUserTalent()
})
</script>

<style lang="scss" scoped>
.talent-container {
  .talent-allocation-card,
  .talent-radar-card,
  .talent-summary-card,
  .simulation-card {
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

  .points-info {
    display: flex;
    align-items: center;
    gap: 8px;

    .points-label {
      font-size: 14px;
      color: #666;
    }

    .points-value {
      font-size: 20px;
      font-weight: 600;

      &.used {
        color: #409EFF;
      }

      &.total {
        color: #909399;
      }
    }

    .points-separator {
      color: #909399;
      font-size: 16px;
    }
  }

  .talent-sliders {
    .talent-slider-item {
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 8px;
      transition: all 0.3s ease;
      border: 1px solid #f0f0f0;

      &.active {
        background: #f5f7fa;
        border-color: #409EFF;
      }

      &:hover {
        background: #fafafa;
      }

      .talent-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .talent-left {
          display: flex;
          align-items: center;
          gap: 8px;

          .talent-icon {
            font-size: 20px;
          }

          .talent-name {
            font-size: 15px;
            font-weight: 500;
            color: #333;
          }
        }

        .talent-right {
          display: flex;
          align-items: center;
          gap: 8px;

          .slider-btn {
            width: 28px;
            height: 28px;
            padding: 0;
          }

          .talent-value-display {
            font-size: 18px;
            font-weight: 600;
            color: #409EFF;
            min-width: 30px;
            text-align: center;
          }
        }
      }

      .talent-description {
        font-size: 12px;
        color: #909399;
        margin-top: 8px;
        padding-left: 28px;
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
  }

  .radar-chart {
    width: 100%;
    height: 300px;
  }

  .talent-analysis {
    .analysis-item {
      margin-bottom: 16px;

      &:last-child {
        margin-bottom: 0;
      }

      .analysis-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .analysis-icon {
          font-size: 16px;
        }

        .analysis-title {
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }
      }

      .analysis-tip {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .simulation-content {
    .simulation-empty {
      padding: 40px 0;
      text-align: center;

      .simulate-btn-text {
        font-size: 16px;
      }
    }

    .simulation-result {
      .result-header {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 24px;
        padding-bottom: 16px;
        border-bottom: 1px solid #f0f0f0;

        .result-score {
          display: flex;
          align-items: baseline;
          gap: 4px;

          .score-label {
            font-size: 14px;
            color: #666;
          }

          .score-value {
            font-size: 32px;
            font-weight: 700;
            color: #409EFF;
          }

          .score-max {
            font-size: 16px;
            color: #909399;
          }
        }
      }

      .result-breakdown,
      .talent-impact,
      .result-events,
      .result-summary {
        margin-bottom: 24px;

        h4 {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin-bottom: 12px;
        }
      }

      .breakdown-list {
        .breakdown-item {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;

          .breakdown-name {
            width: 60px;
            font-size: 14px;
            color: #333;
          }

          .breakdown-progress {
            flex: 1;
          }

          .breakdown-score {
            width: 40px;
            text-align: right;
            font-size: 14px;
            font-weight: 500;
            color: #409EFF;
          }
        }
      }

      .impact-list {
        .impact-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .impact-name {
            width: 60px;
            font-size: 14px;
            color: #333;
          }

          .impact-value {
            font-size: 16px;
            font-weight: 600;
            color: #409EFF;
            width: 30px;
          }
        }
      }

      .events-list {
        .event-item {
          padding: 12px;
          background: #f5f7fa;
          border-radius: 8px;
          margin-bottom: 8px;

          .event-text {
            font-size: 14px;
            color: #333;
            line-height: 1.6;
          }
        }
      }

      .summary-text {
        padding: 16px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 8px;
        color: #fff;
        font-size: 14px;
        line-height: 1.8;
      }
    }
  }
}
</style>
