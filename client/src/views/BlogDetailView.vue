<template>
    <div class="container-fluid mt-5 text-white" v-if="blog">
        <div class="row">
            <div class="col-3 d-none d-md-block"></div>
            <div class="col-12 col-md-6 tx-wrap">
                <div class="d-flex justify-content-end" v-if="$store.state.roles.includes('ADMIN')">
                    <router-link :to="`/blog/edit/${blog.articleId}`" class="btn btn-primary mr-2">수정</router-link>
                    <button @click="deleteArticle" class="btn btn-danger">삭제</button>

                </div>
                <h1 class="text-bold mb-3">{{ blog.title }}</h1>

                <div class="info-line">
                    <h6 class="tc"> {{ formatDate(blog.createdAt) }}</h6>
                    <h6 class="tc"> ✏️ By {{ blog.member.nickName }}</h6>
                </div>
                <div class="udline"></div>

                <div v-html="blog.content"></div>
            </div>
            <div class="col-3 d-none d-md-block">
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
    },

    methods: {
        formatDate(dateString) {
            const options = { year: 'numeric', month: 'short', day: 'numeric' };
            const date = new Date(dateString);
            return date.toLocaleDateString('en-US', options);
        },

        async deleteArticle() {
            try {
                const response = await server.delete(`/api/v1/article/${this.blog.articleId}`);
                if (response.data.status === 200) {
                    this.$router.push('/blog');
                    alert('글이 성공적으로 삭제되었습니다.');
                } else {
                    alert('글 삭제에 실패하였습니다.');
                }
            } catch (error) {
                console.error("글 삭제에 실패하였습니다: ", error);
                alert('글 삭제에 실패하였습니다.');
            }
        }
    }
}
</script>


<style scoped>
::v-deep img {
    max-width: 100%;
    border-radius: 10px;
    border: solid 1px rgb(135, 144, 154);
    height: auto;
}

.tx-wrap {
    word-wrap: break-word;
    overflow-wrap: break-word;
}

.udline {
    margin-bottom: 30px;
    margin-top: 5px;
    border-bottom: 1px solid #656975;
    padding-bottom: 10px;
}

.tc {
    font-weight: 600;
    color: #8F94A3;
}

.info-line {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.btn {
    margin-left: 5px;
    margin-bottom: 10px;
}
</style>