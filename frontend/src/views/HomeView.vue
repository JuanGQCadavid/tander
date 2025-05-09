
<template>
   <div class="profile-stack">
     <div v-if="currentProfile" class="profile-card">
       <img :src="getProfileImage(currentProfile)" alt="Profile Picture" class="profile-image" />
       <h2>{{ currentProfile.name }}</h2>
       <p>{{ calculateAge(currentProfile.dateOfBirth) }}, {{ currentProfile.gender }}</p>
       <p v-if="currentProfile.preferences.interests && currentProfile.preferences.interests.length > 0">
         Interests: {{ currentProfile.preferences.interests.join(', ') }}
       </p>
       <p>{{ currentProfile.bio }}</p>
       <div class="buttons">
         <button @click="dislike">DISLIKE</button>
         <button @click="like">LIKE</button>
       </div>
     </div>
     <div v-else class="no-profiles">
       <p>No new profiles found, try expanding your filters</p>
     </div>
    </div>
</template>

<script>
import axios from 'axios';

const searchUrl = 'http://localhost:8008/api/search';
const matchUrl = 'http://localhost:8007/api/matches'
// TODO: get user id from logged in user
const currentUserId = 2;

export default {
  data() {
    return {
      profiles: [],
      currentIndex: 0,
      currentMatch: null
    };
  },
  computed: {
    currentProfile() {
      return this.profiles[this.currentIndex];
    },
  },
  methods: {
    fetchProfiles() {
      axios.get(searchUrl + `?userId=${currentUserId}`)
          .then(response => {
            this.profiles = response.data;
            if (this.profiles.length > 0) {
              this.fetchMatch(this.currentProfile.userId);
            }
          })
          .catch(error => { console.error(error); })
    },

    calculateAge(dateOfBirth) {
      const birthDate = new Date(dateOfBirth);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDifference = today.getMonth() - birthDate.getMonth();
      if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    },

    getProfileImage(profile) {
      if (profile.image) {
        return profile.image;
      } else if (profile.gender === 'FEMALE') {
        return '/assets/womanDefaultPicture.jpg';
      } else {
        return '/assets/maleDefaultPicture.jpg';
      }
    },

    fetchMatch(profileId) {
      axios.get(matchUrl + `/getMatch?profileId1=${currentUserId}&profileId2=${profileId}`)
          .then(response => {
            this.currentMatch = response.data;
          })
          .catch(error => { console.error(error); });
    },

    createMatch(status) {
      const newMatch = {
        profileId1: currentUserId,
        profileId2: this.currentProfile.userId,
        profileStatus1: status
      }
      axios.post(matchUrl + '/create', newMatch)
          .then(() => null)
          .catch(error => { console.error(error); });
    },

    updateMatch(status) {
      const updatedMatch = {
        ...this.currentMatch,
        profileStatus1: currentUserId === this.currentMatch.profileId1 ? status : this.currentMatch.profileStatus1,
        profileStatus2: currentUserId === this.currentMatch.profileId2 ? status : this.currentMatch.profileStatus2
      }

      axios.put(matchUrl + `/${this.currentMatch.matchId}`, updatedMatch)
          .then(() => null)
          .catch(error => { console.error(error); });
    },

    like() {
      if (this.currentMatch == null) {
        this.createMatch('LIKE');
      } else {
        this.updateMatch('LIKE');
      }
      this.nextProfile();
    },

    dislike() {
      if (this.currentMatch == null) {
        this.createMatch('DISLIKE');
      } else {
        this.updateMatch('DISLIKE');
      }
      this.nextProfile();
    },

    nextProfile() {
      this.currentIndex++;
      if (this.currentIndex < this.profiles.length) {
        this.fetchMatch(this.profiles[this.currentIndex].userId)
      }
    },
  },
  created() {
    this.fetchProfiles();
  }
};
</script>

<style scoped>
.profile-stack {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-card {
  border: 1px solid #ccc;
  padding: 20px;
  margin: 10px;
  width: 300px;
  text-align: center;
}

.profile-image {
  width: 100%;
  height: auto;
  border-radius: 50%;
}

.buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

button {
  padding: 10px 20px;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #ddd;
}

.no-profiles {
  font-weight: bold;
  font-size: 20px;
  text-align: center;
  margin-top: 20px;
}

</style>
