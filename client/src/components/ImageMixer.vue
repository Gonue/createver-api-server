<template>
    <div class="mt-3 mb-3 text-center">
        <canvas ref="canvas" width="300" height="300"></canvas>
        <div>
            <button v-if="showButton" class="btn btn-success mt-2" style="width: 300px;" type="button"
                @click="buy">Buy</button>
        </div>
    </div>
</template>
  
<script>
import { mapActions } from 'vuex';

export default {
    props: ['originalImage', 'showButton'],
    data() {
        return {
            mugImageUrl: require('@/assets/gw.webp'),
        };
    },
    mounted() {
        this.mixImages();
    },
    methods: {
        async mixImages() {
            const canvas = this.$refs.canvas;
            const ctx = canvas.getContext('2d');

            // 원래 이미지 로드
            const originalImg = new Image();
            originalImg.src = this.originalImage;
            await new Promise(resolve => {
                originalImg.onload = resolve;
            });

            // 머그컵 이미지 로드
            const mugImg = new Image();
            mugImg.src = this.mugImageUrl;
            await new Promise(resolve => {
                mugImg.onload = resolve;
            });

            // 머그컵 이미지를 먼저 그립니다.
            ctx.drawImage(mugImg, 0, 0, 300, 300);

            ctx.drawImage(originalImg, 90, 100, 90, 90);

        },
        ...mapActions('image', ['updateOriginalImage']),
        buy() {
            this.updateOriginalImage(this.originalImage);
            this.$router.push({ name: 'checkOut' });
        },
    },

    watch: {
        originalImage() {
            this.mixImages();
        }
    }
};
</script>
  
<style scoped>
canvas {
    border-radius: 10px;
}
</style>
  