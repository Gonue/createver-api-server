<template>
    <div></div>
</template>
  
<script>
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';

export default {
    async beforeCreate() {
        const router = useRouter();
        const store = useStore();

        const accessToken = new URL(location.href).searchParams.get("access_token");

        if (accessToken) {
            document.cookie = `codongs=${accessToken}; secure; SameSite=None`;
            await store.commit('initializeStore');
        }

        router.push('/');
    },
};
</script>
  