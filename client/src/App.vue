<template>
  <div id="app">
    <Header v-if="showHeader"/>
    <div id="contnet" class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import Header from './components/layout/Header.vue'

export default {
  name: 'App',
  components: {
    Header,
  },
  created(){
    this.$store.commit('initializeStore');
  },
  computed: {
    showHeader(){
      const excludePaths = ['/login', '/register'];
      return !excludePaths.includes(this.$route.path);
    }
  }

}
</script>

<style></style>
