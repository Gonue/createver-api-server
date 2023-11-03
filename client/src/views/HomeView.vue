<template>
  <div class="container-fluid">

    <ExplanationMainVue></ExplanationMainVue>
    <div class="row justify-content-center align-items-center">
      <div class="col-10 col-md-8 col-lg-6 text-center justify-content-center">
        <div class="row justify-content-center">
          <div class="col-8 d-flex align-items-center justify-content-center gw">

            <div class="input-group col-8">
              <input type="text" class="form-control" placeholder="What do you want to generate?"
                style="background-color: #3a3d47; color: #fff;" v-model="inputText" @keydown.enter.prevent="drawImage">
                <p class="btn-clear" type="button" v-if="inputText" @click="clearInput"><i class="bi bi-trash3"></i></p>
            </div>

            <button class="col-4 btn btn-success fw-bold gw align-items-center" @click="drawImage">
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

        <div class="col-lg-12 col-md-8 col-sm-12 mt-3">

          <div class="loading-container" v-if="isLoading">
            <!-- <div class="loading-container"> -->
            <div class="elapsed-time">{{ displayTime }}</div>
            <div class="loading-gif">
              <img src="../assets/loding.gif">
            </div>
          </div>

          <div v-if="isLoading || results.length" class="image-box mb-2">

            <div v-for="item in results" :key="item.galleryId">
              <img :src="item.storageUrl" alt="Generated Image" @click="selectImage(item)" />
            </div>

            <ImageModal :selectedImageInfo="selectedImageInfo" @close="deselectImage" />

          </div>

        </div>


      </div>
      <ImageTagComponent @tagSelected="handleTagSelected"></ImageTagComponent>
      <ImageGalleryVue></ImageGalleryVue>

    </div>

  </div>
</template>

<script>
import server from '@/axios-config';
import ImageGalleryVue from '@/components/ImageGallery.vue';
import ExplanationMainVue from '@/components/ExplanationMain.vue';
import ImageModal from '@/components/ImageModal.vue';
import ImageTagComponent from '@/components/ImageTagComponent.vue';

export default {
  components: {
    ImageGalleryVue,
    ExplanationMainVue,
    ImageModal,
    ImageTagComponent
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

      if (this.inputText.length > 200) {
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
    },

    handleTagSelected(tagName) {
      this.inputText = this.inputText ? this.inputText + ' ' + tagName : tagName;
    },

    clearInput() {
      this.inputText = '';  // inputText를 초기화
    },
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
  border: 2px solid #ccc;
  background-color: rgb(34 40 57);
  color: white;
  z-index: 1;
  border-radius: 7px !important;
}

.form-control::placeholder {
  color: #8F94A3;
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

.btn-success {
  margin-left: 10px;
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

.btn-clear {
  position: absolute;
  background: none;
  border: none;
  color: #fff;
  font-size: 16px;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 100 !important;
}


</style>