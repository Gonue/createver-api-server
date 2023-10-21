import { createStore } from "vuex";
import axios from "axios";
import router from "@/router";

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
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
    updateProfileInfo(state, payload) {
      if (payload.nickName) state.nickName = payload.nickName;
      if (payload.profileImage) state.profileImage = payload.profileImage;
    },
    initializeStore(state) {
      const token = getCookie("codongs");
      if (token) {
        state.token = token;
        server
          .get("/api/v1/member/info", {
            headers: {
              Authorization: `${token}`,
            },
          })
          .then((response) => {
            const userInfo = response.data;
            state.email = userInfo.result.email;
            state.nickName = userInfo.result.nickName;
            state.profileImage = userInfo.result.profileImage;
          })
          .catch((err) => {
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
      document.cookie =
        "codongs=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    },
  },

  actions: {
    async login({ commit }, credentials) {
      const loginResponse = await server.post(
        "/api/v1/member/login",
        credentials
      );
      const token = loginResponse.headers["authorization"];

      document.cookie = `codongs=${token}; secure; SameSite=None`;

      const userInfoResponse = await server.get("/api/v1/member/info", {
        headers: {
          Authorization: `${token}`,
        },
      });
      const userInfo = userInfoResponse.data;

      commit("login", {
        email: userInfo.result.email,
        nickName: userInfo.result.nickName,
        profileImage: userInfo.result.profileImage,
        token: token,
      });

      router.push("/");

      console.log("commit : ", userInfo.result.email);
    },

    logout({ commit }) {
      commit("clearUserData");
      router.push("/");
    },

    async updateProfile({ commit, state }, payload) {
      try {
        const requestBody = {};
        if (payload.newNickName) requestBody.nickName = payload.newNickName;
        if (payload.newProfileImage)
          requestBody.profileImage = payload.newProfileImage;

        const response = await server.patch(
          "/api/v1/member/update",
          requestBody,
          {
            headers: {
              Authorization: state.token,
            },
          }
        );

        if (response.data.status === 200) {
          const updatedInfo = response.data.result;
          commit("updateProfileInfo", {
            nickName: updatedInfo.nickName,
            profileImage: updatedInfo.profileImage,
          });
        }
      } catch (error) {
        console.error("Profile update failed:", error);
      }
    },
  },
});
