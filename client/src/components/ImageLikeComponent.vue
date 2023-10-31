<template>
    <div>
        <div>
            <button class="cus-btn mx-1 mb-3" type="button" v-if="hasLiked" @click="likeImage">
                <i class="bi bi-hand-thumbs-up-fill"></i> {{ likeCount }}
            </button>
            <button class="cus-btn mx-1 mb-3" type="button" v-else @click="likeImage">
                <i class="bi bi-hand-thumbs-up"></i> {{ likeCount }}
            </button>
        </div>
    </div>
</template>

<script>
import server from '@/axios-config';

export default {
    props: ['localSelectedImageInfo'],
    data() {
        return {
            hasLiked: false,
            likeCount: 0,

        };
    },
    watch: {
        'localSelectedImageInfo.galleryId': {
            immediate: true,
            async handler(newVal) {
                if (newVal) {
                    await this.fetchLikeStatus(newVal);
                    await this.fetchLikeCount(newVal);
                }
            }
        }
    },

    methods: {

        async fetchLikeStatus(galleryId) {
            try {
                const response = await server.get(`/api/v1/image/like/status/${galleryId}`);
                this.hasLiked = response.data.result;
            } catch (error) {
                console.error('API 호출 중 에러 발생:', error);
            }
        },
        async fetchLikeCount(galleryId) {
            try {
                const response = await server.get(`/api/v1/image/like/count/${galleryId}`);
                this.likeCount = response.data.result;
            } catch (error) {
                console.error('API 호출 중 에러 발생:', error);
            }
        },
        async likeImage() {
            if (!this.$store.state.token) {
                this.$router.push('/login');
                return;
            }

            const galleryId = this.localSelectedImageInfo.galleryId;

            if (this.hasLiked) {
                alert('이미 좋아요를 눌렀습니다.');
                return;
            }

            try {
                const response = await server.post(`/api/v1/image/like/${galleryId}`);
                if (response.data.status === 200) {
                    this.hasLiked = true;
                    this.likeCount += 1;

                }
            } catch (error) {
                console.error('좋아요 추가 중 에러 발생:', error);
            }
        },
    },
};
</script>

<style scoped>

.cus-btn {
    text-align: center;
    padding: 12px 16px;
    border: 1px solid transparent;
    border-radius: 5px;
    background-color: rgba(37, 120, 198, 0.639);
    color: rgb(232, 238, 244);
    margin-bottom: 10px;
}
</style>