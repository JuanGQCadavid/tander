
<template>
    <div class="header">
      <img src="../assets/heart.jpg" alt="Heart" class="heart-icon" />
      
      <div class="text-content">
        <h3 v-if="users.length !== 0">You and {{ users[0]["email"] }}</h3>
        <p>Dating since: {{ chatSince }}</p>
        <p>Id: {{ chatId }}</p>
      </div>

      <div class="ctaEnter">
        <button @click="enterChat" class="addPost">Enter chat</button>
      </div>
    </div>
</template>

<script>


export default {
    name: "ChatList",
    components: {

    },
    props: {
        chatId: {type: String, required: true},
        chatWith: {type: String, required: true},
        chatSince: {type: String, required: true},
        isActive: {type: String, required: true},
        users: {type: Array, required: true},
    },

    data: () => {
    },
    methods: {
        enterChat(){
            this.$router.push("/chat/"+ this.chatId);
        }
    },
    created(){
        console.log('The component is created')
    }
}

</script>

<style scoped>
.chatView {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: center;
}

.heart-icon {
  width: 100px; 
  height: 100px;
  margin-right: 10px;
}

.text-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* aligns children (h3, p) to the left */
  text-align: left;        /* ensures text inside each element is also left-aligned */
}

.text-content h3 {
  margin: 0; 
  font-size: 18px;
}

.text-content p {
  margin: 5px 0; 
  font-size: 14px;
}

.ctaEnter {
    margin-left: 10px;
    /* background-color: #007bff; 
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px; */
}


</style>
