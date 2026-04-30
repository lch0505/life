<template>
  <div class="goal-container">
    <div class="page-header">
      <h2>🎯 年度目标</h2>
      <div class="header-actions">
        <el-select v-model="currentYear" @change="handleYearChange" style="width: 120px; margin-right: 12px">
          <el-option
            v-for="year in yearOptions"
            :key="year"
            :label="`${year}年`"
            :value="year"
          />
        </el-select>
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加目标
        </el-button>
      </div>
    </div>

    <div class="goal-stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <span class="stat-label">目标总数</span>
            <span class="stat-value">{{ goalStats.total }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <span class="stat-label">进行中</span>
            <span class="stat-value primary">{{ goalStats.inProgress }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <span class="stat-label">已完成</span>
            <span class="stat-value success">{{ goalStats.completed }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <span class="stat-label">平均进度</span>
            <span class="stat-value warning">{{ goalStats.avgProgress }}%</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="goal-list">
      <el-empty v-if="goalList.length === 0" description="暂无年度目标，点击上方按钮添加" />
      <el-card
        v-else
        class="goal-card"
        v-for="item in goalList"
        :key="item.id"
        :class="`status-${item.status}`"
      >
        <div class="card-header">
          <div class="card-title">
            <span class="priority-badge" :class="`priority-${item.priority}`">
              {{ getPriorityText(item.priority) }}
            </span>
            <span class="title-text">{{ item.title }}</span>
            <el-tag :type="getGoalStatusType(item.status)" size="small">
              {{ getGoalStatusText(item.status) }}
            </el-tag>
          </div>
          <div class="card-actions">
            <el-tooltip content="查看详情">
              <el-icon class="view-icon" @click="openDetailDialog(item)">
                <View />
              </el-icon>
            </el-tooltip>
            <el-tooltip content="编辑">
              <el-icon class="edit-icon" @click="openEditDialog(item)">
                <Edit />
              </el-icon>
            </el-tooltip>
            <el-tooltip content="删除">
              <el-icon class="delete-icon" @click="handleDeleteGoal(item)">
                <Delete />
              </el-icon>
            </el-tooltip>
          </div>
        </div>

        <div class="card-description" v-if="item.description">
          {{ item.description }}
        </div>

        <div class="card-progress">
          <el-progress
            :percentage="parseFloat(item.progress || 0)"
            :stroke-width="16"
            :color="getProgressColor(item.status)"
          />
        </div>

        <div class="card-footer">
          <div class="date-info">
            <span v-if="item.startDate">
              <el-icon><Calendar /></el-icon>
              {{ item.startDate }} ~ {{ item.endDate || '未设置' }}
            </span>
          </div>
          <div class="task-info" v-if="item.tasks?.length > 0">
            <span>
              <el-icon><List /></el-icon>
              拆解任务: {{ item.tasks.length }} 个
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog
      v-model="goalDialogVisible"
      :title="isGoalEdit ? '编辑年度目标' : '添加年度目标'"
      width="600px"
    >
      <el-form
        ref="goalFormRef"
        :model="goalForm"
        :rules="goalRules"
        label-width="80px"
      >
        <el-form-item label="年份" prop="year">
          <el-select v-model="goalForm.year" placeholder="选择年份" style="width: 100%">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="`${year}年`"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="goalForm.title" placeholder="请输入目标标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="goalForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入目标描述（选填）"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="goalForm.priority" placeholder="选择优先级" style="width: 100%">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="goalForm.status" placeholder="选择状态" style="width: 100%">
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="goalForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="goalForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="goalDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="goalSubmitLoading" @click="handleGoalSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="目标详情"
      width="700px"
    >
      <div class="goal-detail" v-if="currentGoal">
        <div class="detail-header">
          <h3>{{ currentGoal.title }}</h3>
          <el-tag :type="getGoalStatusType(currentGoal.status)" size="large">
            {{ getGoalStatusText(currentGoal.status) }}
          </el-tag>
        </div>
        <div class="detail-info">
          <p v-if="currentGoal.description"><strong>描述：</strong>{{ currentGoal.description }}</p>
          <p><strong>优先级：</strong>{{ getPriorityText(currentGoal.priority) }}</p>
          <p><strong>当前进度：</strong>{{ currentGoal.progress || '0.00' }}%</p>
          <p v-if="currentGoal.startDate">
            <strong>时间范围：</strong>{{ currentGoal.startDate }} ~ {{ currentGoal.endDate || '未设置' }}
          </p>
        </div>

        <el-divider />

        <div class="task-section">
          <div class="task-header">
            <h4>拆解任务</h4>
            <el-button type="primary" size="small" @click="openAddTaskDialog">
              <el-icon><Plus /></el-icon>
              添加任务
            </el-button>
          </div>
          <el-empty v-if="currentGoal.tasks?.length === 0" description="暂无拆解任务" />
          <div v-else class="task-list">
            <div class="task-item" v-for="task in currentGoal.tasks" :key="task.id">
              <div class="task-info">
                <span class="task-title">{{ task.title }}</span>
                <el-tag :type="getTaskStatusType(task.status)" size="small">
                  {{ getGoalStatusText(task.status) }}
                </el-tag>
              </div>
              <div class="task-progress">
                <el-progress
                  :percentage="parseFloat(task.progress || 0)"
                  :stroke-width="10"
                  style="width: 200px"
                />
              </div>
              <div class="task-actions">
                <el-tooltip content="编辑">
                  <el-icon class="edit-icon" @click="openEditTaskDialog(task)">
                    <Edit />
                  </el-icon>
                </el-tooltip>
                <el-tooltip content="删除">
                  <el-icon class="delete-icon" @click="handleDeleteTask(task)">
                    <Delete />
                  </el-icon>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="taskDialogVisible"
      :title="isTaskEdit ? '编辑任务' : '添加任务'"
      width="500px"
    >
      <el-form
        ref="taskFormRef"
        :model="taskForm"
        :rules="taskRules"
        label-width="80px"
      >
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入任务描述（选填）"
          />
        </el-form-item>
        <el-form-item label="进度" prop="progress">
          <el-slider
            v-model="taskForm.progress"
            :min="0"
            :max="100"
            :step="1"
            show-input
            show-input-controls
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="taskForm.status" placeholder="选择状态" style="width: 100%">
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="taskForm.deadline"
            type="date"
            placeholder="选择截止日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="taskSubmitLoading" @click="handleTaskSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import {
  getGoalListWithProgress,
  createGoal,
  updateGoal,
  deleteGoal,
  getGoalTasks,
  createGoalTask,
  updateGoalTask,
  deleteGoalTask,
  updateGoalTaskProgress
} from '@/api/goal'

const currentYear = ref(dayjs().year())
const yearOptions = computed(() => {
  const current = dayjs().year()
  return [current - 2, current - 1, current, current + 1, current + 2]
})

const goalList = ref([])
const goalDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const taskDialogVisible = ref(false)
const isGoalEdit = ref(false)
const isTaskEdit = ref(false)
const goalSubmitLoading = ref(false)
const taskSubmitLoading = ref(false)
const goalFormRef = ref(null)
const taskFormRef = ref(null)
const currentGoal = ref(null)

const goalForm = reactive({
  id: null,
  year: dayjs().year(),
  title: '',
  description: '',
  priority: 1,
  status: 1,
  startDate: '',
  endDate: ''
})

const taskForm = reactive({
  id: null,
  title: '',
  description: '',
  progress: 0,
  status: 1,
  deadline: ''
})

const goalRules = {
  year: [{ required: true, message: '请选择年份', trigger: 'change' }],
  title: [
    { required: true, message: '请输入目标标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

const taskRules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ],
  progress: [{ required: true, message: '请设置进度', trigger: 'change' }]
}

const goalStats = computed(() => {
  const list = goalList.value
  const total = list.length
  const inProgress = list.filter(item => item.status === 1).length
  const completed = list.filter(item => item.status === 2).length
  const avgProgress = total > 0
    ? (list.reduce((sum, item) => sum + parseFloat(item.progress || 0), 0) / total).toFixed(2)
    : '0.00'

  return { total, inProgress, completed, avgProgress }
})

const getPriorityText = (priority) => {
  const map = { 1: '低', 2: '中', 3: '高' }
  return map[priority] || '低'
}

const getGoalStatusText = (status) => {
  const map = { 0: '未开始', 1: '进行中', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const getGoalStatusType = (status) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const getTaskStatusType = (status) => {
  const map = { 0: '', 1: 'primary', 2: 'success', 3: 'danger' }
  return map[status] || ''
}

const getProgressColor = (status) => {
  const map = {
    0: '#909399',
    1: '#409EFF',
    2: '#67C23A',
    3: '#F56C6C'
  }
  return map[status] || '#409EFF'
}

const fetchGoalList = async () => {
  try {
    const res = await getGoalListWithProgress(currentYear.value)
    goalList.value = res.data
  } catch (error) {
    console.error('获取目标列表失败:', error)
  }
}

const handleYearChange = () => {
  fetchGoalList()
}

const resetGoalForm = () => {
  goalForm.id = null
  goalForm.year = currentYear.value
  goalForm.title = ''
  goalForm.description = ''
  goalForm.priority = 1
  goalForm.status = 1
  goalForm.startDate = ''
  goalForm.endDate = ''
}

const resetTaskForm = () => {
  taskForm.id = null
  taskForm.title = ''
  taskForm.description = ''
  taskForm.progress = 0
  taskForm.status = 1
  taskForm.deadline = ''
}

const openAddDialog = () => {
  isGoalEdit.value = false
  resetGoalForm()
  goalDialogVisible.value = true
}

const openEditDialog = (item) => {
  isGoalEdit.value = true
  goalForm.id = item.id
  goalForm.year = item.year
  goalForm.title = item.title
  goalForm.description = item.description || ''
  goalForm.priority = item.priority
  goalForm.status = item.status
  goalForm.startDate = item.startDate || ''
  goalForm.endDate = item.endDate || ''
  goalDialogVisible.value = true
}

const openDetailDialog = async (item) => {
  currentGoal.value = { ...item }
  try {
    const res = await getGoalTasks(item.id)
    currentGoal.value.tasks = res.data
  } catch (error) {
    console.error('获取任务列表失败:', error)
    currentGoal.value.tasks = []
  }
  detailDialogVisible.value = true
}

const openAddTaskDialog = () => {
  isTaskEdit.value = false
  resetTaskForm()
  taskDialogVisible.value = true
}

const openEditTaskDialog = (task) => {
  isTaskEdit.value = true
  taskForm.id = task.id
  taskForm.title = task.title
  taskForm.description = task.description || ''
  taskForm.progress = parseFloat(task.progress || 0)
  taskForm.status = task.status
  taskForm.deadline = task.deadline || ''
  taskDialogVisible.value = true
}

const handleGoalSubmit = async () => {
  const valid = await goalFormRef.value.validate().catch(() => false)
  if (!valid) return

  goalSubmitLoading.value = true
  try {
    const submitData = {
      year: goalForm.year,
      title: goalForm.title,
      description: goalForm.description || undefined,
      priority: goalForm.priority,
      status: goalForm.status,
      startDate: goalForm.startDate || undefined,
      endDate: goalForm.endDate || undefined
    }

    if (isGoalEdit.value) {
      await updateGoal(goalForm.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createGoal(submitData)
      ElMessage.success('创建成功')
    }

    goalDialogVisible.value = false
    fetchGoalList()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    goalSubmitLoading.value = false
  }
}

const handleTaskSubmit = async () => {
  const valid = await taskFormRef.value.validate().catch(() => false)
  if (!valid) return

  taskSubmitLoading.value = true
  try {
    const submitData = {
      title: taskForm.title,
      description: taskForm.description || undefined,
      progress: taskForm.progress,
      status: taskForm.status,
      deadline: taskForm.deadline || undefined
    }

    if (isTaskEdit.value) {
      await updateGoalTask(taskForm.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createGoalTask(currentGoal.value.id, submitData)
      ElMessage.success('创建成功')
    }

    taskDialogVisible.value = false
    const res = await getGoalTasks(currentGoal.value.id)
    currentGoal.value.tasks = res.data
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    taskSubmitLoading.value = false
  }
}

const handleDeleteGoal = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这个年度目标吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteGoal(item.id)
    ElMessage.success('删除成功')
    fetchGoalList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleDeleteTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteGoalTask(task.id)
    ElMessage.success('删除成功')
    const res = await getGoalTasks(currentGoal.value.id)
    currentGoal.value.tasks = res.data
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  fetchGoalList()
})
</script>

<style lang="scss" scoped>
.goal-container {
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

    .header-actions {
      display: flex;
      align-items: center;
    }
  }

  .goal-stats {
    margin-bottom: 20px;

    .stat-card {
      background: #fff;
      border-radius: 8px;
      padding: 20px;
      text-align: center;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

      .stat-label {
        display: block;
        font-size: 14px;
        color: #999;
        margin-bottom: 8px;
      }

      .stat-value {
        display: block;
        font-size: 28px;
        font-weight: 600;
        color: #333;

        &.primary {
          color: #409eff;
        }

        &.success {
          color: #67c23a;
        }

        &.warning {
          color: #e6a23c;
        }
      }
    }
  }

  .goal-list {
    .goal-card {
      margin-bottom: 16px;

      &.status-0 {
        opacity: 0.8;
      }

      &.status-2 {
        background: linear-gradient(135deg, #f0f9eb 0%, #fff 100%);
      }

      &.status-3 {
        opacity: 0.6;
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .card-title {
          display: flex;
          align-items: center;
          gap: 12px;

          .priority-badge {
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;

            &.priority-1 {
              background: #f4f4f5;
              color: #909399;
            }

            &.priority-2 {
              background: #ecf5ff;
              color: #409eff;
            }

            &.priority-3 {
              background: #fef0f0;
              color: #f56c6c;
            }
          }

          .title-text {
            font-size: 16px;
            font-weight: 600;
            color: #333;
          }
        }

        .card-actions {
          display: flex;
          gap: 12px;

          .view-icon {
            color: #999;
            font-size: 16px;
            cursor: pointer;

            &:hover {
              color: #409eff;
            }
          }

          .edit-icon {
            color: #999;
            font-size: 16px;
            cursor: pointer;

            &:hover {
              color: #67c23a;
            }
          }

          .delete-icon {
            color: #999;
            font-size: 16px;
            cursor: pointer;

            &:hover {
              color: #f56c6c;
            }
          }
        }
      }

      .card-description {
        color: #666;
        font-size: 13px;
        margin-bottom: 16px;
        line-height: 1.5;
      }

      .card-progress {
        margin-bottom: 12px;
      }

      .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 12px;
        border-top: 1px solid #f0f0f0;
        font-size: 12px;
        color: #999;

        .date-info,
        .task-info {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  .goal-detail {
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        font-size: 18px;
        color: #333;
        margin: 0;
      }
    }

    .detail-info {
      p {
        margin: 8px 0;
        color: #666;
        line-height: 1.6;

        strong {
          color: #333;
        }
      }
    }

    .task-section {
      .task-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h4 {
          font-size: 16px;
          color: #333;
          margin: 0;
        }
      }

      .task-list {
        .task-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 12px;
          background: #f5f7fa;
          border-radius: 8px;
          margin-bottom: 8px;

          .task-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .task-title {
              font-size: 14px;
              color: #333;
            }
          }

          .task-actions {
            display: flex;
            gap: 12px;

            .edit-icon {
              color: #999;
              font-size: 14px;
              cursor: pointer;

              &:hover {
                color: #67c23a;
              }
            }

            .delete-icon {
              color: #999;
              font-size: 14px;
              cursor: pointer;

              &:hover {
                color: #f56c6c;
              }
            }
          }
        }
      }
    }
  }
}
</style>
