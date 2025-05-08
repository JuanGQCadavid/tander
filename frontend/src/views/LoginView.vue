<template>
    <div class="login-container">
        <h1>Login to Your Account</h1>
        <form @submit.prevent="login" class="login-form">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" v-model="form.email" required placeholder="Enter your email" />
                <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" v-model="form.password" required
                    placeholder="Enter your password" />
                <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            </div>

            <button type="submit" class="login-button" :disabled="loading">
                {{ loading ? 'Logging in...' : 'Login' }}
            </button>

            <div v-if="apiError" class="api-error">
                {{ apiError }}
            </div>

            <div class="register-link">
                Don't have an account? <router-link to="/register">Register</router-link>
            </div>
        </form>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'LoginView',
    data() {
        return {
            form: {
                email: '',
                password: ''
            },
            errors: {
                email: '',
                password: ''
            },
            loading: false,
            apiError: ''
        };
    },
    methods: {
        validateForm() {
            let isValid = true;
            this.errors = {
                email: '',
                password: ''
            };

            // Email validation
            if (!this.form.email) {
                this.errors.email = 'Email is required';
                isValid = false;
            } else if (!/^\S+@\S+\.\S+$/.test(this.form.email)) {
                this.errors.email = 'Please enter a valid email address';
                isValid = false;
            }

            // Password validation
            if (!this.form.password) {
                this.errors.password = 'Password is required';
                isValid = false;
            }

            return isValid;
        },

        async login() {
            if (!this.validateForm()) {
                return;
            }

            this.loading = true;
            this.apiError = '';

            try {
                const response = await axios.post('http://localhost:8003/api/user/login', this.form);

                console.log('Login successful:', response.data);

                if (response.data) {
                    const userData = response.data;
                    sessionStorage.setItem('user', JSON.stringify(userData));
                    this.$router.push('/home');
                }
            } catch (error) {
                console.error('Login error:', error);
                this.apiError = error.response?.data?.message ||
                    'Invalid email or password. Please try again.';
            } finally {
                this.loading = false;
            }
        }
    }
}
</script>

<style scoped>
.login-container {
    max-width: 450px;
    margin: 60px auto;
    padding: 25px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    background: #fff;
}

h1 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
}

.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
    color: #555;
}

input[type="email"],
input[type="password"] {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
}

input:focus {
    outline: none;
    border-color: #4a90e2;
    box-shadow: 0 0 3px rgba(74, 144, 226, 0.3);
}

.remember input {
    margin-right: 8px;
}

.login-button {
    width: 100%;
    padding: 12px;
    background-color: #4a90e2;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
}

.login-button:hover {
    background-color: #3a80d2;
}

.login-button:disabled {
    background-color: #a0c0e8;
    cursor: not-allowed;
}

.error-message {
    color: #e74c3c;
    font-size: 12px;
    margin-top: 4px;
    display: block;
}

.api-error {
    color: #e74c3c;
    text-align: center;
    margin-top: 16px;
    padding: 10px;
    background-color: #fdecea;
    border-radius: 4px;
}

.register-link {
    text-align: center;
    margin-top: 24px;
    font-size: 14px;
}

.register-link a {
    color: #4a90e2;
    text-decoration: none;
}

.register-link a:hover {
    text-decoration: underline;
}
</style>