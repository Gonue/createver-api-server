<template>
  <div class="container-fluid">


    <ExplanationMainVue></ExplanationMainVue>
    <div class="row justify-content-center align-items-center">
      <div class="col-10 col-md-8 col-lg-6 text-center justify-content-center">
        <div class="row justify-content-center">
          <div class="col-8 d-flex align-items-center justify-content-center gw">
            <input class="form-control col-8" placeholder="What do you want to generate?"
              style="background-color: #3a3d47; color: #fff;" v-model="inputText" @keydown.enter.prevent="drawImage">
            <button class="col-4 btn btn-success btn fw-bold gw align-items-center" @click="drawImage">
              <span>Draw</span>
            </button>

          </div>
          <div v-if="errorMessage" class="text-danger">
            {{ errorMessage }}
          </div>
        </div>
        <transition name="fade" mode="out-in">

          <div v-if="inputText">
            <div class="button-container justify-content-center">

              <button type="button" class="custom-btn btn-space" :class="{ 'selected-btn': selectedButton === 1 }"
                @click="selectButton(1)">
                <div class="inner-container">
                  <img src="../assets/art.jpeg" alt="Style example" class="rounded-md custom-img">
                  <a :class="{ 'selected-text': selectedButton === 1 }" class="custom-text">Art</a>
                </div>
              </button>
              <button type="button" class="custom-btn btn-space" :class="{ 'selected-btn': selectedButton === 2 }"
                @click="selectButton(2)">
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
              <img :src="item.storageUrl" alt="Generated Image" @click="selectImage(item)" />
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
      results: [],  // API 응답 결과를 저장
      selectedImageInfo: null,  // 선택한 이미지의 상세 정보
      isLoading: false,  // 로딩 상태를 관리
      elapsedTime: 0,    // 경과된 시간
      timer: null,        // 타이머 ID
      displayTime: '0.0s',
      errorMessage: '',  // 에러 메시지를 저장할 새로운 속성
    };
  },

  methods: {
    selectButton(id) {
      this.selectedButton = id; // 선택된 버튼의 ID를 저장
    },
    async drawImage() {
      if (!this.inputText.trim()) {
        this.errorMessage = '* Space cannot be entered';
        return;
      }

      if (this.inputText.length > 30) {
        this.errorMessage = '* Too many characters';
        return;
      }

      // 유효성 검증 통과시 에러 메시지 초기화
      this.errorMessage = '';


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
          this.results = response.data.result.map(item => {
            if (item.s3Url && !item.storageUrl) {
              item.storageUrl = item.s3Url;
            }
            return item;
          });

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

/* 커스텀 버튼 컨테이너 */
.button-container {
  margin-top: 20px;
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
    min-width: calc(25% - 20px);
    max-width: calc(50% - 20px);

  }

  .gw {
    margin-top: 15px;
    flex-direction: column;
    width: 100%;
  }

  .form-control {
    flex: 1;
    width: 100% !important;
    /* 너비를 100%로 조절 */
    margin-right: 14px !important;
  }
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

.loading-container {
  width: 250px;
  height: 250px;
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
}
</style>