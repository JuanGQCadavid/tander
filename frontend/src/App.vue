<template>
    <div id="app">
        <nav class="navbar">
            <div class="nav-left">
                <router-link to="/">Home</router-link>
                <router-link :to="`/profile/${profileId}`">Profile</router-link>
                <router-link to="/chat">Chats</router-link>
                <router-link to="/search">Search</router-link>
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
export default {
    name: 'App',
    data() {
        return {
            // Used to trigger reactivity manually
            authCheckKey: 0
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
