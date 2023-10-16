<template>
    <div class="header mt-5">
        <div class="tab" :class="{ 'selected-tab': selectedTab === 'Inspirations' }" @click="selectTab('Inspirations')">
            Inspirations</div>
        <div class="tab" :class="{ 'selected-tab': selectedTab === 'Resent' }" @click="selectTab('Resent')">
            Resent</div>
    </div>
    <div class="image-gallery">
        <div class="image-container">
            <img src="../assets/draw.jpeg" alt="Image 1">
            도지
        </div>
        <div class="image-container">
            <img src="../assets/draw.jpeg" alt="Image 2"> 도지
        </div>
        <div class="image-container">
            <img src="../assets/draw.jpeg" alt="Image 3">
        </div>
        <div class="image-container">
            <img src="../assets/draw.jpeg" alt="Image 4">
        </div>
    </div>
</template>
  
<script>
export default {
    name: 'ImageGallery',

    data() {
        return {
            selectedTab: 'Inspirations', // 선택된 탭을 나타내는 변수
        };
    },

    methods: {
        selectTab(tabName) {
            this.selectedTab = tabName; // 선택된 탭을 설정
        }
    },
}
</script>
  
<style scoped>
.image-gallery {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
}

.image-container {
    flex: 1;
    padding: 10px;
}

.image-container img {
    max-width: 100%;
    border-radius: 10px;
    height: auto;
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
        flex: 0 0 calc(50% - 20px) !important;
    }
}
</style>
