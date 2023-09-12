import { createStore } from "vuex";
import axios from "axios";
import router from "@/router";

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

const server = axios.create({
  baseURL: process.env.BASE_URL,
});

export default createStore({
  state: {
    email: "",
    nickName: "",
    profileImage: "",
    token: "",
  },
  
  mutations: {
    login(state, payload) {
      state.email = payload.email;
      state.nickName = payload.nickName;
      state.profileImage = payload.profileImage;
      state.token = payload.token;
    },
    initializeStore(state) {
        const token = getCookie("codongs");
        if (token) {
          state.token = token;
          server.get('/api/v1/member/info', {
            headers: {
              'Authorization': `${token}`
            }
          }).then(response => {
            const userInfo = response.data;
            state.email = userInfo.result.email;
            state.nickName = userInfo.result.nickName;
            state.profileImage = userInfo.result.profileImage;
          }).catch(err => {
            console.error("Failed to fetch user info: ", err);
          });
        }
    },
    clearUserData(state) {
      state.email = "";
      state.nickName = "";
      state.profileImage = "";
      state.token = "";
      // 쿠키에서 토큰 제거
      document.cookie = "codongs=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    }
  },
  
  actions: {
    async login({ commit }, credentials) {
      const loginResponse = await server.post("/api/v1/member/login", credentials);
      const token = loginResponse.headers["authorization"];

      document.cookie = `codongs=${token}; secure; SameSite=None`;

      const userInfoResponse = await server.get("/api/v1/member/info", {
        headers: {
          Authorization: `${token}`,
        },
      });
      const userInfo = userInfoResponse.data;
      console.log("123", userInfo);

      commit("login", {
        email: userInfo.result.email,
        nickName: userInfo.result.nickName,
        profileImage: userInfo.result.profileImage,
        token: token,
      });

      router.push("/");

      console.log("commit : ", userInfo.result.email);
    },

    logout({ commit }){
      commit("clearUserData");
      router.push("/");
    }
  },
});
