<template>
    <div class="outer-container">
        <div v-if="images.length === 0" class="loading-text">
            <img src="../assets/loding.gif">
        </div>
        <div v-else ref="masonry" class="masonry-container">
            <GalleryCard v-for="image in images" :key="image.galleryId" :image="image" @image-loaded="updateMasonry"
                @image-selected="selectImage" />
        </div>
        <ImageModal :selectedImageInfo="selectedImageInfo" @close="deselectImage" />
    </div>
</template>

<script>
import server from '@/axios-config';
import GalleryCard from '@/components/GalleryCard.vue';
import ImageModal from '@/components/ImageModal.vue';
import Masonry from 'masonry-layout';

export default {
    components: {
        GalleryCard,
        ImageModal
    },
    data() {
        return {
            images: [],
            selectedImageInfo: null,
        };
    },
    async created() {
        await this.loadImages();
    },
    mounted() {
        window.addEventListener('resize', this.updateMasonry);
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.updateMasonry);
    },
    methods: {
        updateMasonry() {
            this.$nextTick(() => {
                if (!this.msnry) {
                    this.msnry = new Masonry(this.$refs.masonry, {
                        itemSelector: '.gallery-card',
                        columnWidth: 60,
                        gutter: 10,
                        isFitWidth: true,
                    });
                } else {
                    this.msnry.layout();
                }
            });
        },
        async loadImages() {
            try {
                const response = await server.get('/api/v1/image/listWithCommentCountAndLikeCount?sort=createdAt&size=60');
                if (response.data.status === 200) {
                    this.images = response.data.result.content.map(image => {
                        if (image.storageUrl && !image.s3Url) {
                            image.s3Url = image.storageUrl;
                        }
                        return image;
                    });
                    this.images.sort(() => Math.random() - 0.5);
                }
            } catch (error) {
                console.error("API 호출 중 오류 발생:", error);
            }
        },
        selectImage(item) {
            this.selectedImageInfo = item;
        },
        deselectImage() {
            this.selectedImageInfo = null;
        }
    }
}
</script>

<style scoped>
.outer-container {
    text-align: center;
}

.masonry-container {
    margin: 0 auto;
    text-align: left;
}
</style>
