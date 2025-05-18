<template>

    <div class="notifications-wrapper" @click="toggle">
        <div class="notification-icon">
            <img src="../assets/heart.jpg" alt="Notifications" class="bell-icon" />
            <span v-if="notifications.length" class="badge">{{ notifications.length }}</span>
        </div>

        <div v-if="isToggle" class="popup">
            <p v-if="notifications.length === 0" class="empty">No notifications</p>
            <ul v-else>
                <li v-for="note in notifications" :key="note.id">
                    You got a new match! Check your matches!
                </li>
            </ul>
        </div>

    </div>

</template>

<script>

export default {
  name: 'NotificationsPanel',
  props: {
    token: String
  },
  components: {
    
  },
  data: () => {
    return {
        isToggle: false, 
        notifications: [],
    }
  },
  methods: {
    toggle() {
        console.log("Click!")
        this.isToggle = !this.isToggle;
    },
    fetHistory(token){
        fetch(`http://localhost:8004/api/notification/history`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            })
            .then(response => response.json())
            .then(data => {
                this.notifications = data
            })
            .catch(error => {
                console.error('Error fetching messages:', error);
            }); 
    }
  },
  mounted(){
        console.log('Notifications panel component is created')
        this.fetHistory(this.token)
    }
}
</script>

<style scoped>

.notification-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.notification-icon {
  position: relative;
}

.bell-icon {
  width: 24px;
  height: 24px;
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: red;
  color: white;
  font-size: 12px;
  border-radius: 50%;
  padding: 2px 6px;
}

.empty {
  padding: 12px;
  text-align: center;
  color: #888;
}


.popup {
  position: absolute;
  top: 30px;
  right: 0;
  width: 220px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 999;
  padding: 10px;
}

.popup ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  color: #888;
}

.popup li {
  padding: 8px 5px;
  border-bottom: 1px solid #eee;
  color: #888;
}

.popup li:last-child {
  border-bottom: none;
  color: #888;
}
</style>