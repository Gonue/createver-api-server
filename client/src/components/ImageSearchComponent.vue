<template>
    <div>
        <div class="carousel">
            <button class="scroll-button" @click="scroll(-1)">←</button>
            <div class="image-container">
                <div class="image" v-for="image in relatedImages" :key="image.galleryId" @click="selectImage(image)">
                    <img :src="image.storageUrl" alt="Related Image" width="100" />
                </div>
            </div>
            <button class="scroll-button" @click="scroll(1)">→</button>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

const server = axios.create({
    baseURL: process.env.SERVER_URL,
})

export default {
    props: ['localSelectedImageInfo'],
    data() {
        return {
            relatedImages: []
        };
    },
    async mounted() {
        await this.fetchRelatedImages();
    },
    methods: {
        async fetchRelatedImages() {
            try {
                const response = await server.get(`/api/v1/image/list/search`, {
                    params: { prompt: this.localSelectedImageInfo.prompt }
                });
                this.relatedImages = response.data.result.content;
            } catch (error) {
                console.error('관련 이미지 가져오기 실패:', error);
            }
        },
        selectImage(image) {
            this.$emit('image-selected', image);
        },
        scroll(direction) {
            const container = this.$el.querySelector('.image-container');
            const scrollAmount = direction * container.clientWidth;
            container.scrollLeft += scrollAmount;
        }
    }
};
</script>

<style scoped>
.carousel {
    display: flex;
    align-items: center;
}

.image-container {
    flex-grow: 1;
    overflow-x: auto;
    display: flex;
}

.image {
    flex: 0 0 auto;
    margin: 5px;
}

img {
    border-radius: 10px;
}

.scroll-button {
    background-color: #141827;
    border: none;
    color: white;
    padding: 10px 20px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 30px;
    margin: 4px 2px;
    cursor: pointer;
}
</style>
