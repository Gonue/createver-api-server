<template>
    <div class="outer-container">
        <div ref="masonry" class="masonry-container">
            <GalleryCard v-for="image in images" :key="image.galleryId" :image="image" @image-loaded="handleImageLoaded"
                @image-selected="selectImage"/>
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


    mounted() {
        window.addEventListener('resize', this.updateMasonry);
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.updateMasonry);
    },


    async created() {
        await this.loadImages();
    },


    methods: {
        handleImageLoaded() {
            this.updateMasonry();
        },

        updateMasonry() {
            this.$nextTick(function () {
                const msnry = new Masonry(this.$refs.masonry, {
                    itemSelector: '.gallery-card',
                    columnWidth: 60,
                    gutter: 10,
                    isFitWidth: true,
                });
                msnry.layout();
            });
        },

        async loadImages() {
            try {
                const response = await server.get('/api/v1/image/list?sort=createdAt,&size=60');
                if (response.data.status === 200) {

                    this.images = response.data.result.content.map(image => {
                        if (image.storageUrl && !image.s3Url) {
                            image.s3Url = image.storageUrl;
                        }
                        return image;
                    });
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

.masonry-container{
    margin: 0 auto;
    text-align: left;
}


</style>