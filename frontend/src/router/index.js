import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import RegisterView from '@/views/RegisterView.vue'
import LoginView from '@/views/LoginView.vue'
import ProfileView from '@/views/ProfileView.vue'
import ChatsView from '@/views/ChatsView.vue'
import ChatingView from '@/views/ChatingView.vue'
import SearchView from "@/views/SearchView.vue";

// import ProfileEditView from '@/views/ProfileEditView.vue'

const routes = [{
    path: '/',
    name: 'home',
    component: HomeView
},
{
    path: '/register',
    name: 'Register',
    component: RegisterView
},
{
    path: "/login",
    name: "Login",
    component: LoginView,
},
{
    path: "/profile/:userId",
    name: "Profile",
    component: ProfileView,
    props: true
},
{
    path: "/chat",
    name: "Chats",
    component: ChatsView,
},
{
    path: "/chat/:chatId",
    name: "Chat",
    component: ChatingView,
    props: true
},
    {
        path: "/search",
        name: 'Search',
        component: SearchView
    }
// {
//     path: "/profile/:userId/edit",
//     name: "ProfileEdit",
//     component: ProfileEditView,
//     props: true
// },
    // {
    //     path: '/about',
    //     name: 'about',
    //     // route level code-splitting
    //     // this generates a separate chunk (about.[hash].js) for this route
    //     // which is lazy-loaded when the route is visited.
    //     component: () =>
    //         import ( /* webpackChunkName: "about" */ '../views/AboutView.vue')
    // }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router