<template>
    <div id="app">
        <nav class="navbar">
            <div class="nav-left">
                <router-link to="/">Home</router-link>
                <router-link v-if="isLoggedIn" :to="`/profile/${userId}`">Profile</router-link>
                <router-link to="/chat">Chats</router-link>
                <router-link to="/search">Search</router-link>
            </div>
            <div class="nav-items">
                <NotificationsPanel :userId="userId" v-if="isLoggedIn" />
            </div>
            <div class="nav-right">
                <router-link v-if="!isLoggedIn" to="/login">Login</router-link>
                <button v-else @click="logout" class="logout-button">Logout</button>
            </div>
        </nav>
        <router-view />
    </div>
</template>

<script>
import NotificationsPanel from '@/components/NotificationsPanel.vue';
// import instance from '@/services/Notifications'
export default {
    name: 'App',
    components: {
        NotificationsPanel
    },
    data() {
        return {
            // Used to trigger reactivity manually
            authCheckKey: 0,
            socket: null,
            notificationWS: "ws://localhost:8001/ws",
            userId: null,
            isLoggedIn: false
        };
    },
    computed: {
        
    },
    watch: {
        $route() {
            this.authCheckKey++;
            this.checkAuthStatus();
        }
    },
    created() {
        this.handleLoginBound = this.handleUserLogin.bind(this);
        window.addEventListener('userLoggedIn', this.handleLoginBound);
    },
    beforeUnmount() {
        window.removeEventListener('userLoggedIn', this.handleLoginBound);
    },
    methods: {
        handleUserLogin(event) {
            const userData = event.detail;
            this.userId = userData.id;
            this.isLoggedIn = true;
            this.connectWebSocket();
        },
        logout() {
            localStorage.removeItem('user');
            sessionStorage.removeItem('user');
            this.isLoggedIn = false;
            this.userId = null;
            this.$router.push('/');
        },
        getUserIdFromAuth() {
            const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
            return user.id || null;
        },
        checkIfLoggedIn() {
            const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
            return !!user.id;
        },
        checkAuthStatus() {
            this.userId = this.getUserIdFromAuth();
            this.isLoggedIn = this.checkIfLoggedIn();
        },
        connectWebSocket() {
            if (!this.isLoggedIn || !this.userId) return;
            
            this.socket = new WebSocket(this.notificationWS);
            this.socket.onopen = () => {
                console.log('WebSocket connected!');
                this.socket.send(JSON.stringify(
                    {
                        cmd: "AttachUserId",
                        payload: {
                            userId: this.userId,
                        }
                    }
                ));
            };

            this.socket.onmessage = (event) => {
                console.log('Received:', event.data);

                if (event.data != "OK") {
                    alert("📩 New notification! " + event.data);
                }
            };

            this.socket.onclose = () => {
                console.log('WebSocket disconnected');
                this.socket = null;
            };

            this.socket.onerror = (err) => {
                console.error('WebSocket error:', err);
            };
        }
    },
    mounted() {
        this.checkAuthStatus();
        if (this.isLoggedIn && this.userId) {
            this.connectWebSocket();
        }
    }
}
</script>


<style>
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
}

.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #34495e;
    padding: 10px 20px;
    color: white;
}

.navbar a {
    color: white;
    text-decoration: none;
    margin: 0 10px;
}

.navbar a:hover {
    text-decoration: underline;
}

.nav-items {
    position: relative;
    /* This is important */
}

.logout-button {
    background-color: #e74c3c;
    color: white;
    border: none;
    padding: 6px 12px;
    cursor: pointer;
    border-radius: 4px;
}

.logout-button:hover {
    background-color: #c0392b;
}
</style>