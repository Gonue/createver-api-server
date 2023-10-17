<template>
    <div class="mt-5">
        <canvas ref="canvas" width="300" height="300"></canvas>
        <div>
            <button class="btn btn-success mt-2" style="width: 300px;" type="button">Buy</button>
        </div>
    </div>
</template>
  
<script>
export default {
    props: ['originalImage'],
    data() {
        return {
            mugImageUrl: require('@/assets/gw.webp')
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

            // 원래 이미지를 머그컵 이미지 위에 그립니다.
            // 위치와 크기는 실제 머그컵 이미지에 따라 조절해야 합니다.
            ctx.drawImage(originalImg, 90, 100, 90, 90); // 예시

        }
    }
};
</script>
  
<style scoped>

canvas{
    border-radius: 10px;
}
</style>
  