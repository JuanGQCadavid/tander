<template>
    <nav>
        <router-link to="/">Home</router-link> |
        <router-link to="/register">Register</router-link>|
        <router-link to="/login">Login</router-link> |
        <router-link to="/profile">Profile</router-link> |
    </nav>
    <router-view />
    <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App" />
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'

export default {
    name: 'App',
    components: {
        HelloWorld
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
</style>
