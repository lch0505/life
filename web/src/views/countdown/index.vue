<template>
  <div class="countdown-container">
    <div class="page-header">
      <h2>⏱️ 自定义倒计时</h2>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加倒计时
      </el-button>
    </div>

    <div class="category-tabs">
      <el-tag
        v-for="cat in categories"
        :key="cat"
        :type="currentCategory === cat ? 'primary' : ''"
        @click="selectCategory(cat)"
        class="category-tag"
      >
        {{ cat }}
      </el-tag>
    </div>

    <div class="countdown-grid">
      <el-empty v-if="countdownList.length === 0" description="暂无倒计时，点击上方按钮添加" />
      <div 
        v-else 
        class="countdown-card" 
        v-for="item in countdownList" 
        :key="item.id"
        :style="{ background: `linear-gradient(135deg, ${item.color}20 0%, ${item.color}10 100%)`, borderLeft: `4px solid ${item.color}` }"
      >
        <div class="card-header">
          <div class="card-title">
            <span class="title-text">{{ item.title }}</span>
            <el-tag :type="getCategoryType(item.category)" size="small">{{ item.category }}</el-tag>
          </div>
          <div class="card-actions">
            <el-tooltip content="置顶">
              <el-icon 
                :class="{ 'top-icon': true, active: item.isTop === 1 }" 
                @click="toggleTop(item)"
              >
                <Top />
              </el-icon>
            </el-tooltip>
            <el-tooltip content="编辑">
              <el-icon class="edit-icon" @click="openEditDialog(item)">
                <Edit />
              </el-icon>
            </el-tooltip>
            <el-tooltip content="删除">
              <el-icon class="delete-icon" @click="handleDelete(item)">
                <Delete />
              </el-icon>
            </el-tooltip>
          </div>
        </div>
        
        <div class="card-description" v-if="item.description">
          {{ item.description }}
        </div>

        <div class="countdown-display">
          <div class="countdown-value" :class="{ past: item.isPast }">
            <span class="days">{{ item.days }}</span>
            <span class="unit">天</span>
          </div>
          <div class="countdown-time">
            <div class="time-item">
              <span class="time-value">{{ item.hours.toString().padStart(2, '0') }}</span>
              <span class="time-unit">时</span>
            </div>
            <div class="time-separator">:</div>
            <div class="time-item">
              <span class="time-value">{{ item.minutes.toString().padStart(2, '0') }}</span>
              <span class="time-unit">分</span>
            </div>
            <div class="time-separator">:</div>
            <div class="time-item">
              <span class="time-value">{{ item.seconds.toString().padStart(2, '0') }}</span>
              <span class="time-unit">秒</span>
            </div>
          </div>
        </div>

        <div class="card-footer">
          <span class="target-date">
            <el-icon><Calendar /></el-icon>
            {{ item.targetDate }} {{ item.targetTime || '' }}
          </span>
          <span class="status-text" :class="item.isPast ? 'past' : 'future'">
            {{ item.isPast ? '已过去' : '倒计时' }}
          </span>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑倒计时' : '添加倒计时'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入倒计时标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入描述（选填）"
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat"
              :label="cat"
              :value="cat"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标日期" prop="targetDate">
          <el-date-picker
            v-model="form.targetDate"
            type="date"
            placeholder="选择目标日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="目标时间">
          <el-time-picker
            v-model="form.targetTime"
            placeholder="选择目标时间（选填）"
            style="width: 100%"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="主题颜色">
          <el-color-picker v-model="form.color" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import {
  getCountdownListWithRemaining,
  getCountdownCategories,
  createCountdown,
  updateCountdown,
  updateCountdownTop,
  deleteCountdown
} from '@/api/countdown'

const categories = ref([])
const currentCategory = ref('全部')
const countdownList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
let countdownTimer = null

const form = reactive({
  id: null,
  title: '',
  description: '',
  category: '',
  targetDate: '',
  targetTime: '',
  color: '#1890ff'
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  targetDate: [
    { required: true, message: '请选择目标日期', trigger: 'change' }
  ]
}

const filteredList = computed(() => {
  if (currentCategory.value === '全部') {
    return countdownList.value
  }
  return countdownList.value.filter(item => item.category === currentCategory.value)
})

const getCategoryType = (category) => {
  const types = {
    '婚礼': 'danger',
    '旅行': 'success',
    '考试': 'warning',
    '纪念日': 'primary',
    '发薪日': 'success',
    '其他': 'info'
  }
  return types[category] || 'info'
}

const fetchCategories = async () => {
  try {
    const res = await getCountdownCategories()
    categories.value = ['全部', ...res.data]
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchCountdownList = async () => {
  try {
    const res = await getCountdownListWithRemaining()
    countdownList.value = res.data.sort((a, b) => {
      if (a.isTop !== b.isTop) return b.isTop - a.isTop
      if (a.isPast !== b.isPast) return a.isPast ? 1 : -1
      return a.totalSeconds - b.totalSeconds
    })
  } catch (error) {
    console.error('获取倒计时列表失败:', error)
  }
}

const selectCategory = (category) => {
  currentCategory.value = category
}

const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (item) => {
  isEdit.value = true
  form.id = item.id
  form.title = item.title
  form.description = item.description || ''
  form.category = item.category
  form.targetDate = item.targetDate
  form.targetTime = item.targetTime || ''
  form.color = item.color
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.description = ''
  form.category = ''
  form.targetDate = ''
  form.targetTime = ''
  form.color = '#1890ff'
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const submitData = {
      title: form.title,
      description: form.description || undefined,
      category: form.category,
      targetDate: form.targetDate,
      targetTime: form.targetTime || undefined,
      color: form.color
    }

    if (isEdit.value) {
      await updateCountdown(form.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await createCountdown(submitData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    fetchCountdownList()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const toggleTop = async (item) => {
  try {
    await updateCountdownTop(item.id, item.isTop === 1 ? 0 : 1)
    ElMessage.success(item.isTop === 1 ? '已取消置顶' : '已置顶')
    fetchCountdownList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这个倒计时吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCountdown(item.id)
    ElMessage.success('删除成功')
    fetchCountdownList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const startCountdownTimer = () => {
  countdownTimer = setInterval(() => {
    countdownList.value.forEach(item => {
      if (item.isPast) {
        item.totalSeconds++
      } else {
        if (item.totalSeconds > 0) {
          item.totalSeconds--
        } else {
          item.isPast = true
        }
      }
      item.days = Math.floor(Math.abs(item.totalSeconds) / (24 * 60 * 60))
      item.hours = Math.floor((Math.abs(item.totalSeconds) % (24 * 60 * 60)) / (60 * 60))
      item.minutes = Math.floor((Math.abs(item.totalSeconds) % (60 * 60)) / 60)
      item.seconds = Math.abs(item.totalSeconds) % 60
    })
  }, 1000)
}

onMounted(() => {
  fetchCategories()
  fetchCountdownList()
  startCountdownTimer()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style lang="scss" scoped>
.countdown-container {
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

  .category-tabs {
    margin-bottom: 20px;

    .category-tag {
      cursor: pointer;
      margin-right: 8px;

      &:hover {
        opacity: 0.8;
      }
    }
  }

  .countdown-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;

    .countdown-card {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s, box-shadow 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .card-title {
          display: flex;
          align-items: center;
          gap: 8px;

          .title-text {
            font-size: 16px;
            font-weight: 600;
            color: #333;
          }
        }

        .card-actions {
          display: flex;
          gap: 12px;

          .top-icon {
            color: #999;
            font-size: 16px;
            cursor: pointer;

            &.active {
              color: #409eff;
            }

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

      .countdown-display {
        text-align: center;
        padding: 20px 0;

        .countdown-value {
          font-size: 48px;
          font-weight: 600;
          color: #409eff;
          margin-bottom: 8px;

          &.past {
            color: #909399;
          }

          .unit {
            font-size: 18px;
            margin-left: 4px;
          }
        }

        .countdown-time {
          display: flex;
          justify-content: center;
          align-items: center;
          gap: 4px;

          .time-item {
            text-align: center;

            .time-value {
              display: block;
              font-size: 20px;
              font-weight: 500;
              color: #666;
              font-family: 'Courier New', monospace;
            }

            .time-unit {
              display: block;
              font-size: 11px;
              color: #999;
            }
          }

          .time-separator {
            font-size: 18px;
            color: #999;
            font-weight: 500;
          }
        }
      }

      .card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 12px;
        border-top: 1px solid #f0f0f0;

        .target-date {
          color: #999;
          font-size: 12px;
          display: flex;
          align-items: center;
          gap: 4px;
        }

        .status-text {
          font-size: 12px;
          padding: 2px 8px;
          border-radius: 4px;

          &.future {
            background: #ecf5ff;
            color: #409eff;
          }

          &.past {
            background: #f4f4f5;
            color: #909399;
          }
        }
      }
    }
  }
}
</style>
