// store/index.js
import { createStore } from 'vuex';

const store = createStore({
  state: {
    token: null
  },
  mutations: {
    setToken(state, token) {
      state.token = token;
    }
  },
  actions: {
    updateToken({ commit }, token) {
      commit('setToken', token);
    }
  },
  getters: {
    getToken: state => state.token
  }
});

export default store;