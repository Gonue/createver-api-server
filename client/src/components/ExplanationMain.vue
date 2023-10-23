<template>
    <div>
        <div class="d-flex justify-content-center align-items-center vh">
            <div class="text-center">
                <a href="/">
                    <img src="../assets/mainlogo2.png" class="logo-img">
                </a>
                <div class="text-below-logo row justify-content-center align-items-center mt-3">
                    {{ displayText }}
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Explanation',
    computed: {
        displayText() {
            if (this.$route.path === '/image/search') {
                return 'Find AI generated images from the Createver database';
            }
            return 'Create AI Art with our free AI image generator.';
        }
    }
}
</script>

<style scoped>
.vh {
    min-height: 15vh;
    color: #8f94a3;
}

.logo-img {
    width: 250px;
    height: auto;
    margin-top: 50px;
}

.text-below-logo {
    margin-top: 15px;
    /* 이미지와 텍스트 사이의 여백 */
    margin-bottom: 50px;
}
</style>
