<template>
    <div class="profile-stack">
        <div v-if="currentProfile" class="profile-card">
            <!-- <img :src="profileImageUrl" alt="Profile Picture" class="profile-image" /> -->

            <!-- Profile Image -->
            <div class="profile-image-section">
                <div class="profile-image-container">
                    <img v-if="profileImageUrl" :src="profileImageUrl" alt="Profile Image" class="profile-image" />
                    <div v-else class="profile-image-placeholder">
                        <span>{{ currentProfile.name ? currentProfile.name.charAt(0).toUpperCase() : 'U' }}</span>
                    </div>
                </div>
            </div>

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
const fileServiceUrl = 'http://localhost:8010/api/files';

export default {
    data() {
        return {
            profiles: [],
            currentIndex: 0,
            currentMatch: null,
            profileImageUrl: null,
        };
    },
    computed: {
        currentProfile() {
            return this.profiles[this.currentIndex];
        },
    },
    methods: {
        getUserIdFromAuth() {
            const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
            return user.id;
        },
        fetchProfiles() {
            axios.get(searchUrl + `?userId=${this.getUserIdFromAuth()}`)
                .then(response => {
                    this.profiles = response.data;
                    if (this.profiles.length > 0) {
                        this.fetchMatch(this.currentProfile.userId);
                        this.fetchProfileImage(this.currentProfile.imageId);
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

        async fetchProfileImage(imageId) {
            try {
                const response = await axios.get(`${fileServiceUrl}/file/${imageId}`);
                this.profileImageUrl = response.data.fileUrl;
            } catch (error) {
                console.error('Error fetching profile image:', error);
                this.profileImageUrl = null;
            }
        },

        fetchMatch(profileId) {
            axios.get(matchUrl + `/getMatch?profileId1=${this.getUserIdFromAuth()}&profileId2=${profileId}`)
                .then(response => {
                    this.currentMatch = response.data;
                })
                .catch(error => { console.error(error); });
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
        const userId = this.getUserIdFromAuth();
        if (!userId) {
            this.$router.push(`/login`)
        }
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
    width: 500px;
    text-align: center;
}

/* Profile Image Styles */
.profile-image-section {
    display: flex;
    justify-content: center;
    margin-bottom: 40px;
}

.profile-image-container {
    width: 400px;
    height: 400px;
    overflow: hidden;
    border: 3px solid #4a90e2;
    position: relative;
    box-shadow: 0 4px 12px rgba(74, 144, 226, 0.2);
}

.profile-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
}

.profile-image-placeholder {
    width: 100%;
    height: 100%;
    background-color: #e8f0fe;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 86px;
    font-weight: bold;
    color: #4a90e2;
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
