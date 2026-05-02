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
              <div class="header-actions">
                <el-button type="primary" size="small" plain @click="handleQuickAllocate">
                  <el-icon><MagicStick /></el-icon>
                  一键分配
                </el-button>
                <el-button type="warning" size="small" plain @click="handleRandomAllocate">
                  <el-icon><Dice /></el-icon>
                  随机分配
                </el-button>
                <el-button type="danger" size="small" plain @click="handleReset" :disabled="isAllocating">
                  重置
                </el-button>
              </div>
            </div>
          </template>

          <div class="points-overview">
            <div class="points-circle">
              <el-progress
                type="dashboard"
                :percentage="pointsPercentage"
                :width="120"
                :stroke-width="12"
                :color="pointsColor"
              >
                <div class="circle-content">
                  <span class="circle-used">{{ totalUsed }}</span>
                  <span class="circle-separator">/</span>
                  <span class="circle-total">{{ talentConfig.totalPoints }}</span>
                </div>
              </el-progress>
            </div>
            
            <div class="points-details">
              <div class="detail-item used">
                <div class="detail-label">已分配点数</div>
                <div class="detail-value">{{ totalUsed }}</div>
                <div class="detail-bar">
                  <div class="bar-inner used" :style="{ width: pointsPercentage + '%' }"></div>
                </div>
              </div>
              
              <div class="detail-item remaining">
                <div class="detail-label">剩余点数</div>
                <div class="detail-value highlight">{{ remainingPoints }}</div>
                <div class="detail-bar">
                  <div class="bar-inner remaining" :style="{ width: remainingPercentage + '%' }"></div>
                </div>
              </div>
              
              <div class="detail-tip" v-if="remainingPoints > 0">
                <el-icon><InfoFilled /></el-icon>
                <span>还有 <strong>{{ remainingPoints }}</strong> 点天赋可以分配！</span>
              </div>
              <div class="detail-tip success" v-else-if="remainingPoints === 0">
                <el-icon><CircleCheckFilled /></el-icon>
                <span>天赋已全部分配完成！</span>
              </div>
            </div>
          </div>

          <div class="talent-sliders">
            <div 
              v-for="talent in talentList" 
              :key="talent.key" 
              class="talent-slider-item"
              :class="{ 
                'active': activeTalent === talent.key,
                'has-points': tempTalent[talent.key] > 0,
                'can-add': canAddMore(talent.key) === false && remainingPoints > 0
              }"
              @mouseenter="activeTalent = talent.key"
              @mouseleave="activeTalent = null"
            >
              <div class="talent-header">
                <div class="talent-left">
                  <div class="talent-icon-wrapper">
                    <span class="talent-icon">{{ talent.icon }}</span>
                  </div>
                  <div class="talent-info">
                    <span class="talent-name">{{ talent.name }}</span>
                    <span class="talent-description">{{ talent.description }}</span>
                  </div>
                </div>
                
                <div class="talent-right">
                  <div class="talent-value-control">
                    <el-button 
                      class="control-btn minus" 
                      size="small" 
                      circle
                      :disabled="tempTalent[talent.key] <= talentConfig.minPerTalent"
                      @click="adjustTalent(talent.key, -1)"
                    >
                      <span class="btn-text">-</span>
                    </el-button>
                    
                    <div class="value-display">
                      <span class="current-value">{{ tempTalent[talent.key] }}</span>
                      <span class="max-indicator">/{{ talentConfig.maxPerTalent }}</span>
                    </div>
                    
                    <el-button 
                      class="control-btn plus" 
                      size="small" 
                      circle
                      :disabled="canAddMore(talent.key)"
                      @click="adjustTalent(talent.key, 1)"
                    >
                      <span class="btn-text">+</span>
                    </el-button>
                  </div>
                </div>
              </div>
              
              <div class="slider-wrapper">
                <el-slider
                  v-model="tempTalent[talent.key]"
                  :min="talentConfig.minPerTalent"
                  :max="calculateMaxForSlider(talent.key)"
                  :show-tooltip="false"
                  :marks="sliderMarks"
                  :disabled="false"
                  @input="onSliderInput(talent.key)"
                  @change="onSliderChange(talent.key)"
                />
                
                <div class="slider-mini-bars" v-if="tempTalent[talent.key] > 0">
                  <div 
                    v-for="i in talentConfig.maxPerTalent" 
                    :key="i"
                    class="mini-bar"
                    :class="{ 'filled': i <= tempTalent[talent.key] }"
                  ></div>
                </div>
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
              <el-icon><Check /></el-icon>
              保存分配
            </el-button>
            <el-button 
              size="large"
              @click="handleCancel"
            >
              <el-icon><Refresh /></el-icon>
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
          <el-empty description="点击下方按钮开始模拟你的人生">
            <el-button type="primary" size="large" @click="runSimulation">
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

const remainingPoints = computed(() => {
  return talentConfig.value.totalPoints - totalUsed.value
})

const pointsPercentage = computed(() => {
  return Math.round((totalUsed.value / talentConfig.value.totalPoints) * 100)
})

const remainingPercentage = computed(() => {
  return Math.round((remainingPoints.value / talentConfig.value.totalPoints) * 100)
})

const pointsColor = computed(() => {
  if (pointsPercentage.value >= 80) return '#67c23a'
  if (pointsPercentage.value >= 60) return '#409EFF'
  if (pointsPercentage.value >= 40) return '#e6a23c'
  return '#909399'
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
  
  if (currentValue >= maxPerTalent) return true
  if (remainingPoints.value <= 0) return true
  return false
}

const calculateMaxForSlider = (key) => {
  const currentValue = tempTalent.value[key]
  const maxPerTalent = talentConfig.value.maxPerTalent
  const totalRemaining = remainingPoints.value + currentValue
  
  return Math.min(maxPerTalent, totalRemaining)
}

const adjustTalent = (key, delta) => {
  const newValue = tempTalent.value[key] + delta
  const maxPerTalent = talentConfig.value.maxPerTalent
  const minPerTalent = talentConfig.value.minPerTalent
  
  if (delta > 0) {
    if (newValue > maxPerTalent) return
    if (remainingPoints.value <= 0) return
  } else {
    if (newValue < minPerTalent) return
  }
  
  tempTalent.value[key] = newValue
  updateRadarChart()
}

const onSliderInput = (key) => {
  updateRadarChart()
}

const onSliderChange = (key) => {
  updateRadarChart()
}

const handleQuickAllocate = () => {
  const maxPer = talentConfig.value.maxPerTalent
  const total = talentConfig.value.totalPoints
  
  talentList.forEach(talent => {
    tempTalent.value[talent.key] = 0
  })
  
  let remaining = total
  for (let i = 0; i < maxPer && remaining > 0; i++) {
    for (const talent of talentList) {
      if (remaining > 0 && tempTalent.value[talent.key] < maxPer) {
        tempTalent.value[talent.key]++
        remaining--
      }
    }
  }
  
  updateRadarChart()
  ElMessage.success('已一键均衡分配天赋点！')
}

const handleRandomAllocate = () => {
  const maxPer = talentConfig.value.maxPerTalent
  const total = talentConfig.value.totalPoints
  
  talentList.forEach(talent => {
    tempTalent.value[talent.key] = 0
  })
  
  let remaining = total
  const keys = talentList.map(t => t.key)
  
  while (remaining > 0) {
    const randomKey = keys[Math.floor(Math.random() * keys.length)]
    if (tempTalent.value[randomKey] < maxPer) {
      tempTalent.value[randomKey]++
      remaining--
    }
  }
  
  updateRadarChart()
  ElMessage.success('已随机分配天赋点！')
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

    .header-actions {
      display: flex;
      gap: 8px;
    }

    .title-icon {
      margin-right: 8px;
      font-size: 18px;
    }

    .title-text {
      font-size: 16px;
    }
  }

  .points-overview {
    display: flex;
    gap: 24px;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    margin-bottom: 24px;

    .points-circle {
      flex-shrink: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      
      :deep(.el-progress-circle) {
        svg {
          width: 120px !important;
          height: 120px !important;
        }
      }

      .circle-content {
        position: absolute;
        display: flex;
        align-items: baseline;
        justify-content: center;
        gap: 2px;

        .circle-used {
          font-size: 28px;
          font-weight: 700;
          color: #fff;
        }

        .circle-separator {
          font-size: 16px;
          color: rgba(255, 255, 255, 0.7);
        }

        .circle-total {
          font-size: 16px;
          color: rgba(255, 255, 255, 0.7);
        }
      }
    }

    .points-details {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .detail-item {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;

        &.used {
          .detail-value {
            color: #fff;
          }
        }

        &.remaining {
          .detail-value {
            color: #ffd700;
          }
        }

        .detail-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
          width: 80px;
        }

        .detail-value {
          font-size: 24px;
          font-weight: 700;
          width: 50px;

          &.highlight {
            animation: pulse 2s infinite;
          }
        }

        .detail-bar {
          flex: 1;
          height: 8px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 4px;
          overflow: hidden;

          .bar-inner {
            height: 100%;
            border-radius: 4px;
            transition: width 0.3s ease;

            &.used {
              background: linear-gradient(90deg, #409EFF, #67c23a);
            }

            &.remaining {
              background: linear-gradient(90deg, #ffd700, #ffed4e);
            }
          }
        }
      }

      .detail-tip {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 12px;
        background: rgba(255, 255, 255, 0.15);
        border-radius: 6px;
        margin-top: 8px;

        .el-icon {
          font-size: 16px;
          color: #ffd700;
        }

        span {
          font-size: 14px;
          color: #fff;

          strong {
            font-size: 16px;
            color: #ffd700;
          }
        }

        &.success {
          .el-icon {
            color: #67c23a;
          }

          strong {
            color: #67c23a;
          }
        }
      }
    }
  }

  .talent-sliders {
    .talent-slider-item {
      padding: 16px 20px;
      border-radius: 12px;
      margin-bottom: 12px;
      transition: all 0.3s ease;
      border: 2px solid #f0f0f0;
      background: #fff;

      &.active {
        background: #f5f7fa;
        border-color: #409EFF;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
      }

      &.has-points {
        border-left: 4px solid #409EFF;
      }

      &.can-add {
        border-color: #67c23a;
      }

      .talent-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .talent-left {
          display: flex;
          align-items: center;
          gap: 12px;

          .talent-icon-wrapper {
            width: 48px;
            height: 48px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
          }

          .talent-icon {
            font-size: 24px;
          }

          .talent-info {
            display: flex;
            flex-direction: column;
            gap: 4px;

            .talent-name {
              font-size: 16px;
              font-weight: 600;
              color: #333;
            }

            .talent-description {
              font-size: 12px;
              color: #909399;
            }
          }
        }

        .talent-right {
          .talent-value-control {
            display: flex;
            align-items: center;
            gap: 12px;

            .control-btn {
              width: 32px;
              height: 32px;
              padding: 0;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 8px;
              transition: all 0.2s ease;

              &.minus {
                background: #fef0f0;
                border-color: #fbc4c4;

                &:hover:not(:disabled) {
                  background: #fde2e2;
                }

                .btn-text {
                  color: #f56c6c;
                  font-size: 20px;
                  font-weight: 600;
                }
              }

              &.plus {
                background: #f0f9eb;
                border-color: #c2e7b0;

                &:hover:not(:disabled) {
                  background: #e1f3d8;
                }

                .btn-text {
                  color: #67c23a;
                  font-size: 20px;
                  font-weight: 600;
                }
              }

              &:disabled {
                opacity: 0.5;
                cursor: not-allowed;
              }
            }

            .value-display {
              display: flex;
              align-items: baseline;
              gap: 2px;
              padding: 8px 16px;
              background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
              border-radius: 8px;
              min-width: 60px;
              text-align: center;

              .current-value {
                font-size: 24px;
                font-weight: 700;
                color: #409EFF;
              }

              .max-indicator {
                font-size: 12px;
                color: #909399;
              }
            }
          }
        }
      }

      .slider-wrapper {
        padding: 0 8px;

        .slider-mini-bars {
          display: flex;
          gap: 4px;
          margin-top: 8px;
          height: 6px;

          .mini-bar {
            flex: 1;
            background: #f0f0f0;
            border-radius: 3px;
            transition: all 0.3s ease;

            &.filled {
              background: linear-gradient(90deg, #409EFF, #67c23a);
              box-shadow: 0 0 4px rgba(64, 158, 255, 0.3);
            }
          }
        }
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

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}
</style>
