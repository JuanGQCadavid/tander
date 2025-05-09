<template>
    <div id="app">
        <nav class="navbar">
            <div class="nav-left">
                <router-link to="/">Home</router-link>
                <router-link :to="`/profile/${profileId}`">Profile</router-link>
                <router-link to="/chat">Chats</router-link>
                <router-link to="/search">Search</router-link>
                
            </div>
            <div class="nav-items">
                <NotificationsPanel :userId="1" />
            </div>
            <div class="nav-right">
                <!-- <NotificationsPanel /> -->
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
            userId: "1"
        };
    },
    computed: {
        isLoggedIn() {
            const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
            return !!user.id;
        },
        profileId() {
            const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
            return user.id || '';
        }
    },
    watch: {
        $route() {
            this.authCheckKey++;
        }
    },
    methods: {
        logout() {
            localStorage.removeItem('user');
            sessionStorage.removeItem('user');
            this.$router.go();
        }
    },
    mounted() {
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
            
            if (event.data != "OK"){
                alert("📩 New notification! " + event.data);
            }
        };

        this.socket.onclose = () => {
            console.log('WebSocket disconnected');
            this.isConnected = false;
            this.socket = null;
        };

        this.socket.onerror = (err) => {
        console.error('WebSocket error:', err);
        };
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
  position: relative; /* This is important */
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
