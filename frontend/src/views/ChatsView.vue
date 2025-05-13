<template>
    <div >
        <h1>This is a Chats view page</h1>
        <div class="item" v-for="chat in chats" :key="chat.chatId">
            <ChatList :chatId="chat.chatId" :chatWith="chat.chatWith" :isActive="chat.isActive" :chatSince="chat.chatSince" :users="chat.users"/>
        </div>
    </div>
</template>

<script>
import ChatList from '@/components/ChatList.vue';

export default {
  name: 'ChatsView',
  components: {
    ChatList
  },
  data: () => {
    return {
        userId: "",
        chats: []
    }
  },
  methods: {
    fetchMembers(chatId, userId){
        fetch(`http://localhost:8009/api/chat/members/${chatId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'userId': userId,
                },
            })
            .then(response => response.json())
            .then(data => {
                console.log("Fetching members")
                console.log(data)
                data.forEach(member => {
                    const chatIndex = this.chats.findIndex(chat => chat.chatId === member.chatId);
                    if (chatIndex !== -1) {
                        this.chats[chatIndex].users.push({
                            userId: member.userId,
                            email: member.email,
                        }); 
                    } else {
                    console.log('Chat not found');
                    }
                })

            })
            .catch(error => {
                console.error('Error fetching chats:', error);
            });
    },
    fetchChats(userId) {
        fetch(`http://localhost:8009/api/chat/`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'userId': userId,
                },
            })
            .then(response => response.json())
            .then(data => {
                console.log(data)
                data.forEach(chat => {
                    this.chats.push({
                        chatId:  chat.id,
                        isActive: chat.isActive,
                        chatSince: chat.dateOfCreation,
                        users: [],
                    });

                    this.fetchMembers(chat.id, userId)
                })

            })
            .catch(error => {
                console.error('Error fetching chats:', error);
            });
    }
  },
  mounted(){

        const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');

        if(user.id == null) {
            this.$router.push({
              name: "Login", 
            });
        }

        console.log('ChatView component is created')
        console.log("user ID - " + user.id)
        this.fetchChats(user.id)
        this.userId = user.id
        return




    }
}
</script>

<style>
</style>