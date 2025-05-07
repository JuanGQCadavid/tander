<template>
    <div class="register-container">
        <h1>Create an Account</h1>
        <form @submit.prevent="registerUser" class="register-form">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" v-model="form.email" required placeholder="Enter your email" />
                <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
            </div>

            <div class="form-group">
                <label for="phoneNumber">Phone Number</label>
                <input type="tel" id="phoneNumber" v-model="form.phoneNumber" placeholder="Enter your phone number" />
                <span v-if="errors.phoneNumber" class="error-message">{{ errors.phoneNumber }}</span>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" v-model="form.password" required placeholder="Create a password" />
                <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" v-model="confirmPassword" required
                    placeholder="Confirm your password" />
                <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
            </div>

            <button type="submit" class="register-button" :disabled="loading">
                {{ loading ? 'Registering...' : 'Register' }}
            </button>

            <div v-if="apiError" class="api-error">
                {{ apiError }}
            </div>

            <div class="login-link">
                Already have an account? <router-link to="/login">Login</router-link>
            </div>
        </form>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'RegisterView',
    data() {
        return {
            form: {
                email: '',
                phoneNumber: '',
                password: ''
            },
            confirmPassword: '',
            errors: {
                email: '',
                phoneNumber: '',
                password: '',
                confirmPassword: ''
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
                phoneNumber: '',
                password: '',
                confirmPassword: ''
            };

            // Email validation
            if (!this.form.email) {
                this.errors.email = 'Email is required';
                isValid = false;
            } else if (!/^\S+@\S+\.\S+$/.test(this.form.email)) {
                this.errors.email = 'Please enter a valid email address';
                isValid = false;
            }

            // Phone validation - basic format check
            if (this.form.phoneNumber && !/^\+?[0-9()-\s]{8,}$/.test(this.form.phoneNumber)) {
                this.errors.phoneNumber = 'Please enter a valid phone number';
                isValid = false;
            }

            // Password validation
            if (!this.form.password) {
                this.errors.password = 'Password is required';
                isValid = false;
            } else if (this.form.password.length < 8) {
                this.errors.password = 'Password must be at least 8 characters';
                isValid = false;
            }

            // Confirm password validation
            if (this.form.password !== this.confirmPassword) {
                this.errors.confirmPassword = 'Passwords do not match';
                isValid = false;
            }

            return isValid;
        },

        async registerUser() {
            if (!this.validateForm()) {
                return;
            }

            this.loading = true;
            this.apiError = '';

            try {
                console.log(this.form)
                const response = await axios.post('http://localhost:8003/api/user/register', this.form);

                console.log('Registration successful:', response.data);

                // Store user data or token if returned
                if (response.data) {
                    localStorage.setItem('user', JSON.stringify(response.data));
                }

                this.$router.push('/home');
            } catch (error) {
                console.error('Registration error:', error);
                this.apiError = error.response?.data?.message ||
                    'Registration failed. Please try again later.';
            } finally {
                this.loading = false;
            }
        }
    }
}
</script>

<style scoped>
.register-container {
    max-width: 500px;
    margin: 40px auto;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    background: #fff;
}

h1 {
    text-align: center;
    margin-bottom: 24px;
    color: #333;
}

.form-group {
    margin-bottom: 16px;
}

label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
    color: #555;
}

input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
}

input:focus {
    outline: none;
    border-color: #4a90e2;
    box-shadow: 0 0 3px rgba(74, 144, 226, 0.3);
}

.register-button {
    width: 100%;
    padding: 12px;
    background-color: #4a90e2;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    margin-top: 10px;
}

.register-button:hover {
    background-color: #3a80d2;
}

.register-button:disabled {
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

.login-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
}

.login-link a {
    color: #4a90e2;
    text-decoration: none;
}

.login-link a:hover {
    text-decoration: underline;
}
</style>