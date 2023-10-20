<template>
    <div>
        <div class="header mt-5">
            <div class="tab" :class="{ 'selected-tab': selectedTab === 'Inspirations' }" @click="selectTab('Inspirations')">
                Inspirations
            </div>
            <div class="tab" :class="{ 'selected-tab': selectedTab === 'Resent' }" @click="selectTab('Resent')">
                Resent
            </div>
        </div>
        <div class="image-gallery">
            <div class="image-container" v-for="image in sortedImages" :key="image.galleryId">

                <div class="image-box">
                    <img :src="image.storageUrl" :alt="'Image ' + image.galleryId" @click="selectImage(image)">
                </div>
                <div class="text-box">
                <a v-if="selectedTab === 'Inspirations'">
                    {{ truncateString(image.prompt, 18) }}
                </a>
                <a v-else>
                    {{ truncateString(image.prompt, 18) }}
                    <br>
                    <a class="time justify-content-center">

                        <svg xmlns="http://www.w3.org/2000/svg" width="8" height="8" fill="currentColor"
                            class="bi bi-alarm-fill" viewBox="0 0 16 16">
                            <path
                                d="M6 .5a.5.5 0 0 1 .5-.5h3a.5.5 0 0 1 0 1H9v1.07a7.001 7.001 0 0 1 3.274 12.474l.601.602a.5.5 0 0 1-.707.708l-.746-.746A6.97 6.97 0 0 1 8 16a6.97 6.97 0 0 1-3.422-.892l-.746.746a.5.5 0 0 1-.707-.708l.602-.602A7.001 7.001 0 0 1 7 2.07V1h-.5A.5.5 0 0 1 6 .5zm2.5 5a.5.5 0 0 0-1 0v3.362l-1.429 2.38a.5.5 0 1 0 .858.515l1.5-2.5A.5.5 0 0 0 8.5 9V5.5zM.86 5.387A2.5 2.5 0 1 1 4.387 1.86 8.035 8.035 0 0 0 .86 5.387zM11.613 1.86a2.5 2.5 0 1 1 3.527 3.527 8.035 8.035 0 0 0-3.527-3.527z" />
                        </svg> {{ timeSince(image.createdAt) }}
                    </a>
                </a>
            </div>
            </div>
        </div>
        <ImageModal :selectedImageInfo="selectedImageInfo" @close="deselectImage" />
    </div>
</template>

<script>
import axios from 'axios';
import ImageModal from '@/components/ImageModal.vue';


const server = axios.create({
    baseURL: process.env.SERVER_URL,
})

export default {
    components: {
        ImageModal
    },
    name: 'ImageGallery',

    data() {
        return {
            selectedTab: 'Inspirations', // 선택된 탭을 나타내는 변수
            images: [], // API에서 가져올 이미지 정보를 저장할 변수
            selectedImageInfo: null,  // 선택한 이미지의 상세 정보
        };
    },
    computed: {
        sortedImages() {
            let sorted = [...this.images];

            if (this.selectedTab === 'Resent') {
                sorted.sort((a, b) => b.galleryId - a.galleryId);
            } else if (this.selectedTab === 'Inspirations') {
                sorted = this.shuffleArray(sorted);
            }

            return sorted;
        }
    },
    async mounted() {
        await this.loadImages();
    },
    methods: {
        async loadImages() {
            try {
                const response = await server.get('/api/v1/image/list?size=60');
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
        selectTab(tabName) {
            this.selectedTab = tabName; // 선택된 탭을 설정
            this.loadImages(); // 선택된 탭에 따라 이미지를 다시 불러옴
        },
        timeSince(date) {
            const now = new Date();
            const past = new Date(date);
            const seconds = Math.floor((now - past) / 1000);

            let interval = Math.floor(seconds / 31536000);
            if (interval > 1) return interval + " years";

            interval = Math.floor(seconds / 2592000);
            if (interval > 1) return interval + " months";

            interval = Math.floor(seconds / 86400);
            if (interval > 1) return interval + " days";
            if (interval === 1) return "1 day";

            interval = Math.floor(seconds / 3600);
            if (interval > 1) return interval + " hours";
            if (interval === 1) return "1 hour";

            interval = Math.floor(seconds / 60);
            if (interval > 1) return interval + " mins";
            if (interval === 1) return "1 min";

            return "Just now";
        },
        truncateString(str, num) {
            if (str.length <= num) {
                return str;
            }
            return str.slice(0, num) + '...';
        },

        selectImage(item) {
            this.selectedImageInfo = item;  // 선택한 이미지의 상세 정보 설정

        },
        deselectImage() {
            this.selectedImageInfo = null;
        },

        shuffleArray(array) {
            for (let i = array.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [array[i], array[j]] = [array[j], array[i]];
            }
            return array;
        },

    }
}
</script>
  
<style scoped>
.image-gallery {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
}

.image-container {
    flex: 0 0 calc(35% - 20px);
    padding: 10px;
}

.image-container img {
    max-width: 100%;
    border-radius: 5px;
    height: auto;
}

.image-container img:hover {
    transform: scale(0.95);
    transition: transform 0.3s ease-in-out;
}

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


@media (max-width: 768px) {
    .image-container {
        flex: 0 0 calc(50% - 20px);
    }
}

a {
    font-size: 12px;
    font-weight: 400;
}

.time {
    color: #8F94A3;
    text-decoration: none;
    display: flex;
    align-items: center;
}

.time svg {
    margin-right: 4px;
}

.image-box, .text-box {
    width: 100%;
}
</style>
