import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import ImageCreateView from "../views/ImageCreateView.vue";
import ImageSearchView from "../views/ImageSearchView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import CommunityList from "../views/CommunityList.vue";
import UserOauth from "../components/UserOauth.vue";
import MyPageView from "../views/MyPageView.vue";
import BlogView from "../views/BlogView.vue";
import BlogWriteView from "../views/BlogWriteView";
import BlogDetailView from "../views/BlogDetailView";
import BlogEditView from "../views/BlogEditView";
import PricingView from "../views/PricingView"

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
    name: "regetser",
    component: RegisterView,
  },
  {
    path: "/community",
    name: "community",
    component: CommunityList,
  },

  {
    path: "/user-oauth",
    name: "user-oauth",
    component: UserOauth,
  },
  {
    path: "/mypage",
    name: "mypage",
    component: MyPageView,
  },
  {
    path: "/blog",
    name: "blog",
    component: BlogView,
  },
  {
    path: "/blog/write",
    name: "blogWrite",
    component: BlogWriteView,
  },
  {
    path: "/blog/:articleId",
    name: "blogDetail",
    component: BlogDetailView,
  },
  {
    path: "/blog/edit/:articleId",
    name: "blogEdit",
    component: BlogEditView,
  },
  {
    path: "/pricing",
    name: "pricingView",
    component: PricingView,
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/",
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;

import store from "@/store";

router.beforeEach((to, from, next) => {
  // 어드민만 접근 가능한 경로

  const adminOnlyRoutes = ["blogWrite", "blogEdit"];

  if (adminOnlyRoutes.includes(to.name)) {
    if (store.state.roles.includes("ADMIN")) {
      next();
    } else {
      next({ name: "home" });
    }
  } else {
    next();
  }
});
