<template>
    <div class="container-fluid">
        <ExplanationMainVue></ExplanationMainVue>

        <div class="justify-content-center align-items-center">
            <div class="text-center">
                <div class="row justify-content-center">
                    <div class="col-10 col-md-8 col-lg-6 text-center justify-content-center">
                        <div class="row justify-content-center">
                            <div class="col-8 d-flex align-items-center justify-content-center gw">
                                <input class="form-control col-8" placeholder="Search for an image..." v-model="searchQuery"
                                    style="background-color: #3a3d47; color: #fff" @keydown.enter.prevent="searchImages">
                                <button class="col-4 btn btn-success fw-bold" @click="searchImages">Search</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="image-box mt-5">
                    <div v-if="results.length">
                        <a v-for="item in results" :key="item.galleryId">
                            <img :src="item.storageUrl" alt="Searched Image" @click="selectImage(item)" />
                        </a>
                    </div>
                    <div v-else>
                        <a v-for="item in defaultImages" :key="item.galleryId">
                            <img :src="item.storageUrl" alt="Default Image" @click="selectImage(item)" class="col-8" />
                        </a>
                    </div>
                    <ImageModal :selectedImageInfo="selectedImageInfo" @close="deselectImage" />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import ExplanationMainVue from '@/components/ExplanationMain.vue';
import ImageModal from '@/components/ImageModal.vue';

const server = axios.create({
    baseURL: process.env.SERVER_URL,
});

export default {
    components: {
        ImageModal,
        ExplanationMainVue
    },
    data() {
        return {
            searchQuery: '',
            results: [],
            defaultImages: [],
            selectedImageInfo: null,
        };
    },
    async mounted() {
        await this.loadDefaultImages();
    },
    methods: {

        shuffleArray(array) {
            for (let i = array.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [array[i], array[j]] = [array[j], array[i]];
            }
        },
        async searchImages() {
            try {
                const response = await server.get(`/api/v1/image/list/search`, {
                    params: { prompt: this.searchQuery }
                });
                if (response.data.status === 200) {
                    this.results = response.data.result.content;
                    this.shuffleArray(this.results);

                } else {
                    console.error('API 호출 실패:', response.data.message);
                }
            } catch (error) {
                console.error('API 호출 중 에러 발생:', error);
            }
        },
        async loadDefaultImages() {
            try {
                const response = await server.get('/api/v1/image/list?size=60');
                if (response.data.status === 200) {
                    this.defaultImages = response.data.result.content;
                    this.shuffleArray(this.defaultImages);
                }
            } catch (error) {
                console.error('API 호출 중 에러 발생:', error);
            }
        },
        selectImage(item) {
            this.selectedImageInfo = item;
        },
        deselectImage() {
            this.selectedImageInfo = null;
        },
    },
};
</script>

<style scoped>
.container-fluid {
    background-color: #141827;
    color: white;

    min-height: 100vh;
    padding: 0;
}

.image-box img {
    width: 250px;
    margin: 10px;
    border-radius: 15px;
}

@media (max-width: 768px) {
    .image-box {
        display: flex;
        flex-wrap: wrap;
    }

    .image-box img {
        width: calc(50% - 50px);
        /* 20px는 양쪽 마진을 고려한 값 */
        margin: 10px;
    }

}

.form-control {
    margin-right: 5px;
    margin-left: 10px;
    /* 버튼 왼쪽에 마진 추가 */
}

.btn-success {
    margin-left: 5px;
    /* 버튼 왼쪽에 마진 추가 */
    margin-right: 10px;
}


.form-control {
    border: 2px solid #ccc;
    /* 테두리 설정 */
    background-color: rgb(34 40 57);
    color: white;
}

.form-control::placeholder {
    color: white;
    /* 하얀색으로 설정 */
}

.form-control:focus {
    outline: none !important;
    box-shadow: none !important;
    /* 포커스 상태에서 box-shadow 제거 */
    border: 2px solid #ccc;
    /* 테두리 설정 */

    -webkit-text-fill-color: white;
    /* 웹킷 브라우저에서 텍스트 색상 고정 */
    background-color: rgb(34, 40, 57);
    /* 포커스 상태에서 배경색 고정 */
}

.btn-success {
    margin-left: 5px;
    margin-right: 10px;
    background-color: #2c7aa3;
    border-color: #2c7aa3;
    transition: background-color 0.3s ease, transform 0.3s ease;
    box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
}

.btn-success:hover {
    background-color: #5c33ae;
    border-color: #6239b4;
}

.btn-success:active {
    transform: scale(0.95);
}

.btn-success:focus {
    box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.5);
}
</style>
