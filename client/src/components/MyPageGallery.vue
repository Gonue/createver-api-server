<template>
    <div class="row mt-5 d-flex">
        <h2 class="d-flex justify-content-center">My Gallery</h2>
        <div class="gallery-container d-flex flex-wrap justify-content-center">
            <div v-for="item in galleryItems" :key="item.galleryId" class="gallery-item">
                <img :src="item.storageUrl" alt="Gallery Image" class="img-fluid" @click="selectImage(item)">
            </div>
        </div>
            <ImageModal :selectedImageInfo="selectedImageInfo" @close="deselectImage" />
    </div>
</template>

<script>
import server from '@/axios-config';
import ImageModal from '@/components/ImageModal.vue';



export default {
    components: {
        ImageModal
    },
    data() {
        return {
            galleryItems: [],
            selectedImageInfo: null,  // 선택한 이미지의 상세 정보

        }
    },

    async mounted() {
        try {
            const response = await server.get('/api/v1/member/my-galleries');
            if (response.data.status === 200) {
                this.galleryItems = response.data.result.content;
            }
        } catch (error) {
            console.error('Failed to fetch galleries:', error);
        }
    },

    methods: {
        selectImage(item) {
            this.selectedImageInfo = item;
        },
        deselectImage() {
            this.selectedImageInfo = null;
        },
    },
}
</script>


<style scoped>
.gallery-item {
    margin: 10px;
    width: 13%;
}

.img-fluid {
    border-radius: 15px;
    height: 100%;
}
</style>