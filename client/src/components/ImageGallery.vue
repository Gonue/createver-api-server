<template>
    <div>
        <div class="header">
            <div class="tab" :class="{ 'selected-tab': selectedTab === 'Inspirations' }" @click="selectTab('Inspirations')">
                Inspirations
            </div>
            <div class="tab" :class="{ 'selected-tab': selectedTab === 'Resent' }" @click="selectTab('Resent')">
                Resent
            </div>
        </div>

        
        <div>
            <GalleryInspirations v-if="selectedTab === 'Inspirations'" />
            <GalleryResent v-else />
        </div>

    </div>
</template>

<script>
import GalleryResent from '@/components/GalleryResent.vue';
import GalleryInspirations from '@/components/GalleryInspirations.vue';

export default {
    components: {
        GalleryResent,
        GalleryInspirations
    },
    name: 'ImageGallery',
    data() {
        return {
            selectedTab: 'Inspirations',
        };
    },
    methods: {
        selectTab(tabName) {
            this.selectedTab = tabName;
        },
    }
}
</script>
  
<style scoped>
.header {
    display: flex;
    justify-content: center;
}

.tab {
    padding: 5px;
    text-align: center;
    cursor: pointer;
    width: 100px;
    color: #8F94A3;
}

.selected-tab {
    color: white !important;
}
</style>
