<template>
  <div class="container-fluid">
      <!-- Main Content -->

      <div class="row justify-content-center align-items-center">
          <img src="../assets/main.jpeg" class="mainImg mt-5">
      </div>

      <div class="row justify-content-center align-items-center vh">
          <div class="col-10 col-md-8 col-lg-6 text-center">
              <div class="input-group mb-3">
                  <textarea class="form-control" placeholder="What do you want to generate?" rows="2"></textarea>
                  <button class="btn btn-warning btn-lg">
                      <span>Draw</span>
                  </button>
              </div>

              <div class="button-container justify-content-center">

                  <button type="button" class="custom-btn btn-space" :class="{ 'selected-btn': selectedButton === 1 }"
                      @click="selectButton(1)">
                      <img src="../assets/art.jpeg" alt="Style example" class="rounded-md custom-img">
                      <a class="custom-text">Art</a>
                  </button>
                  <button type="button" class="custom-btn btn-space" :class="{ 'selected-btn': selectedButton === 2 }"
                      @click="selectButton(2)">
                      <img src="../assets/draw.jpeg" alt="Style example" class="rounded-md custom-img">
                      <a class="custom-text">Drawing</a>
                  </button> <button type="button" class="custom-btn btn-space"
                      :class="{ 'selected-btn': selectedButton === 3 }" @click="selectButton(3)">
                      <img src="../assets/photo.jpeg" alt="Style example" class="rounded-md custom-img">
                      <a class="custom-text">Photo</a>
                  </button> <button type="button" class="custom-btn btn-space"
                      :class="{ 'selected-btn': selectedButton === 4 }" @click="selectButton(4)">
                      <img src="../assets/none.jpeg" alt="Style example" class="rounded-md custom-img">
                      <a class="custom-text">None</a>
                  </button>
              </div>


              <div>
                  <div class="mt-4">
                      <img id="generatedImage" src="" alt="Generated Image" class="img-fluid">
                  </div>
                  <button type="button" class="btn btn-primary btn-lg btn-space">Large button</button>
                  <button type="button" class="btn btn-secondary btn-lg btn-space">Large button</button>
              </div>
          </div>
      </div>
  </div>
</template>


<script>
export default {
  data() {
      return {
          selectedButton: 1, // 선택된 버튼의 ID를 저장, 초기 값은 1로 설정
      };
  },
  methods: {
      selectButton(id) {
          this.selectedButton = id; // 선택된 버튼의 ID를 저장
      },
  },
};
</script>



<style scoped>
/* Custom Styles */


.vh {
  min-height: 45vh;
}

.btn-space {
  margin-right: 30px;
}

.btn-lg {
  width: 100%;
}

textarea.form-control {
  resize: none;
}

@media (max-width: 768px) {
  .btn-lg {
      width: auto;
  }
  .custom-btn {
  flex-direction: column; /* 이미지와 텍스트를 수직으로 정렬 */
  align-items: center; /* 중앙 정렬 */
}
.custom-text {
  margin-left: 0; /* 마진 제거 */
  margin-right: 0; /* 마진 제거 */
}
.btn-space {
  margin-right: 15px; /* 마진 줄임 */
  margin-bottom: 15px; /* 버튼 사이에 수직 간격 추가 */
}
.button-container {
  flex-wrap: wrap; /* 버튼을 여러 줄로 나눔 */
  justify-content: space-around; /* 버튼 사이의 간격을 균등하게 배분 */
}
}
.btn-lg {
      width: auto;
  }
.container-fluid {
  background-color: #141827;
  color: white;
  min-height: 100vh;
  padding: 0;
}

.mainImg {
  width: 350px;
  height: 350px;
  border-radius: 4em;
}

.custom-btn {
  display: flex;
  align-items: center;
  background-color: rgb(34 40 57);
  border: 10px #313437;
  border-radius: 10px;
  position: relative;
  border: 3px solid transparent;

}

.custom-img {
  height: 55px;
  width: 65px;
  border-radius: 15px;

}

.custom-text {
  color: rgb(95, 120, 212);
  margin-left: 20px;
  margin-right: 20px;
  text-decoration: none;
}

.button-container {
  display: flex;
  align-items: center;
}

.selected-btn {
  border: 3px solid #3358bd;
}
</style>