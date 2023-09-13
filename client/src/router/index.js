import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import ImageView from "../views/ImageView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue"
import CommunityList from "../views/CommunityList.vue"
import UserOauth from "../components/UserOauth.vue"

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },

  {
    path: "/login",
    name: "login",
    component: LoginView,
  },
  {
    path: "/image",
    name: "image",
    component: ImageView,
  },
  {
    path: "/register",
    name : "regetser",
    component: RegisterView,
  },
  {
    path: "/community",
    name: "community",
    component: CommunityList
  },

  {
    path: "/user-oauth",
    name: "user-oauth",
    component : UserOauth
  }


];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
