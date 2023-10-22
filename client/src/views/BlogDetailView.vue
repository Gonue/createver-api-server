<template>
    <div class="container mt-5" v-if="blog">
        <div class="card-footer text-right">
            <router-link :to="`/blog/edit/${blog.articleId}`" class="btn btn-primary">수정</router-link>
        </div>
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h2 class="text-bold">{{ blog.title }}</h2>
                    </div>
                    <div class="card-body">
                        <div v-html="blog.content"></div>
                        <!-- 이하 다른 정보들 -->
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import server from '@/axios-config'

export default {
    data() {
        return {
            blog: null
        }
    },
    async mounted() {
        const articleId = this.$route.params.articleId;
        try {
            const response = await server.get(`/api/v1/article/${articleId}`);
            if (response.data.status === 200) {
                this.blog = response.data.result;
            } else {
                console.error('글 조회 실패했습니다.');
            }
        } catch (error) {
            console.error('API 호출 에러:', error);
        }
    }
}
</script>
