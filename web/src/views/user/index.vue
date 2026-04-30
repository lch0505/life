<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <span>👤 个人信息</span>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="头像">
          <el-avatar :size="80" icon="UserFilled" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="选择出生日期（用于计算人生进度）"
            style="width: 100%"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
          />
          <div class="form-tip">设置出生日期后，系统可以自动计算您的人生进度</div>
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="form.role === 0 ? 'danger' : 'primary'">
            {{ form.role === 0 ? '超级管理员' : '普通用户' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="form.status === 1 ? 'success' : 'danger'">
            {{ form.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>🔐 修改密码</span>
      </template>
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="120px"
        style="max-width: 600px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码（6-20位）" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="passwordSubmitLoading" @click="handlePasswordSubmit">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>ℹ️ 账户信息</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ form.username }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ userStore.userInfo?.createTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="最后更新时间">
          {{ userStore.userInfo?.updateTime || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo } from '@/api/auth'

const userStore = useUserStore()

const formRef = ref(null)
const passwordFormRef = ref(null)
const submitLoading = ref(false)
const passwordSubmitLoading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  birthDate: '',
  role: 1,
  status: 1
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  nickname: [
    { max: 50, message: '昵称不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    const data = res.data
    form.username = data.username
    form.nickname = data.nickname || ''
    form.email = data.email || ''
    form.phone = data.phone || ''
    form.birthDate = data.birthDate || ''
    form.role = data.role
    form.status = data.status
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const submitData = {
      nickname: form.nickname || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
      birthDate: form.birthDate || undefined
    }

    await updateUserInfo(submitData)
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handlePasswordSubmit = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  passwordSubmitLoading.value = true
  try {
    ElMessage.warning('密码修改功能暂未开放，请联系管理员')
  } finally {
    passwordSubmitLoading.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style lang="scss" scoped>
.user-container {
  .form-tip {
    font-size: 12px;
    color: #999;
    margin-top: 8px;
  }
}
</style>
