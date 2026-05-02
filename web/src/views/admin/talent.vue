<template>
  <div class="admin-talent-container">
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>⚙️ 天赋系统配置</span>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form :model="configForm" label-width="120px">
            <el-form-item label="初始总天赋点">
              <el-input-number 
                v-model="configForm.totalPoints" 
                :min="0" 
                :max="100"
                @change="handleConfigChange('total_points', configForm.totalPoints)"
              />
            </el-form-item>
            <el-form-item label="单天赋最大值">
              <el-input-number 
                v-model="configForm.maxPerTalent" 
                :min="0" 
                :max="50"
                @change="handleConfigChange('max_per_talent', configForm.maxPerTalent)"
              />
            </el-form-item>
            <el-form-item label="单天赋最小值">
              <el-input-number 
                v-model="configForm.minPerTalent" 
                :min="0" 
                :max="10"
                @change="handleConfigChange('min_per_talent', configForm.minPerTalent)"
              />
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="16">
          <el-table :data="configList" style="width: 100%">
            <el-table-column prop="configKey" label="配置键" width="200">
              <template #default="scope">
                <span class="config-key">{{ scope.row.configKey }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="configValue" label="配置值">
              <template #default="scope">
                <el-input 
                  v-model="scope.row.configValue" 
                  size="small"
                  @change="handleConfigUpdate(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="stats-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>📊 天赋统计数据</span>
          <el-button type="primary" size="small" @click="fetchStatistics">
            刷新数据
          </el-button>
        </div>
      </template>

      <el-row :gutter="20" v-if="statistics">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
              👥
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ statistics.totalUsers }}</span>
              <span class="stat-label">已初始化用户</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
              📊
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ statistics.totalPointsUsed }}</span>
              <span class="stat-label">总分配点数</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-icon" style="background: #f0f9eb; color: #67c23a">
              📈
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ statistics.averagePointsUsed }}</span>
              <span class="stat-label">人均分配点数</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
              🎯
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ configForm.totalPoints }}</span>
              <span class="stat-label">当前配置总点数</span>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="averages-section" v-if="statistics?.averages">
        <h4>各天赋平均值分布</h4>
        <div class="averages-grid">
          <div v-for="item in statistics.averages" :key="item.name" class="average-item">
            <div class="average-header">
              <span class="average-name">{{ item.name }}</span>
              <span class="average-value">{{ item.average }}</span>
            </div>
            <el-progress 
              :percentage="calculatePercentage(item.average, configForm.maxPerTalent)"
              :stroke-width="10"
              :show-text="false"
            />
            <div class="average-footer">
              <span class="average-max">最大值: {{ item.max }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="users-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>👥 用户天赋管理</span>
          <div class="header-actions">
            <el-input 
              v-model="searchKeyword" 
              placeholder="搜索用户ID" 
              style="width: 200px; margin-right: 10px"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" size="small" @click="fetchUserTalents">
              刷新列表
            </el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="filteredUserTalents" 
        style="width: 100%" 
        v-loading="loading"
        max-height="500"
      >
        <el-table-column prop="id" label="天赋ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" sortable />
        <el-table-column label="颜值" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.appearance)" size="small">
              {{ scope.row.appearance }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="智商" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.intelligence)" size="small">
              {{ scope.row.intelligence }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="财运" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.wealth)" size="small">
              {{ scope.row.wealth }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="健康" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.health)" size="small">
              {{ scope.row.health }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="情商" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.emotional)" size="small">
              {{ scope.row.emotional }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="运气" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.luck)" size="small">
              {{ scope.row.luck }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalPointsUsed" label="已分配" width="100" sortable>
          <template #default="scope">
            <span class="points-used">{{ scope.row.totalPointsUsed }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="scope">
            {{ scope.row.updateTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              type="text" 
              size="small" 
              @click="openEditDialog(scope.row)"
            >
              编辑天赋
            </el-button>
            <el-button 
              type="text" 
              size="small" 
              @click="viewUserLogs(scope.row)"
            >
              查看日志
            </el-button>
            <el-button 
              type="text" 
              size="small" 
              style="color: #f56c6c"
              @click="resetUserTalent(scope.row)"
            >
              重置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户天赋"
      width="600px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        label-width="100px"
      >
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userId" disabled />
        </el-form-item>
        <el-form-item label="颜值">
          <el-slider 
            v-model="editForm.appearance" 
            :min="configForm.minPerTalent" 
            :max="configForm.maxPerTalent"
            show-input
          />
        </el-form-item>
        <el-form-item label="智商">
          <el-slider 
            v-model="editForm.intelligence" 
            :min="configForm.minPerTalent" 
            :max="configForm.maxPerTalent"
            show-input
          />
        </el-form-item>
        <el-form-item label="财运">
          <el-slider 
            v-model="editForm.wealth" 
            :min="configForm.minPerTalent" 
            :max="configForm.maxPerTalent"
            show-input
          />
        </el-form-item>
        <el-form-item label="健康">
          <el-slider 
            v-model="editForm.health" 
            :min="configForm.minPerTalent" 
            :max="configForm.maxPerTalent"
            show-input
          />
        </el-form-item>
        <el-form-item label="情商">
          <el-slider 
            v-model="editForm.emotional" 
            :min="configForm.minPerTalent" 
            :max="configForm.maxPerTalent"
            show-input
          />
        </el-form-item>
        <el-form-item label="运气">
          <el-slider 
            v-model="editForm.luck" 
            :min="configForm.minPerTalent" 
            :max="configForm.maxPerTalent"
            show-input
          />
        </el-form-item>
        <el-form-item label="已分配点数">
          <span class="total-points-display">{{ calculateEditTotal() }} / {{ configForm.totalPoints }}</span>
          <el-tag v-if="calculateEditTotal() > configForm.totalPoints" type="danger" size="small" style="margin-left: 10px">
            超出限制
          </el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :loading="submitLoading" 
          @click="handleEditSubmit"
          :disabled="calculateEditTotal() > configForm.totalPoints"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="logsDialogVisible"
      title="用户模拟日志"
      width="800px"
    >
      <div class="logs-filter">
        <el-select 
          v-model="logFilterType" 
          placeholder="选择模拟类型" 
          clearable
          style="width: 200px"
          @change="fetchUserLogs"
        >
          <el-option label="全部" value="" />
          <el-option label="事业" value="career" />
          <el-option label="感情" value="relationship" />
          <el-option label="健康" value="health" />
          <el-option label="财富" value="wealth" />
          <el-option label="综合评估" value="overall" />
        </el-select>
      </div>

      <el-table :data="userLogs" style="width: 100%" v-loading="logsLoading" max-height="400">
        <el-table-column prop="id" label="日志ID" width="80" />
        <el-table-column prop="simulationType" label="模拟类型" width="120">
          <template #default="scope">
            <el-tag :type="getSimulationTagType(scope.row.simulationType)" size="small">
              {{ getSimulationTypeName(scope.row.simulationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="simulationResult" label="模拟结果" min-width="200">
          <template #default="scope">
            <el-button 
              type="text" 
              size="small"
              @click="showLogDetail(scope.row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <el-empty v-if="userLogs.length === 0 && !logsLoading" description="暂无模拟日志" />
    </el-dialog>

    <el-dialog
      v-model="logDetailVisible"
      title="模拟日志详情"
      width="600px"
    >
      <div class="log-detail-content" v-if="currentLogDetail">
        <h4>基本信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ currentLogDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="模拟类型">
            <el-tag :type="getSimulationTagType(currentLogDetail.simulationType)" size="small">
              {{ getSimulationTypeName(currentLogDetail.simulationType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ currentLogDetail.createTime }}
          </el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top: 20px">天赋快照</h4>
        <div class="talent-snapshot" v-if="parsedTalentSnapshot">
          <el-row :gutter="20">
            <el-col :span="8" v-for="(value, key) in parsedTalentSnapshot" :key="key">
              <div class="snapshot-item">
                <span class="snapshot-label">{{ getTalentName(key) }}</span>
                <span class="snapshot-value">{{ value }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <h4 style="margin-top: 20px">模拟结果</h4>
        <div class="simulation-result-detail">
          <pre>{{ JSON.stringify(parsedSimulationResult, null, 2) }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAllConfigs,
  updateConfig,
  getAllUserTalents,
  allocateTalentForUser,
  getTalentStatistics,
  getUserSimulationLogs,
  initUserTalent
} from '@/api/talent'

const loading = ref(false)
const submitLoading = ref(false)
const logsLoading = ref(false)

const configForm = reactive({
  totalPoints: 20,
  maxPerTalent: 10,
  minPerTalent: 0
})

const configList = ref([])
const userTalents = ref([])
const searchKeyword = ref('')
const statistics = ref(null)

const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  userId: null,
  appearance: 0,
  intelligence: 0,
  wealth: 0,
  health: 0,
  emotional: 0,
  luck: 0
})

const logsDialogVisible = ref(false)
const logFilterType = ref('')
const currentUserId = ref(null)
const userLogs = ref([])

const logDetailVisible = ref(false)
const currentLogDetail = ref(null)
const parsedTalentSnapshot = ref(null)
const parsedSimulationResult = ref(null)

const filteredUserTalents = computed(() => {
  if (!searchKeyword.value) {
    return userTalents.value
  }
  return userTalents.value.filter(item => 
    item.userId.toString().includes(searchKeyword.value)
  )
})

const fetchConfigs = async () => {
  try {
    const res = await getAllConfigs()
    configList.value = res.data
    
    const totalPointsConfig = res.data.find(c => c.configKey === 'total_points')
    const maxPerTalentConfig = res.data.find(c => c.configKey === 'max_per_talent')
    const minPerTalentConfig = res.data.find(c => c.configKey === 'min_per_talent')
    
    if (totalPointsConfig) configForm.totalPoints = parseInt(totalPointsConfig.configValue)
    if (maxPerTalentConfig) configForm.maxPerTalent = parseInt(maxPerTalentConfig.configValue)
    if (minPerTalentConfig) configForm.minPerTalent = parseInt(minPerTalentConfig.configValue)
  } catch (error) {
    console.error('获取配置失败:', error)
  }
}

const handleConfigChange = async (configKey, value) => {
  try {
    await updateConfig(configKey, { configValue: value.toString() })
    ElMessage.success('配置更新成功')
    fetchStatistics()
  } catch (error) {
    ElMessage.error('配置更新失败')
  }
}

const handleConfigUpdate = async (row) => {
  try {
    await updateConfig(row.configKey, { configValue: row.configValue, description: row.description })
    ElMessage.success('配置更新成功')
    fetchConfigs()
  } catch (error) {
    ElMessage.error('配置更新失败')
  }
}

const fetchUserTalents = async () => {
  loading.value = true
  try {
    const res = await getAllUserTalents()
    userTalents.value = res.data
  } catch (error) {
    console.error('获取用户天赋列表失败:', error)
    ElMessage.error('获取用户天赋列表失败')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const res = await getTalentStatistics()
    statistics.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const getTagType = (value) => {
  if (value >= 8) return 'danger'
  if (value >= 6) return 'warning'
  if (value >= 4) return 'primary'
  if (value >= 2) return 'success'
  return 'info'
}

const calculatePercentage = (value, max) => {
  return Math.round((parseFloat(value) / max) * 100)
}

const openEditDialog = (row) => {
  editForm.id = row.id
  editForm.userId = row.userId
  editForm.appearance = row.appearance
  editForm.intelligence = row.intelligence
  editForm.wealth = row.wealth
  editForm.health = row.health
  editForm.emotional = row.emotional
  editForm.luck = row.luck
  editDialogVisible.value = true
}

const calculateEditTotal = () => {
  return editForm.appearance + editForm.intelligence + editForm.wealth + 
         editForm.health + editForm.emotional + editForm.luck
}

const handleEditSubmit = async () => {
  submitLoading.value = true
  try {
    const talentPoints = {
      appearance: editForm.appearance,
      intelligence: editForm.intelligence,
      wealth: editForm.wealth,
      health: editForm.health,
      emotional: editForm.emotional,
      luck: editForm.luck
    }
    
    await allocateTalentForUser(editForm.userId, talentPoints)
    ElMessage.success('天赋更新成功')
    editDialogVisible.value = false
    fetchUserTalents()
    fetchStatistics()
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    submitLoading.value = false
  }
}

const resetUserTalent = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 ${row.userId} 的所有天赋点吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const resetPoints = {
      appearance: 0,
      intelligence: 0,
      wealth: 0,
      health: 0,
      emotional: 0,
      luck: 0
    }
    
    await allocateTalentForUser(row.userId, resetPoints)
    ElMessage.success('重置成功')
    fetchUserTalents()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置失败:', error)
    }
  }
}

const viewUserLogs = async (row) => {
  currentUserId.value = row.userId
  logsDialogVisible.value = true
  fetchUserLogs()
}

const fetchUserLogs = async () => {
  if (!currentUserId.value) return
  
  logsLoading.value = true
  try {
    const res = await getUserSimulationLogs(currentUserId.value, logFilterType.value)
    userLogs.value = res.data
  } catch (error) {
    console.error('获取用户日志失败:', error)
    ElMessage.error('获取日志失败')
  } finally {
    logsLoading.value = false
  }
}

const getSimulationTagType = (type) => {
  const typeMap = {
    career: 'primary',
    relationship: 'danger',
    health: 'success',
    wealth: 'warning',
    overall: 'info'
  }
  return typeMap[type] || 'info'
}

const getSimulationTypeName = (type) => {
  const nameMap = {
    career: '事业',
    relationship: '感情',
    health: '健康',
    wealth: '财富',
    overall: '综合评估'
  }
  return nameMap[type] || type
}

const getTalentName = (key) => {
  const nameMap = {
    appearance: '颜值',
    intelligence: '智商',
    wealth: '财运',
    health: '健康',
    emotional: '情商',
    luck: '运气'
  }
  return nameMap[key] || key
}

const showLogDetail = (row) => {
  currentLogDetail.value = row
  
  try {
    parsedTalentSnapshot.value = JSON.parse(row.talentSnapshot)
    parsedSimulationResult.value = JSON.parse(row.simulationResult)
  } catch (e) {
    parsedTalentSnapshot.value = null
    parsedSimulationResult.value = null
  }
  
  logDetailVisible.value = true
}

onMounted(() => {
  fetchConfigs()
  fetchUserTalents()
  fetchStatistics()
})
</script>

<style lang="scss" scoped>
.admin-talent-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;

    .header-actions {
      display: flex;
      align-items: center;
    }
  }

  .config-key {
    font-family: 'Courier New', monospace;
    color: #409EFF;
  }

  .stat-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
    border: 1px solid #ebeef5;

    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 24px;
      margin-right: 16px;
    }

    .stat-info {
      display: flex;
      flex-direction: column;

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #333;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .averages-section {
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;

    h4 {
      margin-bottom: 16px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }

    .averages-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
    }

    .average-item {
      padding: 16px;
      background: #f5f7fa;
      border-radius: 8px;

      .average-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .average-name {
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }

        .average-value {
          font-size: 18px;
          font-weight: 600;
          color: #409EFF;
        }
      }

      .average-footer {
        margin-top: 8px;
        text-align: right;

        .average-max {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }

  .points-used {
    font-weight: 600;
    color: #409EFF;
  }

  .total-points-display {
    font-size: 18px;
    font-weight: 600;
    color: #409EFF;
  }

  .logs-filter {
    margin-bottom: 16px;
  }

  .log-detail-content {
    h4 {
      margin-bottom: 12px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }

    .talent-snapshot {
      .snapshot-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px;
        background: #f5f7fa;
        border-radius: 6px;
        margin-bottom: 8px;

        .snapshot-label {
          font-size: 14px;
          color: #666;
        }

        .snapshot-value {
          font-size: 16px;
          font-weight: 600;
          color: #409EFF;
        }
      }
    }

    .simulation-result-detail {
      pre {
        background: #282c34;
        color: #abb2bf;
        padding: 16px;
        border-radius: 8px;
        font-size: 12px;
        overflow-x: auto;
        max-height: 300px;
        overflow-y: auto;
      }
    }
  }
}
</style>
