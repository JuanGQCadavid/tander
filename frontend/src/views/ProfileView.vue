<template>
    <div class="profile-container">
      <div v-if="loading" class="loading-container">
        <div class="loader"></div>
        <p>Loading profile...</p>
      </div>
      
      <div v-else-if="error" class="error-container">
        <h2>Error Loading Profile</h2>
        <p>{{ error }}</p>
        <button @click="fetchProfile" class="retry-button">Retry</button>
      </div>
      
      <div v-else class="profile-content">
        <div class="profile-header">
          <h1>{{ profile.name }}</h1>
          <div class="profile-actions">
            <button @click="navigateToEdit" class="edit-button">Edit Profile</button>
          </div>
        </div>
        
        <div class="profile-details">
          <div class="profile-section">
            <h2>Personal Information</h2>
            <div class="detail-item">
              <span class="label">Name:</span>
              <span class="value">{{ profile.name }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Age:</span>
              <span class="value">{{ calculateAge }} years</span>
            </div>
            <div class="detail-item">
              <span class="label">Gender:</span>
              <span class="value">{{ formatGender(profile.gender) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Bio:</span>
              <p class="bio-text">{{ profile.bio || 'No bio provided' }}</p>
            </div>
          </div>
          
          <div class="profile-section">
            <h2>Location</h2>
            <div v-if="profile.location" class="detail-item">
              <span class="label">Coordinates:</span>
              <span class="value">
                {{ profile.location.latitude }}° N, {{ profile.location.longitude }}° E
              </span>
            </div>
            <div v-else class="detail-item">
              <span class="value">No location set</span>
            </div>
            
            <div v-if="profile.location" class="location-map">
              <div class="map-placeholder">
                <div class="map-pin"></div>
                <p>Map View</p>
              </div>
            </div>
          </div>
          
          <div class="profile-section">
            <h2>Preferences</h2>
            <div v-if="profile.preferences">
              <div class="detail-item">
                <span class="label">Interested in:</span>
                <span class="value">{{ formatGender(profile.preferences.gender) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Age Range:</span>
                <span class="value">{{ profile.preferences.minAge }} - {{ profile.preferences.maxAge }} years</span>
              </div>
              <div class="detail-item">
                <span class="label">Maximum Distance:</span>
                <span class="value">{{ profile.preferences.maxDistance }} km</span>
              </div>
              <div class="detail-item">
                <span class="label">Interests:</span>
                <div class="interests-list">
                  <span v-for="(interest, index) in profile.preferences.interests" :key="index" class="interest-tag">
                    {{ interest }}
                  </span>
                  <span v-if="!profile.preferences.interests || profile.preferences.interests.length === 0" class="no-interests">
                    No interests specified
                  </span>
                </div>
              </div>
            </div>
            <div v-else class="detail-item">
              <span class="value">No preferences set</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    name: 'ProfileView',
    data() {
      return {
        profile: {
          userId: null,
          name: '',
          dateOfBirth: '',
          gender: null,
          preferences: {
            gender: null,
            interests: [],
            location: null,
            maxDistance: 50,
            minAge: 18,
            maxAge: 50
          },
          location: null,
          bio: ''
        },
        loading: true,
        error: null
      };
    },
    computed: {
      calculateAge() {
        if (!this.profile.dateOfBirth) return 'Unknown';
        
        const birthDate = new Date(this.profile.dateOfBirth);
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();
        
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          age--;
        }
        
        return age;
      },
      userId() {
        return this.$route.params.userId || this.getUserIdFromAuth();
      }
    },
    created() {
      this.fetchProfile();
    },
    methods: {
      getUserIdFromAuth() {
        const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
        return user.userId;
      },
      
      async fetchProfile() {
        this.loading = true;
        this.error = null;
        
        try {
          const response = await axios.get(`http://localhost:8006/api/profiles/${this.userId}`);
          this.profile = response.data;
        } catch (error) {
          console.error('Error fetching profile:', error);
          this.error = error.response?.data?.message || 'Failed to load profile. Please try again later.';
        } finally {
          this.loading = false;
        }
      },
      
      formatGender(gender) {
        if (!gender) return 'Not specified';
        
        return gender.charAt(0).toUpperCase() + gender.slice(1).toLowerCase();
      },
      
      navigateToEdit() {
        this.$router.push(`/profile/${this.userId}/edit`);
      }
    }
  }
  </script>
  
  <style scoped>
  .profile-container {
    max-width: 800px;
    margin: 30px auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  .loading-container, .error-container {
    text-align: center;
    padding: 40px 0;
  }
  
  .loader {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #4a90e2;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  .retry-button {
    background: #4a90e2;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 15px;
  }
  
  .profile-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
  }
  
  .profile-header h1 {
    margin: 0;
    color: #333;
  }
  
  .edit-button {
    background: #4a90e2;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 4px;
    font-weight: 500;
    cursor: pointer;
  }
  
  .edit-button:hover {
    background: #3a80d2;
  }
  
  .profile-section {
    margin-bottom: 30px;
    padding: 20px;
    background: #f9f9f9;
    border-radius: 6px;
  }
  
  .profile-section h2 {
    margin-top: 0;
    margin-bottom: 15px;
    color: #333;
    font-size: 20px;
  }
  
  .detail-item {
    margin-bottom: 12px;
  }
  
  .label {
    font-weight: 600;
    color: #555;
    margin-right: 8px;
  }
  
  .value {
    color: #333;
  }
  
  .bio-text {
    white-space: pre-line;
    margin: 10px 0 0;
    line-height: 1.5;
  }
  
  .interests-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 8px;
  }
  
  .interest-tag {
    background: #e8f0fe;
    color: #4a90e2;
    padding: 5px 10px;
    border-radius: 15px;
    font-size: 14px;
  }
  
  .no-interests {
    color: #888;
    font-style: italic;
  }
  
  .location-map {
    margin-top: 15px;
    height: 200px;
    border-radius: 6px;
    overflow: hidden;
  }
  
  .map-placeholder {
    height: 100%;
    background-color: #e8f0fe;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #4a90e2;
    position: relative;
  }
  
  .map-pin {
    width: 20px;
    height: 20px;
    background-color: #4a90e2;
    border-radius: 50% 50% 50% 0;
    transform: rotate(-45deg);
    margin-bottom: 10px;
  }
  
  .map-pin:after {
    content: '';
    width: 10px;
    height: 10px;
    background-color: white;
    position: absolute;
    border-radius: 50%;
    top: 5px;
    left: 5px;
  }
  
  @media (max-width: 600px) {
    .profile-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;
    }
    
    .profile-actions {
      width: 100%;
    }
    
    .edit-button {
      width: 100%;
    }
  }
  </style>