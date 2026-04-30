<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1>🌍 人生进度条</h1>
        <p class="subtitle">加入我们，开始记录你的人生</p>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名（4-20位字母数字下划线）"
            prefix-icon="User"
            size="large"
            @blur="checkUsernameExists"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（6-20位）"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称（选填）"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱（选填）"
            prefix-icon="Message"
            size="large"
            @blur="checkEmailExists"
          />
        </el-form-item>
        <el-form-item prop="birthDate">
          <el-date-picker
            v-model="registerForm.birthDate"
            type="date"
            placeholder="请选择出生日期（选填，用于计算人生进度）"
            size="large"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-btn"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { register, checkUsername, checkEmail } from '@/api/auth'
import dayjs from 'dayjs'

const router = useRouter()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  birthDate: null
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const disabledDate = (time) => {
  return time.getTime() > Date.now()
}

const checkUsernameExists = async () => {
  if (!registerForm.username) return
  try {
    const res = await checkUsername(registerForm.username)
    if (res.data) {
      ElMessage.warning('该用户名已被使用')
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
  }
}

const checkEmailExists = async () => {
  if (!registerForm.email) return
  try {
    const res = await checkEmail(registerForm.email)
    if (res.data) {
      ElMessage.warning('该邮箱已被注册')
    }
  } catch (error) {
    console.error('检查邮箱失败:', error)
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const submitData = {
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname || undefined,
      email: registerForm.email || undefined,
      birthDate: registerForm.birthDate ? dayjs(registerForm.birthDate).format('YYYY-MM-DD') : undefined
    }

    await register(submitData)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 0;
  overflow-y: auto;

  .register-box {
    width: 420px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);

    .register-header {
      text-align: center;
      margin-bottom: 30px;

      h1 {
        font-size: 28px;
        color: #333;
        margin-bottom: 8px;
      }

      .subtitle {
        color: #666;
        font-size: 14px;
      }
    }

    .register-form {
      .register-btn {
        width: 100%;
        height: 48px;
        font-size: 16px;
      }
    }

    .register-footer {
      text-align: center;
      margin-top: 20px;

      span {
        color: #666;
        font-size: 14px;
      }

      a {
        color: #1890ff;
        font-size: 14px;
        text-decoration: none;
        margin-left: 4px;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>
