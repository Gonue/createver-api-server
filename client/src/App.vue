<template>
  <div id="app">
    <headerView/>
    <div id="contnet" class="content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import headerView from './components/layout/Header.vue'

export default {
  name: 'App',
  components: {
    headerView,
  },
  created(){
    this.$store.commit('initializeStore');
  },
  
}
</script>

<style></style>
