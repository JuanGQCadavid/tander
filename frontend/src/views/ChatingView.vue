<template>
    <div class="about">
      <h1>This is the chat space for chat  {{ chatId }}</h1>
    </div>
    <div class="chat-container">
        <div class="messages">
        <div v-for="msg in messages" :key="msg.id" class="message">
            <strong>{{ msg.senderId }}:</strong> {{ msg.textContent }}
            <i>{{ msg.dateOfCreation }}</i>
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
            userId: '1',
        }
    },
    props: {
        chatId: {type: String, required: true},
    },
    methods: {
        fetHistory(userId, chatId) {
            fetch(`http://localhost:8009/api/chat/history/${chatId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'userId': userId,
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
                        userId: this.userId,
                        chatId: this.chatId
                    }
                }
            ));
        }
    },
    mounted(){
        console.log('ChatView component is created')
        this.createWebSocket()
        this.fetHistory(this.userId, this.chatId)
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