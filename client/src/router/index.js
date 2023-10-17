import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import ImageCreateView from "../views/ImageCreateView.vue";
import ImageSearchView from "../views/ImageSearchView.vue";
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
    path: "/image/create",
    name: "imageCreate",
    component: ImageCreateView,
  },
  {
    path: "/image/search",
    name: "imageSearch",
    component: ImageSearchView,
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
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/"
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
