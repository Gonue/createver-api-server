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

<style>
#app{
  background-color: #141827;
  overflow-x: hidden;

}

*::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

*::-webkit-scrollbar-track {
    background-color: #141827;
}

*::-webkit-scrollbar-thumb {
    background-color: rgb(107, 108, 117);
    border-radius: 10px;
}

</style>
