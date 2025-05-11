<template>
  <div class="search-page">
    <div v-if="showFilters" class="filters">
      <h2>Search Filters</h2>
      <div class="first-row">
        <div class="filter-item">
          <label for="gender">Gender preference:</label>
          <select v-model="gender" id="gender">
            <option value="">Any</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
        <div class="filter-item">
          <label for="minAge">Minimum Age:</label>
          <input type="number" v-model="minAge" id="minAge" @input="validateAge('minAge')" />
        </div>
        <div class="filter-item">
          <label for="maxAge">Maximum Age:</label>
          <input type="number" v-model="maxAge" id="maxAge" @input="validateAge('maxAge')" />
        </div>
      </div>
      <div class="first-row">
        <div class="filter-item">
          <label for="longitude">Longitude:</label>
          <input type="number" v-model="longitude" id="longitude" />
        </div>
        <div class="filter-item">
          <label for="latitude">Latitude:</label>
          <input type="number" v-model="latitude" id="latitude" />
        </div>
        <div class="filter-item">
          <label for="maxDistance">Maximum Distance:</label>
          <input type="number" v-model="maxDistance" id="maxDistance" />
        </div>
      </div>

      <div class="filter-item">
        <label for="interests">Interests:</label>
        <input type="text" v-model="newInterest" @keyup.enter="addInterest" placeholder="Add interest" />
        <ul class="interests-list">
          <li v-for="(interest, index) in interests" :key="index">
            {{ interest }} <button @click="removeInterest(index)">Remove</button>
          </li>
        </ul>
      </div>
      <div class="buttons">
        <button @click="confirmSearch">Search</button>
        <button @click="toggleFilters">Close Filters</button>
      </div>
    </div>
    <div v-else class="buttons">
      <button @click="toggleFilters">Filters</button>
    </div>
    <div v-if="profiles.length" class="profile-stack">
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
        <p>No profiles found...</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

const searchUrl = 'http://localhost:8008/api/search';
const matchUrl = 'http://localhost:8007/api/matches'

export default {
  data() {
    return {
      showFilters: true,
      minAge: '18',
      maxAge: '99',
      maxDistance: '',
      longitude: '',
      latitude: '',
      gender: '',
      newInterest: '',
      interests: [],
      profiles: [],
      currentIndex: 0,
      currentMatch: null
    };
  },
  computed: {
    currentProfile() {
      if (this.profiles.length > 0) {
        return this.profiles[this.currentIndex];
      } else {
        return {}
      }
    }
  },
  methods: {
    getUserIdFromAuth() {
      const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
      return user.id;
    },
    toggleFilters() {
      this.showFilters = !this.showFilters;
    },
    addInterest() {
      if (this.newInterest) {
        this.interests.push(this.newInterest);
        this.newInterest = '';
      }
    },
    removeInterest(index) {
      this.interests.splice(index, 1);
    },
    validateAge(fieldName) {
      if (this[fieldName] < 0) {
        this[fieldName] = 0;
      } else if (this[fieldName] > 99 || this[fieldName] < 0) {
        this[fieldName] = 0
      }
    },
    confirmSearch() {
      const ownId = this.getUserIdFromAuth();
      const searchFilters = {
        genderPreference: this.gender || null,
        interests: this.interests || [],
        locationPreference: {
          longitude: this.longitude,
          latitude: this.latitude
        },
        maxDistance: this.maxDistance || null,
        minAge: parseInt(this.minAge) || 0,
        maxAge: parseInt(this.maxAge) || 99
      }
      this.currentIndex = 0;
      axios.post(searchUrl + '/custom', searchFilters)
          .then(response => {
            this.profiles = response.data.filter(profile => profile.userId !== ownId)
          })
          .catch(error => { console.error(error) });
    },
    fetchMatch(profileId) {
      axios.get(matchUrl + `/getMatch?profileId1=${this.getUserIdFromAuth()}&profileId2=${profileId}`)
          .then(response => {
            this.currentMatch = response.data;
          })
          .catch(error => { console.error(error); });
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
    createMatch(status) {
      const newMatch = {
        profileId1: this.getUserIdFromAuth(),
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
        profileStatus1: this.getUserIdFromAuth() === this.currentMatch.profileId1 ? status : this.currentMatch.profileStatus1,
        profileStatus2: this.getUserIdFromAuth() === this.currentMatch.profileId2 ? status : this.currentMatch.profileStatus2
      }

      axios.put(matchUrl + `/${this.currentMatch.matchId}`, updatedMatch)
          .then(() => null)
          .catch(error => { console.error(error); });
    },
    nextProfile() {
      this.currentIndex++;
      if (this.currentIndex < this.profiles.length) {
        this.fetchMatch(this.profiles[this.currentIndex].userId)
      }
    },
  }
};
</script>

<style scoped>
.search-page {
   font-family: Arial, sans-serif;
   padding: 20px;
}

.filters {
   background-color: #f9f9f9;
   padding: 20px;
   border-radius: 8px;
   box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-item {
   margin: 10px;
}

.filter-item label {
   display: block;
   margin-bottom: 5px;
}

.filter-item input,
.filter-item select {
   width: 100%;
   padding: 8px;
   border: 1px solid #ccc;
   border-radius: 4px;
}

.interests-list {
   list-style-type: none;
   padding: 0;
}

.interests-list li {
   display: flex;
   justify-content: space-between;
   align-items: center;
   background-color: #e9e9e9;
   padding: 8px;
   border-radius: 4px;
   margin-bottom: 5px;
}

.buttons {
   display: flex;
   gap: 10px;
}

.buttons button {
   padding: 10px 15px;
   border: none;
   border-radius: 4px;
   cursor: pointer;
}

.buttons button:first-child {
   background-color: #007bff;
   color: white;
}

.buttons button:last-child {
   background-color: #6c757d;
   color: white;
}

.results ul {
   list-style-type: none;
   padding: 0;
}

.results li {
   background-color: #f1f1f1;
   padding: 10px;
   border-radius: 4px;
   margin-bottom: 5px;
}

.first-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

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