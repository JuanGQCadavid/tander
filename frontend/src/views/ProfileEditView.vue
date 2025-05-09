<template>
    <div class="profile-edit-container">
        <div v-if="!isOwnProfile" class="error-container">
            <h2>No permission to edit this profile</h2>
        </div>

        <div v-else-if="loading" class="loading-container">
            <div class="loader"></div>
            <p>Loading profile data...</p>
        </div>

        <div v-else-if="error && !isCreatingNew" class="error-container">
            <h2>Error Loading Profile</h2>
            <p>{{ error }}</p>
            <div class="error-actions">
                <button @click="fetchProfile" class="retry-button">Retry</button>
                <button @click="createNewProfile" class="create-button">Create New Profile</button>
            </div>
        </div>

        <div v-else class="edit-content">
            <h1>{{ isCreatingNew ? 'Create Profile' : 'Edit Profile' }}</h1>

            <!-- Profile Image Section -->
            <div class="profile-image-section">
                <div class="profile-image-container">
                    <img v-if="profileImageUrl" :src="profileImageUrl" alt="Profile Image" class="profile-image" />
                    <div v-else class="profile-image-placeholder">
                        <span>{{ form.name ? form.name.charAt(0).toUpperCase() : 'U' }}</span>
                    </div>
                </div>
                <div class="profile-image-actions">
                    <label for="profileImageUpload" class="upload-button">
                        {{ profileImageUrl ? 'Change Image' : 'Upload Image' }}
                        <input 
                            type="file" 
                            id="profileImageUpload" 
                            accept="image/*" 
                            @change="handleImageUpload" 
                            class="hidden-input" 
                        />
                    </label>
                    <label 
                        v-if="profileImageUrl" 
                        @click="removeProfileImage" 
                        class="remove-image-button"
                    >
                        Remove Image
                    </label>
                </div>
                <div v-if="uploadProgress > 0 && uploadProgress < 100" class="upload-progress">
                    <div class="progress-bar">
                        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                    </div>
                    <span>{{ uploadProgress }}%</span>
                </div>
                <span v-if="errors.image" class="error-message">{{ errors.image }}</span>
            </div>

            <div class="tabs">
                <button @click="activeTab = 'personal'" :class="['tab-button', { active: activeTab === 'personal' }]">
                    Personal Info
                </button>
                <button @click="activeTab = 'preferences'"
                    :class="['tab-button', { active: activeTab === 'preferences' }]">
                    Preferences
                </button>
                <button @click="activeTab = 'location'" :class="['tab-button', { active: activeTab === 'location' }]">
                    Location
                </button>
            </div>

            <!-- Personal Information Form -->
            <form v-if="activeTab === 'personal'" @submit.prevent="savePersonalInfo" class="edit-form">
                <div class="form-group">
                    <label for="name">Name</label>
                    <input type="text" id="name" v-model="form.name" required placeholder="Your full name" />
                    <span v-if="errors.name" class="error-message">{{ errors.name }}</span>
                </div>

                <div class="form-group">
                    <label for="dateOfBirth">Date of Birth</label>
                    <input type="date" id="dateOfBirth" v-model="dateOfBirthInput" required />
                    <span v-if="errors.dateOfBirth" class="error-message">{{ errors.dateOfBirth }}</span>
                </div>

                <div class="form-group">
                    <label>Gender</label>
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" v-model="form.gender" value="MALE" />
                            <span>Male</span>
                        </label>
                        <label class="radio-label">
                            <input type="radio" v-model="form.gender" value="FEMALE" />
                            <span>Female</span>
                        </label>
                        <label class="radio-label">
                            <input type="radio" v-model="form.gender" value="OTHER" />
                            <span>Other</span>
                        </label>
                    </div>
                    <span v-if="errors.gender" class="error-message">{{ errors.gender }}</span>
                </div>

                <div class="form-group">
                    <label for="bio">Bio</label>
                    <textarea id="bio" v-model="form.bio" rows="5"
                        placeholder="Write something about yourself..."></textarea>
                    <span v-if="errors.bio" class="error-message">{{ errors.bio }}</span>
                </div>

                <div class="form-actions">
                    <button type="button" @click="cancelEdit" class="cancel-button">Cancel</button>
                    <button type="submit" class="save-button" :disabled="saving">
                        {{ saving ? 'Saving...' : (isCreatingNew ? 'Create Profile' : 'Save Changes') }}
                    </button>
                </div>
            </form>

            <!-- Preferences Form -->
            <form v-if="activeTab === 'preferences'" @submit.prevent="savePreferences" class="edit-form">
                <div class="form-group">
                    <label>Interested in</label>
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" v-model="form.preferences.genderPreference" value="MALE" />
                            <span>Male</span>
                        </label>
                        <label class="radio-label">
                            <input type="radio" v-model="form.preferences.genderPreference" value="FEMALE" />
                            <span>Female</span>
                        </label>
                        <label class="radio-label">
                            <input type="radio" v-model="form.preferences.genderPreference" value="OTHER" />
                            <span>Other</span>
                        </label>
                    </div>
                </div>

                <div class="form-group">
                    <label>Age Range</label>
                    <div class="age-range">
                        <div class="age-input">
                            <label for="minAge">Min Age</label>
                            <input type="number" id="minAge" v-model.number="form.preferences.minAge" min="18"
                                max="120" />
                        </div>
                        <div class="age-input">
                            <label for="maxAge">Max Age</label>
                            <input type="number" id="maxAge" v-model.number="form.preferences.maxAge" min="18"
                                max="120" />
                        </div>
                    </div>
                    <span v-if="errors.minAge" class="error-message">{{ errors.minAge }}</span>
                    <span v-if="errors.maxAge" class="error-message">{{ errors.maxAge }}</span>
                </div>

                <div class="form-group">
                    <label for="maxDistance">Maximum Distance (km)</label>
                    <input type="range" id="maxDistance" v-model.number="form.preferences.maxDistance" min="1" max="200"
                        class="range-slider" />
                    <div class="range-value">{{ form.preferences.maxDistance }} km</div>
                    <span v-if="errors.maxDistance" class="error-message">{{ errors.maxDistance }}</span>
                </div>

                <div class="form-group">
                    <label for="interests">Interests</label>
                    <div class="interests-input">
                        <input type="text" id="interestInput" v-model="newInterest"
                            placeholder="Add an interest and press Enter" @keyup.enter="addInterest" />
                        <button type="button" @click="addInterest" class="add-interest-button"
                            :disabled="!newInterest.trim()">
                            Add
                        </button>
                    </div>
                    <div class="interests-list">
                        <div v-for="(interest, index) in form.preferences.interests" :key="index" class="interest-tag">
                            {{ interest }}
                            <button type="button" class="remove-interest" @click="removeInterest(index)">
                                ×
                            </button>
                        </div>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="button" @click="cancelEdit" class="cancel-button">Cancel</button>
                    <button type="submit" class="save-button" :disabled="saving">
                        {{ saving ? 'Saving...' : (isCreatingNew ? 'Create Profile' : 'Save Changes') }}
                    </button>
                </div>
            </form>

            <!-- Location Form -->
            <form v-if="activeTab === 'location'" @submit.prevent="saveLocation" class="edit-form">
                <div class="form-group">
                    <label for="latitude">Latitude</label>
                    <input type="number" id="latitude" v-model.number="form.location.latitude" step="0.000001" min="-90"
                        max="90" required />
                    <span v-if="errors.latitude" class="error-message">{{ errors.latitude }}</span>
                </div>

                <div class="form-group">
                    <label for="longitude">Longitude</label>
                    <input type="number" id="longitude" v-model.number="form.location.longitude" step="0.000001"
                        min="-180" max="180" required />
                    <span v-if="errors.longitude" class="error-message">{{ errors.longitude }}</span>
                </div>

                <div class="location-map">
                    <div class="map-placeholder">
                        <div class="map-pin"></div>
                    </div>
                </div>

                <div class="form-group">
                    <button type="button" @click="getCurrentLocation" class="current-location-button">
                        Use Current Location
                    </button>
                </div>

                <div class="form-actions">
                    <button type="button" @click="cancelEdit" class="cancel-button">Cancel</button>
                    <button type="submit" class="save-button" :disabled="saving">
                        {{ saving ? 'Saving...' : (isCreatingNew ? 'Create Profile' : 'Save Changes') }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'ProfileEditView',
    data() {
        return {
            activeTab: 'personal',
            isCreatingNew: false,
            form: {
                userId: null,
                name: '',
                dateOfBirth: '',
                gender: null,
                bio: '',
                imageId: null,
                preferences: {
                    gender: null,
                    interests: [],
                    maxDistance: 50,
                    minAge: 18,
                    maxAge: 50
                },
                location: {
                    latitude: 0,
                    longitude: 0
                }
            },
            originalProfile: {},
            dateOfBirthInput: '',
            newInterest: '',
            loading: true,
            saving: false,
            error: null,
            errors: {},
            profileImageUrl: null,
            uploadProgress: 0,
            fileUploadUrl: 'http://localhost:8010/api/files',
            fileServiceUrl: 'http://localhost:8010/api/files'
        };
    },
    computed: {
        userId() {
            return this.$route.params.userId || this.getUserIdFromAuth();
        },
        isOwnProfile() {
            return this.$route.params.userId == this.getUserIdFromAuth();
        }
    },
    watch: {
        dateOfBirthInput(newValue) {
            if (newValue) {
                this.form.dateOfBirth = new Date(newValue).toISOString();
            }
        }
    },
    created() {
        this.fetchProfile();
    },
    methods: {
        getUserIdFromAuth() {
            const user = JSON.parse(localStorage.getItem('user') || sessionStorage.getItem('user') || '{}');
            return user.id;
        },

        async fetchProfile() {
            this.loading = true;
            this.error = null;
            this.isCreatingNew = false;

            try {
                const response = await axios.get(`http://localhost:8006/api/profiles/${this.userId}`);
                const profileData = response.data;

                this.form = { ...profileData };

                // Make a deep copy for comparison later
                this.originalProfile = JSON.parse(JSON.stringify(profileData));

                // Convert dateOfBirth to date input format
                if (this.form.dateOfBirth) {
                    const date = new Date(this.form.dateOfBirth);
                    this.dateOfBirthInput = date.toISOString().split('T')[0];
                }

                // Initialize location if not present
                if (!this.form.location) {
                    this.form.location = { latitude: 0, longitude: 0 };
                }

                // Initialize preferences if not present
                if (!this.form.preferences) {
                    this.form.preferences = {
                        gender: null,
                        interests: [],
                        maxDistance: 50,
                        minAge: 18,
                        maxAge: 50
                    };
                }

                // Initialize interests array if not present
                if (!this.form.preferences.interests) {
                    this.form.preferences.interests = [];
                }

                // Fetch image if imageId exists
                if (this.form.imageId) {
                    this.fetchProfileImage(this.form.imageId);
                }

            } catch (error) {
                console.error('Error fetching profile:', error);
                if (error.response && error.response.status === 404) {
                    // Profile not found, offer to create a new one
                    this.error = 'No profile found for this user. Would you like to create a new profile?';
                } else {
                    this.error = error.response?.data?.message || 'Failed to load profile. Please try again later.';
                }
            } finally {
                this.loading = false;
            }
        },

        async fetchProfileImage(imageId) {
            try {
                const response = await axios.get(`${this.fileServiceUrl}/file/${imageId}`);
                this.profileImageUrl = response.data.fileUrl;
            } catch (error) {
                console.error('Error fetching profile image:', error);
                this.profileImageUrl = null;
            }
        },

        async handleImageUpload(event) {
            const file = event.target.files[0];
            if (!file) return;

            // Reset file input
            event.target.value = '';

            // Validate file
            if (!file.type.startsWith('image/')) {
                this.errors = { ...this.errors, image: 'Please select an image file' };
                return;
            }

            // File size validation (5MB max)
            if (file.size > 5 * 1024 * 1024) {
                this.errors = { ...this.errors, image: 'Image size should not exceed 5MB' };
                return;
            }

            this.errors = { ...this.errors, image: null };
            this.uploadProgress = 0;

            try {
                const formData = new FormData();
                formData.append('file', file);

                const response = await axios.post(this.fileUploadUrl, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    },
                    onUploadProgress: progressEvent => {
                        this.uploadProgress = Math.round(
                            (progressEvent.loaded * 100) / progressEvent.total
                        );
                    }
                });

                // Update the profile with the new image ID
                this.form.imageId = response.data.id;
                this.profileImageUrl = response.data.fileUrl;
                
                // Automatically save the profile with the new image ID
                await this.savePersonalInfo();
                
            } catch (error) {
                console.error('Error uploading image:', error);
                this.errors = { ...this.errors, image: 'Failed to upload image. Please try again.' };
                this.uploadProgress = 0;
            }
        },

        removeProfileImage() {
            this.profileImageUrl = null;
            this.form.imageId = null;
            this.uploadProgress = 0;
        },

        createNewProfile() {
            this.isCreatingNew = true;
            this.error = null;

            // Initialize a new profile with default values
            this.form = {
                userId: this.userId,
                name: '',
                dateOfBirth: '',
                gender: null,
                bio: '',
                imageId: null,
                preferences: {
                    gender: 'ANY',
                    interests: [],
                    maxDistance: 50,
                    minAge: 18,
                    maxAge: 50
                },
                location: {
                    latitude: 0,
                    longitude: 0
                }
            };

            this.dateOfBirthInput = '';
            this.newInterest = '';
            this.errors = {};
            this.profileImageUrl = null;
            this.uploadProgress = 0;
        },

        validatePersonalInfo() {
            const errors = {};

            if (!this.form.name || this.form.name.trim() === '') {
                errors.name = 'Name is required';
            }

            if (!this.form.dateOfBirth) {
                errors.dateOfBirth = 'Date of birth is required';
            } else {
                const birthDate = new Date(this.form.dateOfBirth);
                const today = new Date();
                let age = today.getFullYear() - birthDate.getFullYear();
                const monthDiff = today.getMonth() - birthDate.getMonth();

                if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                    age--;
                }

                if (age < 18) {
                    errors.dateOfBirth = 'You must be at least 18 years old';
                }
            }

            if (!this.form.gender) {
                errors.gender = 'Gender is required';
            }

            this.errors = errors;
            return Object.keys(errors).length === 0;
        },

        validatePreferences() {
            const errors = {};
            const { minAge, maxAge, maxDistance } = this.form.preferences;

            if (minAge < 18) {
                errors.minAge = 'Minimum age must be at least 18';
            }

            if (maxAge > 120) {
                errors.maxAge = 'Maximum age cannot exceed 120';
            }

            if (minAge > maxAge) {
                errors.minAge = 'Minimum age cannot be greater than maximum age';
            }

            if (maxDistance < 1) {
                errors.maxDistance = 'Maximum distance must be at least 1 km';
            }

            if (maxDistance > 200) {
                errors.maxDistance = 'Maximum distance cannot exceed 200 km';
            }

            this.errors = errors;
            return Object.keys(errors).length === 0;
        },

        validateLocation() {
            const errors = {};
            const { latitude, longitude } = this.form.location;

            if (latitude === null || latitude === undefined) {
                errors.latitude = 'Latitude is required';
            } else if (latitude < -90 || latitude > 90) {
                errors.latitude = 'Latitude must be between -90 and 90';
            }

            if (longitude === null || longitude === undefined) {
                errors.longitude = 'Longitude is required';
            } else if (longitude < -180 || longitude > 180) {
                errors.longitude = 'Longitude must be between -180 and 180';
            }

            this.errors = errors;
            return Object.keys(errors).length === 0;
        },

        async savePersonalInfo() {
            if (!this.validatePersonalInfo()) return;

            this.saving = true;

            try {
                let response;

                if (this.isCreatingNew) {
                    // Create new profile
                    response = await axios.post(`http://localhost:8006/api/profiles/create`, this.form);
                } else {
                    // Update existing profile
                    response = await axios.put(`http://localhost:8006/api/profiles/${this.userId}`, this.form);
                }

                this.form = response.data;
                this.originalProfile = JSON.parse(JSON.stringify(response.data));
                this.isCreatingNew = false;

                if (this.activeTab === 'personal') {
                    // Proceed to next tab if this was the first step in profile creation
                    this.activeTab = 'preferences';
                } else {
                    this.$router.push(`/profile/${this.userId}`);
                }
            } catch (error) {
                console.error('Error saving profile:', error);
                this.errors = { ...this.errors, api: error.response?.data?.message || 'Failed to save profile.' };
            } finally {
                this.saving = false;
            }
        },

        async savePreferences() {
            if (!this.validatePreferences()) return;

            this.saving = true;

            try {
                let response;

                if (this.isCreatingNew) {
                    // We're still creating the profile, update the whole profile
                    response = await axios.put(`http://localhost:8006/api/profiles/${this.userId}`, this.form);
                } else {
                    // Update only preferences
                    const preferencesDTO = {
                        gender: this.form.preferences.gender,
                        interests: this.form.preferences.interests,
                        maxDistance: this.form.preferences.maxDistance,
                        minAge: this.form.preferences.minAge,
                        maxAge: this.form.preferences.maxAge
                    };

                    response = await axios.put(`http://localhost:8006/api/profiles/${this.userId}/preferences`, preferencesDTO);
                }

                this.form = response.data;
                this.originalProfile = JSON.parse(JSON.stringify(response.data));

                if (this.isCreatingNew) {
                    // Proceed to next tab if this is part of profile creation
                    this.activeTab = 'location';
                    this.isCreatingNew = false;
                } else {
                    this.$router.push(`/profile/${this.userId}`);
                }
            } catch (error) {
                console.error('Error saving preferences:', error);
                this.errors = { ...this.errors, api: error.response?.data?.message || 'Failed to save preferences.' };
            } finally {
                this.saving = false;
            }
        },

        async saveLocation() {
            if (!this.validateLocation()) return;

            this.saving = true;

            try {
                // Update the profile with the new location
                const updatedProfile = { ...this.form };

                let response;
                if (this.isCreatingNew) {
                    response = await axios.post(`http://localhost:8006/api/profiles`, updatedProfile);
                    this.isCreatingNew = false;
                } else {
                    response = await axios.put(`http://localhost:8006/api/profiles/${this.userId}`, updatedProfile);
                }

                this.form = response.data;
                this.originalProfile = JSON.parse(JSON.stringify(response.data));
                this.$router.push(`/profile/${this.userId}`);
            } catch (error) {
                console.error('Error saving location:', error);
                this.errors = { ...this.errors, api: error.response?.data?.message || 'Failed to save location.' };
            } finally {
                this.saving = false;
            }
        },

        getCurrentLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        this.form.location.latitude = position.coords.latitude;
                        this.form.location.longitude = position.coords.longitude;
                    },
                    (error) => {
                        console.error('Error getting current location:', error);
                        this.errors = { ...this.errors, location: 'Failed to get current location. Please enter coordinates manually.' };
                    }
                );
            } else {
                this.errors = { ...this.errors, location: 'Geolocation is not supported by your browser. Please enter coordinates manually.' };
            }
        },

        addInterest() {
            const interest = this.newInterest.trim();
            if (interest && !this.form.preferences.interests.includes(interest)) {
                this.form.preferences.interests.push(interest);
                this.newInterest = '';
            }
        },

        removeInterest(index) {
            this.form.preferences.interests.splice(index, 1);
        },

        cancelEdit() {
            if (this.isCreatingNew) {
                // If creating a new profile, just go back to the profile page
                // The user will see the "no profile" state there
                this.$router.push(`/profile/${this.userId}`);
            } else {
                // If editing an existing profile, reset form to original values
                this.form = JSON.parse(JSON.stringify(this.originalProfile));

                // Reset image URL to original if there was one
                if (this.form.imageId) {
                    this.fetchProfileImage(this.form.imageId);
                } else {
                    this.profileImageUrl = null;
                }
                
                this.$router.push(`/profile/${this.userId}`);
            }
        }
    }
}
</script>

<style scoped>
.profile-edit-container {
    max-width: 800px;
    margin: 30px auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
    text-align: center;
    margin-bottom: 24px;
    color: #333;
}

.loading-container,
.error-container {
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
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

.error-actions {
    display: flex;
    justify-content: center;
    gap: 15px;
    margin-top: 15px;
}

.retry-button,
.create-button {
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    border: none;
}

.retry-button {
    background: #4a90e2;
    color: white;
}

.create-button {
    background: #27ae60;
    color: white;
}

.tabs {
    display: flex;
    margin-bottom: 25px;
    border-bottom: 1px solid #ddd;
}

.tab-button {
    flex: 1;
    background: none;
    border: none;
    padding: 12px;
    cursor: pointer;
    font-size: 16px;
    color: #555;
    transition: all 0.3s ease;
}

.tab-button.active {
    color: #4a90e2;
    border-bottom: 2px solid #4a90e2;
}

.tab-button:hover {
    background-color: #f5f5f5;
}

.edit-form {
    max-width: 600px;
    margin: 0 auto;
}

.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #555;
}

input[type="text"],
input[type="number"],
input[type="date"],
textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
}

input:focus,
textarea:focus {
    outline: none;
    border-color: #4a90e2;
    box-shadow: 0 0 3px rgba(74, 144, 226, 0.3);
}

textarea {
    resize: vertical;
    min-height: 100px;
}

.radio-group {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
}

.radio-label {
    display: flex;
    align-items: center;
    cursor: pointer;
}

.radio-label input {
    margin-right: 8px;
}

.age-range {
    display: flex;
    gap: 20px;
}

.age-input {
    flex: 1;
}

.age-input label {
    font-size: 14px;
    color: #666;
}

.range-slider {
    width: 100%;
    margin: 10px 0;
}

.range-value {
    text-align: center;
    font-weight: 500;
    color: #4a90e2;
}

.interests-input {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
}

.add-interest-button {
    padding: 10px 15px;
    background: #4a90e2;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.add-interest-button:disabled {
    background: #a0c0e8;
    cursor: not-allowed;
}

.interests-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 10px;
}

.interest-tag {
    background: #e8f0fe;
    color: #4a90e2;
    padding: 5px 10px;
    border-radius: 15px;
    font-size: 14px;
    display: flex;
    align-items: center;
}

.remove-interest {
    background: none;
    border: none;
    color: #4a90e2;
    margin-left: 5px;
    cursor: pointer;
    font-size: 18px;
    font-weight: bold;
    padding: 0 2px;
}

.location-map {
    margin: 20px 0;
    height: 250px;
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

.current-location-button {
    padding: 8px 16px;
    background: white;
    color: #4a90e2;
    border: 1px solid #4a90e2;
    border-radius: 4px;
    cursor: pointer;
    display: block;
    margin: 0 auto;
}

.form-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 30px;
}

.cancel-button {
    padding: 12px 20px;
    background: white;
    color: #555;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
}

.save-button {
    padding: 12px 25px;
    background: #4a90e2;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.save-button:hover {
    background: #3a80d2;
}

.save-button:disabled {
    background: #a0c0e8;
    cursor: not-allowed;
}

.error-message {
    color: #e74c3c;
    font-size: 12px;
    margin-top: 5px;
    display: block;
}

@media (max-width: 600px) {
    .tabs {
        flex-direction: column;
    }

    .tab-button {
        border-bottom: 1px solid #eee;
    }

    .tab-button.active {
        border-bottom: 1px solid #4a90e2;
    }

    .age-range {
        flex-direction: column;
        gap: 10px;
    }
}

/* Profile Image Styles */
.profile-image-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 40px;
}

.profile-image-container {
    width: 400px;
    height: 400px;
    overflow: hidden;
    margin-bottom: 15px;
    border: 3px solid #4a90e2;
    position: relative;
}

.profile-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.profile-image-placeholder {
    width: 100%;
    height: 100%;
    background-color: #e8f0fe;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 64px;
    font-weight: bold;
    color: #4a90e2;
}

.profile-image-actions {
    display: flex;
    gap: 10px;
}

.upload-button {
    padding: 8px 16px;
    background: #4a90e2;
    color: white;
    border-radius: 4px;
    cursor: pointer;
    text-align: center;
    font-size: 14px;
}

.hidden-input {
    display: none;
}

.remove-image-button {
    padding: 8px 16px;
    background: white;
    color: #e74c3c;
    border: 1px solid #e74c3c;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.upload-progress {
    width: 100%;
    max-width: 250px;
    margin-top: 10px;
    text-align: center;
}

.progress-bar {
    height: 10px;
    background-color: #eee;
    border-radius: 5px;
    margin-bottom: 5px;
    overflow: hidden;
}

.progress-fill {
    height: 100%;
    background-color: #4a90e2;
    transition: width 0.3s ease;
}

</style>