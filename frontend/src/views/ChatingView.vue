<template>
    <div class="about">
      <h1>{{ chatTitle }}</h1>
      <h3>{{ getPowerMessage() }}</h3>
    </div>
    <div class="chat-container">
        <div class="messages">
        <div v-for="msg in messages" :key="msg.id" class="message">
            <div v-if="msg.senderId == userId" class="message-myself">
                <p> {{ msg.textContent }} <br> {{ msg.dateOfCreation }}  </p>
                <!-- <br>
                <i>{{ msg.dateOfCreation }}</i> -->
            </div>
            <div v-if="msg.senderId != userId" class="message-other">
                <p> {{ msg.textContent }} <br> {{ msg.dateOfCreation }}  </p>
            </div>


        </div>
        </div>
        
        <div class="input-area">
        <input 
            v-model="newMessage" 
            @keyup.enter="sendMessage"
            type="text" 
            placeholder="Give me your best flirt line..."
        />
        <button @click="sendMessage">Send</button>
        </div>
  </div>
</template>


<script>
export default {
    name: "ChatingView",
    components: {

    },
    data: () => {
        return {
            messages: [],
            ws: null,
            newMessage: '',
            userId: '',
            chatTitle: "",
            token: "",
        }
    },
    props: {
        chatId: {type: String, required: true},
    },
    methods: {
        getRandomInt(min, max) {
            min = Math.ceil(min);
            max = Math.floor(max);
            return Math.floor(Math.random() * (max - min + 1)) + min;
        },
        getPowerMessage(){
            const possibles= [
                "... The begining of a new netflix romance movie",
                "Somewhere, Taylor Swift just started writing a song about this.",
                "Insert dramatic romantic music and stolen glances.",
                "Flirting mode activated",
                "Warning: This chat may cause butterflies",
            ]
            return possibles[this.getRandomInt(0, 4)]
        },
        fetchMembers(chatId, userId, token){
            fetch(`http://localhost:8009/api/chat/members/${chatId}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                })
                .then(response => response.json())
                .then(data => {
                    console.log("Fetching members")
                    console.log(data)
                    data.forEach(member => {
                        if (member.userId !== userId) {
                            this.chatTitle = member.email + " and you."
                        }
                    })

                })
                .catch(error => {
                    console.error('Error fetching chats:', error);
                });
        },
        fetHistory(token, chatId) {
            fetch(`http://localhost:8009/api/chat/history/${chatId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            })
            .then(response => response.json())
            .then(data => {
                this.messages = data
            })
            .catch(error => {
                console.error('Error fetching messages:', error);
            }); 
        },
        createWebSocket() {
            this.ws = new WebSocket('ws://localhost:8005/ws');
    
            // Connect
            this.ws.onopen = () => {
                console.log('WebSocket connected!');
                this.attachWebsocket(this.userId, this.chatId)
            };

            // when message
            this.ws.onmessage = (event) => {

                if (event.data != "Oks"){
                    const message = JSON.parse(event.data);
                    if (message.cmd == "newMessage") {
                        this.messages.push(message.payload);
                    }
                }

            };

            // Reconecting
            this.ws.onclose = () => {
                console.log('WebSocket closed. Reconnecting...');
                setTimeout(this.connectWebSocket, 1000);
            };

            // Crash!
            this.ws.onerror = (error) => {
                console.error('WebSocket error:', error);
            };
        },
        sendMessage() {
            if (this.newMessage.trim() !== '') {
                this.ws.send(JSON.stringify(
                    {
                        cmd: "NewMessage",
                        payload: {
                            textCotent: this.newMessage,
                            chatId: this.chatId
                        }
                    }
                ));
                this.newMessage = '';
            }
        },
        attachWebsocket() {
            this.ws.send(JSON.stringify(
                {
                    cmd: "AttachUserId",
                    payload: {
                        userId: `Bearer ${this.token}`,
                        chatId: this.chatId
                    }
                }
            ));
        }
    },
    mounted(){
        console.log('ChatView component is created')

        this.userId = localStorage.getItem('userId');
        if(this.userId == null) {
            this.$router.push({
              name: "Login", 
            });
        }

        const token = localStorage.getItem('token');

        if(token == null || token == "") {
            this.$router.push({
              name: "Login", 
            });
        }

        console.log("user ID - " + this.userId)
        this.token = token 

        this.createWebSocket()
        this.fetHistory(this.token, this.chatId)
        this.fetchMembers(this.chatId, this.userId, this.token)
    }

}

</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  /* height: 80%; */
  max-width: 600px;
  margin: auto;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 10px;
}

.messages {
  flex-grow: 1;
  overflow-y: auto;
  margin-bottom: 10px;
  padding-right: 5px;
}

.message {
  margin-bottom: 5px;
}

.message-other {
    display: flex;
    justify-content: left;

}

.message-myself {
    display: flex;
    justify-content: right
    
}

.input-area {
  display: flex;
  gap: 10px;
}

input[type="text"] {
  flex-grow: 1;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #aaa;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
}


</style>