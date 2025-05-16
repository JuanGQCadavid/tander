<template>
    <div class="admin-view">
        <h1 class="admin-heading">Admin Dashboard</h1>

        <!-- Error message -->
        <div v-if="error" class="error-alert">
            <strong class="font-bold">Error:</strong>
            <span class="block sm:inline">{{ error }}</span>
        </div>

        <!-- Loading indicator -->
        <div v-if="loading" class="loading-container">
            <div class="loader"></div>
            <span class="loading-text">Loading data...</span>
        </div>

        <div v-else class="dashboard-grid">
            <!-- Users Section -->
            <div class="data-card">
                <h2 class="section-heading">Users</h2>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="table-header">ID</th>
                                <th class="table-header">Email</th>
                                <th class="table-header">Phone</th>
                                <th class="table-header">Verified</th>
                                <th class="table-header">Notifications</th>
                                <th class="table-header">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="user in userData" :key="user.id" class="table-row">
                                <td class="table-cell">{{ user.id }}</td>
                                <td class="table-cell">{{ user.email }}</td>
                                <td class="table-cell">{{ user.phoneNumber }}</td>
                                <td class="table-cell">
                                    <span :class="user.isVerified ? 'status-badge-success' : 'status-badge-error'">
                                        {{ user.isVerified ? 'Yes' : 'No' }}
                                    </span>
                                </td>
                                <td class="table-cell">
                                    <div class="badge-container">
                                        <span
                                            :class="user.notificationPreference?.allowMessage ? 'notification-badge-enabled' : 'notification-badge-disabled'">
                                            Message: {{ user.notificationPreference?.allowMessage ? 'Yes' : 'No' }}
                                        </span>
                                        <span
                                            :class="user.notificationPreference?.allowEmail ? 'notification-badge-enabled' : 'notification-badge-disabled'">
                                            Email: {{ user.notificationPreference?.allowEmail ? 'Yes' : 'No' }}
                                        </span>
                                        <span
                                            :class="user.notificationPreference?.allowPush ? 'notification-badge-enabled' : 'notification-badge-disabled'">
                                            Push: {{ user.notificationPreference?.allowPush ? 'Yes' : 'No' }}
                                        </span>
                                    </div>
                                </td>
                                <td class="table-cell">
                                    <div class="action-buttons">
                                        <button @click="fetchUserChats(user.id)" class="btn-view">
                                            View Chats
                                        </button>
                                        <button @click="executeDelete('user', user.id)" class="btn-delete">
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr v-if="userData.length === 0">
                                <td colspan="6" class="empty-message">No user data available</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Profiles Section -->
            <div class="data-card">
                <h2 class="section-heading">Profiles</h2>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="table-header">User ID</th>
                                <th class="table-header">Name</th>
                                <th class="table-header">Age</th>
                                <th class="table-header">Gender</th>
                                <th class="table-header">Location</th>
                                <th class="table-header">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="profile in profileData" :key="profile.userId" class="table-row">
                                <td class="table-cell">{{ profile.userId }}</td>
                                <td class="table-cell">{{ profile.name }}</td>
                                <td class="table-cell">{{ calculateAge(profile.dateOfBirth) }}</td>
                                <td class="table-cell">{{ profile.gender }}</td>
                                <td class="table-cell">
                                    <span v-if="profile.location">{{ profile.location.latitude.toFixed(2) }}, {{
                                        profile.location.longitude.toFixed(2) }}</span>
                                    <span v-else>Not set</span>
                                </td>
                                <td class="table-cell">
                                    <div class="action-buttons">
                                        <button @click="viewProfileDetails(profile)" class="btn-view">
                                            Details
                                        </button>
                                        <button @click="executeDelete('profiles', profile.userId)" class="btn-delete">
                                            Delete
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr v-if="profileData.length === 0">
                                <td colspan="6" class="empty-message">No profile data available</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Chats Section -->
            <div class="data-card">
                <h2 class="section-heading">
                    Chats
                    <span v-if="selectedUserId" class="user-filter-label">
                        for User ID: {{ selectedUserId }}
                    </span>
                </h2>

                <div v-if="!selectedUserId && !chatLoading" class="info-alert">
                    <p class="font-medium">Select a user from the Users table to view their chats.</p>
                </div>

                <div v-if="chatLoading" class="loading-container">
                    <div class="loader"></div>
                    <span class="loading-text">Loading chats...</span>
                </div>

                <div v-else-if="selectedUserId" class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="table-header">ID</th>
                                <th class="table-header">Status</th>
                                <th class="table-header">Created</th>
                                <th class="table-header">Archived</th>
                                <th class="table-header">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="chat in chatData" :key="chat.id" class="table-row">
                                <td class="table-cell">{{ chat.id }}</td>
                                <td class="table-cell">
                                    <span :class="chat.isActive ? 'status-badge-success' : 'status-badge-neutral'">
                                        {{ chat.isActive ? 'Active' : 'Inactive' }}
                                    </span>
                                </td>
                                <td class="table-cell">{{ formatDate(chat.dateOfCreation) }}</td>
                                <td class="table-cell">{{ chat.dateOfArchived ? formatDate(chat.dateOfArchived) : 'N/A'
                                    }}</td>
                                <td class="table-cell">
                                    <button @click="executeDelete('chat', chat.id)" class="btn-delete">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr v-if="chatData.length === 0">
                                <td colspan="5" class="empty-message">No chat data available for this user</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Matches Section -->
            <div class="data-card">
                <h2 class="section-heading">Matches</h2>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="table-header">Match ID</th>
                                <th class="table-header">Profile 1</th>
                                <th class="table-header">Profile 2</th>
                                <th class="table-header">Status 1</th>
                                <th class="table-header">Status 2</th>
                                <th class="table-header">Created</th>
                                <th class="table-header">Match Status</th>
                                <th class="table-header">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="match in matchData" :key="match.matchId" class="table-row">
                                <td class="table-cell">{{ match.matchId }}</td>
                                <td class="table-cell">{{ match.profileId1 }}</td>
                                <td class="table-cell">{{ match.profileId2 }}</td>
                                <td class="table-cell">
                                    <span :class="getStatusClass(match.profileStatus1)">
                                        {{ match.profileStatus1 }}
                                    </span>
                                </td>
                                <td class="table-cell">
                                    <span :class="getStatusClass(match.profileStatus2)">
                                        {{ match.profileStatus2 }}
                                    </span>
                                </td>
                                <td class="table-cell">{{ formatDate(match.createdAt) }}</td>
                                <td class="table-cell">
                                    <span
                                        :class="isMatchSuccessful(match) ? 'status-badge-success' : 'status-badge-neutral'">
                                        {{ isMatchSuccessful(match) ? 'Matched' : 'Pending' }}
                                    </span>
                                </td>
                                <td class="table-cell">
                                    <button @click="executeDelete('matches', match.matchId)" class="btn-delete">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr v-if="matchData.length === 0">
                                <td colspan="8" class="empty-message">No match data available</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Profile Detail Modal -->
        <div v-if="selectedProfile" class="modal-overlay">
            <div class="modal-container">
                <div class="modal-header">
                    <h3 class="modal-title">Profile Details</h3>
                    <button @click="selectedProfile = null" class="modal-close">
                        <span class="text-2xl">&times;</span>
                    </button>
                </div>

                <div class="modal-grid">
                    <div>
                        <h4 class="detail-section-title">Basic Information</h4>
                        <p><strong>Name:</strong> {{ selectedProfile.name }}</p>
                        <p><strong>User ID:</strong> {{ selectedProfile.userId }}</p>
                        <p><strong>Date of Birth:</strong> {{ formatDate(selectedProfile.dateOfBirth) }}</p>
                        <p><strong>Age:</strong> {{ calculateAge(selectedProfile.dateOfBirth) }}</p>
                        <p><strong>Gender:</strong> {{ selectedProfile.gender }}</p>
                        <p><strong>Bio:</strong> {{ selectedProfile.bio || 'Not provided' }}</p>
                    </div>

                    <div>
                        <h4 class="detail-section-title">Location</h4>
                        <div v-if="selectedProfile.location">
                            <p><strong>Latitude:</strong> {{ selectedProfile.location.latitude }}</p>
                            <p><strong>Longitude:</strong> {{ selectedProfile.location.longitude }}</p>
                        </div>
                        <p v-else>No location information available</p>

                        <h4 class="detail-section-title">Preferences</h4>
                        <div v-if="selectedProfile.preferences">
                            <p><strong>Gender Preference:</strong> {{ selectedProfile.preferences.gender || 'Not specified' }}</p>
                            <p><strong>Age Range:</strong> {{ selectedProfile.preferences.minAge }} - {{
                                selectedProfile.preferences.maxAge }}</p>
                            <p><strong>Max Distance:</strong> {{ selectedProfile.preferences.maxDistance }} km</p>

                            <div
                                v-if="selectedProfile.preferences.interests && selectedProfile.preferences.interests.length > 0">
                                <p><strong>Interests:</strong></p>
                                <div class="interest-tags">
                                    <span v-for="(interest, idx) in selectedProfile.preferences.interests" :key="idx"
                                        class="interest-tag">
                                        {{ interest }}
                                    </span>
                                </div>
                            </div>
                            <p v-else><strong>Interests:</strong> None specified</p>
                        </div>
                        <p v-else>No preference information available</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'AdminView',
    data() {
        return {
            userData: [],
            profileData: [],
            chatData: [],
            matchData: [],
            loading: true,
            chatLoading: false,
            error: null,
            selectedProfile: null,
            selectedUserId: null,
        };
    },
    beforeRouteEnter(_to, _from, next) {
        //TODO: if verified as admin
        next();
        //TODO: else
        //   next({ name: 'home' });
    },
    created() {
        this.fetchAllData();
    },
    methods: {
        async fetchAllData() {
            this.loading = true;
            this.error = null;

            try {
                const [users, profiles, matches] = await Promise.all([
                    this.fetchData('user', 8003),
                    this.fetchData('profiles', 8006),
                    this.fetchData('matches', 8007)
                ]);

                this.userData = users;
                this.profileData = profiles;
                this.matchData = matches;
                this.chatData = [];
            } catch (err) {
                this.error = "Failed to load data. Please try again later.";
                console.error("Error parsing data:", err);
            } finally {
                this.loading = false;
            }
        },
        async fetchData(serviceName, port) {
            try {
                const response = await axios.get(`http://localhost:${port}/api/${serviceName}/`);
                return response.data || [];
            } catch (error) {
                console.error(`Error fetching ${serviceName} data:`, error);
                throw error;
            }
        },
        async fetchUserChats(userId) {
            this.selectedUserId = userId;
            this.chatLoading = true;
            this.chatData = [];

            try {
                const response = await axios.get(`http://localhost:8009/api/chat/`, {
                    headers: {
                        userId: userId
                    }
                });
                this.chatData = response.data || [];
            } catch (error) {
                console.error(`Error fetching chats for user ${userId}:`, error);
                this.error = `Failed to load chats for user ${userId}. Please try again later.`;
            } finally {
                this.chatLoading = false;
            }
        },
        formatDate(dateString) {
            if (!dateString) return '';

            try {
                const date = new Date(dateString);
                return date.toLocaleString();
            } catch (e) {
                return dateString;
            }
        },
        calculateAge(dateOfBirth) {
            if (!dateOfBirth) return 'N/A';

            try {
                const birthDate = new Date(dateOfBirth);
                const today = new Date();
                let age = today.getFullYear() - birthDate.getFullYear();
                const monthDiff = today.getMonth() - birthDate.getMonth();

                if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                    age--;
                }

                return age;
            } catch (e) {
                return 'N/A';
            }
        },
        getStatusClass(status) {
            if (status === 'LIKE') return 'status-badge-success';
            if (status === 'DISLIKE') return 'status-badge-error';
            return 'status-badge-warning';
        },
        isMatchSuccessful(match) {
            return match.profileStatus1 === 'LIKE' && match.profileStatus2 === 'LIKE';
        },
        viewProfileDetails(profile) {
            this.selectedProfile = profile;
        },
        async executeDelete(type, id) {
            try {
                let port;
                switch (type) {
                    case 'user':
                        port = 8003;
                        break;
                    case 'profiles':
                        port = 8006;
                        break;
                    case 'chat':
                        port = 8009;
                        break;
                    case 'matches':
                        port = 8007;
                        break;
                    default:
                        throw new Error('Unknown service type');
                }
                await axios.delete(`http://localhost:${port}/api/${type}/${id}`);

                if (type === 'user') {
                    this.userData = this.userData.filter(item => item.id !== id);
                    if (this.selectedUserId === id) {
                        this.selectedUserId = null;
                        this.chatData = [];
                    }
                } else if (type === 'profiles') {
                    this.profileData = this.profileData.filter(item => item.userId !== id);
                } else if (type === 'chat') {
                    this.chatData = this.chatData.filter(item => item.id !== id);
                } else if (type === 'matches') {
                    this.matchData = this.matchData.filter(item => item.matchId !== id);
                }
            } catch (error) {
                console.error(`Error deleting ${type} with ID ${id}:`, error);
                this.error = `Failed to delete ${type}. Please try again later.`;
            }
        },
    }
};
</script>

<style scoped>
/* Main Layout */
.admin-view {
    padding: 1.5rem;
    max-width: 1200px;
    margin: 0 auto;
}

.admin-heading {
    font-size: 1.5rem;
    font-weight: bold;
    margin-bottom: 1.5rem;
}

.dashboard-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 2rem;
}

/* Card Styles */
.data-card {
    background-color: white;
    border-radius: 0.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    padding: 1.5rem;
}

.section-heading {
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 1rem;
}

.user-filter-label {
    font-size: 0.875rem;
    font-weight: normal;
    color: #6b7280;
    margin-left: 0.5rem;
}

/* Table Styles */
.table-container {
    overflow-x: auto;
}

.data-table {
    min-width: 100%;
    background-color: white;
    border-collapse: collapse;
}

.table-header {
    padding: 0.5rem 1rem;
    border-bottom: 1px solid #e5e7eb;
    background-color: #f9fafb;
    text-align: left;
    font-size: 0.75rem;
    font-weight: 500;
    color: #6b7280;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.table-row {
    transition: background-color 0.2s;
}

.table-row:hover {
    background-color: #f9fafb;
}

.table-cell {
    padding: 0.5rem 1rem;
    border-bottom: 1px solid #e5e7eb;
}

.empty-message {
    padding: 1rem;
    text-align: center;
    color: #6b7280;
}

/* Badge Styles */
.badge-container {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
}

.status-badge-success {
    background-color: #d1fae5;
    color: #065f46;
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
    border-radius: 9999px;
}

.status-badge-error {
    background-color: #fee2e2;
    color: #991b1b;
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
    border-radius: 9999px;
}

.status-badge-warning {
    background-color: #fef3c7;
    color: #92400e;
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
    border-radius: 9999px;
}

.status-badge-neutral {
    background-color: #f3f4f6;
    color: #4b5563;
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
    border-radius: 9999px;
}

.notification-badge-enabled {
    background-color: #dbeafe;
    color: #1e40af;
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
    border-radius: 9999px;
}

.notification-badge-disabled {
    background-color: #f3f4f6;
    color: #6b7280;
    padding: 0.25rem 0.5rem;
    font-size: 0.75rem;
    border-radius: 9999px;
}

/* Button Styles */
.action-buttons {
    display: flex;
    gap: 0.25rem;
}

.btn-view {
    background-color: #3b82f6;
    color: white;
    padding: 0.25rem 0.5rem;
    border-radius: 0.25rem;
    font-size: 0.75rem;
    transition: background-color 0.2s;
    border: none;
    cursor: pointer;
}

.btn-view:hover {
    background-color: #2563eb;
}

.btn-delete {
    background-color: #ef4444;
    color: white;
    padding: 0.25rem 0.5rem;
    border-radius: 0.25rem;
    font-size: 0.75rem;
    transition: background-color 0.2s;
    border: none;
    cursor: pointer;
}

.btn-delete:hover {
    background-color: #dc2626;
}

/* Modal Styles */
.modal-overlay {
    position: fixed;
    inset: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 50;
}

.modal-container {
    background-color: white;
    border-radius: 0.5rem;
    padding: 1.5rem;
    max-width: 42rem;
    width: 100%;
    max-height: 90vh;
    overflow-y: auto;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.modal-title {
    font-size: 1.25rem;
    font-weight: bold;
}

.modal-close {
    color: #6b7280;
    background: none;
    border: none;
    cursor: pointer;
    transition: color 0.2s;
}

.modal-close:hover {
    color: #1f2937;
}

.modal-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1rem;
}

@media (min-width: 768px) {
    .modal-grid {
        grid-template-columns: 1fr 1fr;
    }
}

.detail-section-title {
    font-weight: 600;
    margin-bottom: 0.5rem;
    margin-top: 1rem;
}

.detail-section-title:first-child {
    margin-top: 0;
}

.interest-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
    margin-top: 0.25rem;
}

.interest-tag {
    background-color: #dbeafe;
    color: #1e40af;
    padding: 0.25rem 0.5rem;
    border-radius: 9999px;
    font-size: 0.75rem;
}

/* Alert Styles */
.error-alert {
    background-color: #fee2e2;
    border: 1px solid #f87171;
    color: #b91c1c;
    padding: 0.75rem 1rem;
    margin-bottom: 1rem;
    border-radius: 0.25rem;
}

.info-alert {
    background-color: #dbeafe;
    border-left: 4px solid #3b82f6;
    color: #1e40af;
    padding: 1rem;
    margin-bottom: 1rem;
}

/* Loading Styles */
.loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 2rem 0;
}

.loading-text {
    margin-left: 0.5rem;
}

.loader {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3b82f6;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}
</style>