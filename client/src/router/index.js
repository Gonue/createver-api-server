import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import ImageView from "../views/ImageView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue"

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
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
