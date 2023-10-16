<template>
    <div class="container-fluid">


        <ExplanationMainVue></ExplanationMainVue>
        <div class="row justify-content-center align-items-center">
            <div class="col-10 col-md-8 col-lg-6 text-center">

                <div class="input-group mb-3">
                    <textarea class="form-control ar me-2" placeholder="What do you want to generate?"
                        style="background-color: #3a3d47; color: #fff;" v-model="inputText"
                        @keydown.enter.prevent="drawImage"></textarea>
                    <button class="btn btn-lg fw-bold" style="background-color: #4266db;" @click="drawImage">
                        <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor"
                                class="bi bi-brush" viewBox="0 0 16 16">
                                <path
                                    d="M15.825.12a.5.5 0 0 1 .132.584c-1.53 3.43-4.743 8.17-7.095 10.64a6.067 6.067 0 0 1-2.373 1.534c-.018.227-.06.538-.16.868-.201.659-.667 1.479-1.708 1.74a8.118 8.118 0 0 1-3.078.132 3.659 3.659 0 0 1-.562-.135 1.382 1.382 0 0 1-.466-.247.714.714 0 0 1-.204-.288.622.622 0 0 1 .004-.443c.095-.245.316-.38.461-.452.394-.197.625-.453.867-.826.095-.144.184-.297.287-.472l.117-.198c.151-.255.326-.54.546-.848.528-.739 1.201-.925 1.746-.896.126.007.243.025.348.048.062-.172.142-.38.238-.608.261-.619.658-1.419 1.187-2.069 2.176-2.67 6.18-6.206 9.117-8.104a.5.5 0 0 1 .596.04zM4.705 11.912a1.23 1.23 0 0 0-.419-.1c-.246-.013-.573.05-.879.479-.197.275-.355.532-.5.777l-.105.177c-.106.181-.213.362-.32.528a3.39 3.39 0 0 1-.76.861c.69.112 1.736.111 2.657-.12.559-.139.843-.569.993-1.06a3.122 3.122 0 0 0 .126-.75l-.793-.792zm1.44.026c.12-.04.277-.1.458-.183a5.068 5.068 0 0 0 1.535-1.1c1.9-1.996 4.412-5.57 6.052-8.631-2.59 1.927-5.566 4.66-7.302 6.792-.442.543-.795 1.243-1.042 1.826-.121.288-.214.54-.275.72v.001l.575.575zm-4.973 3.04.007-.005a.031.031 0 0 1-.007.004zm3.582-3.043.002.001h-.002z" />
                            </svg> Draw</span>
                    </button>
                </div>
                <transition name="fade" mode="out-in">

                    <div v-if="inputText">
                        <div class="button-container justify-content-center">

                            <button type="button" class="custom-btn btn-space"
                                :class="{ 'selected-btn': selectedButton === 1 }" @click="selectButton(1)">
                                <div class="inner-container">
                                    <img src="../assets/art.jpeg" alt="Style example" class="rounded-md custom-img">
                                    <a :class="{ 'selected-text': selectedButton === 1 }" class="custom-text">Art</a>
                                </div>
                            </button>
                            <button type="button" class="custom-btn btn-space"
                                :class="{ 'selected-btn': selectedButton === 2 }" @click="selectButton(2)">
                                <div class="inner-container">

                                    <img src="../assets/draw.jpeg" alt="Style example" class="rounded-md custom-img">
                                    <a :class="{ 'selected-text': selectedButton === 2 }" class="custom-text">Drawing</a>
                                </div>

                            </button> <button type="button" class="custom-btn btn-space"
                                :class="{ 'selected-btn': selectedButton === 3 }" @click="selectButton(3)">
                                <div class="inner-container">

                                    <img src="../assets/photo.jpeg" alt="Style example" class="rounded-md custom-img">
                                    <a :class="{ 'selected-text': selectedButton === 3 }" class="custom-text">Photo</a>

                                </div>

                            </button> <button type="button" class="custom-btn btn-space"
                                :class="{ 'selected-btn': selectedButton === 4 }" @click="selectButton(4)">
                                <div class="inner-container">

                                    <img src="../assets/none.jpeg" alt="Style example" class="rounded-md custom-img">
                                    <a :class="{ 'selected-text': selectedButton === 4 }" class="custom-text">None</a>
                                </div>

                            </button>
                        </div>
                    </div>
                </transition>

                <div class="col-lg-12 col-md-8 col-sm-12 mt-5">

                    <div class="loading-container" v-if="isLoading">
                        <!-- <div class="loading-container"> -->
                        <div class="elapsed-time">{{ displayTime }}</div>
                        <div class="loading-gif">
                            <img src="../assets/loding.gif">
                        </div>
                    </div>

                    <div v-if="isLoading || results.length" class="image-box">

                        <div v-for="item in results" :key="item.galleryId">
                            <img :src="item.s3Url" alt="Generated Image" @click="selectImage(item)" />
                        </div>

                        <ImageModal :selectedImageInfo="selectedImageInfo" @close="deselectImage" />

                    </div>
                </div>

                <ImageGalleryVue></ImageGalleryVue>
            </div>
        </div>

    </div>
</template>

<script>
import axios from 'axios';
import ImageGalleryVue from '@/components/ImageGallery.vue';
import ExplanationMainVue from '@/components/ExplanationMain.vue';
import ImageModal from '@/components/ImageModal.vue';


const server = axios.create({
    baseURL: process.env.SERVER_URL,
})

export default {
    components: {
        ImageGalleryVue,
        ExplanationMainVue,
        ImageModal
    },
    data() {
        return {
            selectedButton: 1, // 선택된 버튼의 ID를 저장, 초기 값은 1로 설정
            inputText: '',  // 사용자가 입력한 텍스트를 저장할 변수
            results: [
                {
                    galleryId: 23,
                    prompt: 'test image',
                    s3Url: require('@/assets/draw.jpeg'),
                    option: 2,
                    createdAt: '2023-10-15T17:55:14.916023'
                },
                {
                    galleryId: 24,
                    prompt: 'test image',
                    s3Url: require('@/assets/draw.jpeg'),
                    option: 2,
                    createdAt: '2023-10-15T17:55:15.183301'
                },
                {
                    galleryId: 25,
                    prompt: 'test image',
                    s3Url: require('@/assets/draw.jpeg'),
                    option: 2,
                    createdAt: '2023-10-15T17:55:15.387539'
                },
                {
                    galleryId: 26,
                    prompt: 'test image',
                    s3Url: require('@/assets/draw.jpeg'),
                    option: 2,
                    createdAt: '2023-10-15T17:55:15.604433'
                }
            ],  // API 응답 결과를 저장
            selectedImageInfo: null,  // 선택한 이미지의 상세 정보
            isLoading: false,  // 로딩 상태를 관리
            elapsedTime: 0,    // 경과된 시간
            timer: null,        // 타이머 ID
            displayTime: '0.0s'

        };
    },

    methods: {
        selectButton(id) {
            this.selectedButton = id; // 선택된 버튼의 ID를 저장
        },
        async drawImage() {
            this.isLoading = true;  // 로딩 시작
            this.elapsedTime = 0;

            this.timer = setInterval(() => {
                this.elapsedTime += 0.1;
                this.elapsedTime = parseFloat(this.elapsedTime.toFixed(1));
                this.displayTime = this.elapsedTime.toFixed(1) + 's';
            }, 100);


            try {
                const response = await server.post("/api/v1/image/create", {
                    prompt: this.inputText,
                    option: this.selectedButton
                });

                if (response.data.status === 200) {
                    this.results = response.data.result;

                } else {
                    console.error('API 호출 실패:', response.data.message);
                }
            } catch (error) {
                console.error('API 호출 중 에러 발생:', error);
            }
            clearInterval(this.timer);
            this.isLoading = false;

        },
        selectImage(item) {
            this.selectedImageInfo = item;  // 선택한 이미지의 상세 정보 설정

        },
        deselectImage() {
            this.selectedImageInfo = null;
        }


    },
};
</script>



<style scoped>
.container-fluid {
    background-color: #141827;
    color: white;
    height: auto;
    min-height: 100vh;
    padding: 0;
}

/* 텍스트에어리어와 버튼 스타일 */
.input-group .ar {
    border: 1px solid #ccc;
    border-radius: 4px 0 0 4px;
}

.input-group .btn {
    border-radius: 0 4px 4px 0;
}

.custom-img {
    width: 40px;
    height: 40px;
    margin-right: 10px;
    margin-left: -10px;
    margin-top: -11px;
    margin-bottom: -11px;
}

/* 선택된 버튼 스타일 */
.selected-btn {
    border-color: #398cf2cc !important;
}

.selected-text {
    color: white !important;
}

/* 버튼 간격 */
.btn-space {
    margin: 10px;
}

/* 큰 버튼 스타일 */
.btn-lg.btn-space {
    margin-top: 20px;
}

/* 커스텀 버튼 컨테이너 */
.button-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
}

/* 커스텀 버튼 스타일 */
.custom-btn {
    display: flex;
    margin: 5px;
    display: flex;
    align-items: center;
    padding: 10px;
    background-color: rgb(34 40 57);
    box-sizing: border-box;
    transition: all 0.3s ease;
    border: 2px solid transparent;
    border-radius: 10px;
    border: 3px solid transparent;
    position: relative;

}

/* 반응형: 화면 크기가 768px 미만일 때 */
@media (max-width: 768px) {
    .custom-btn {
        min-width: calc(50% - 20px);
        /* 2x2 그리드로 버튼이 나열됨 */
        max-width: calc(50% - 20px);
    }



    .input-group textarea,
    .input-group button {
        margin-top: 15px;
        flex: 1 0 100%;
        /* 모바일 환경에서는 너비 100%로 설정 */
    }
}

.input-group>textarea.form-control.ar {
    border-top-right-radius: 5px !important;
    border-bottom-right-radius: 5px !important;
}

.input-group>.btn {
    border-top-left-radius: 5px !important;
    border-bottom-left-radius: 5px !important;
}

.button-container {
    display: flex;
    align-items: center;
}

.inner-container {
    display: flex;
    align-items: center;
}

.custom-text {
    color: #8F94A3;
    text-decoration: none;
    font-size: 14px;
}

textarea.form-control {
    resize: none;
    width: 50px;
    height: 50px;

}

.ar::placeholder {
    color: white !important;
    opacity: 1;
    font-family: 'Courier New', Courier, monospace;
}


.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s ease-in-out;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.fade-enter-to,
.fade-leave-from {
    opacity: 1;
}

.image-box {
    position: relative;
    display: flex;
    flex-wrap: wrap;
    border-radius: 25px;
    justify-content: center;
    width: 100%;
    align-items: flex-start;
    height: auto;
}


.image-box img {
    flex: 1;
    width: 350px;
    height: auto;
    margin: 5px;
    border-radius: 15px;
    max-width: 100%;
    max-height: 100%;
    overflow: hidden;

    transition: all 0.3s ease-in-out;
}

.image-box img:hover {
    transform: scale(0.95);
}

.loading-container {
    width: 350px;
    height: 350px;
    position: relative;
    margin: auto;
}

.elapsed-time {
    top: 0;
    left: 100%;
    position: absolute;
    font-size: 15px;
    font-weight: bold;
}

.loading-gif {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

.loading-gif img {
    width: 50%;
    height: auto;
}
</style>