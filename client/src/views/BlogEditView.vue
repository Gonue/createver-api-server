<template>
    <div class="container mt-5 text-white">
        <form @submit.prevent="updateArticle">
            <div class="mb-3">
                <label for="title" class="form-label">제목</label>
                <input type="text" id="title" v-model="title" class="form-control" />
            </div>
            <div class="mb-3">
                <label for="content" class="form-label">내용</label>
                <QuillEditor v-model:content="content" :modules="modules" contentType="html" theme="snow" toolbar="full" />
            </div>
            <div class="mb-3">
                <button type="submit" class="btn btn-primary">수정</button>
                <button type="button" @click="cancel" class="btn btn-secondary">취소</button>
            </div>
        </form>
    </div>
</template>

<script>
import server from '@/axios-config'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import ImageUploader from 'quill-image-uploader';
import BlotFormatter from 'quill-blot-formatter'


export default {
    components: {
        QuillEditor,
    },
    data() {
        return {
            title: '',
            content: '',
            articleId: null,
            modules: [
                {
                    name: 'imageUploader',
                    module: ImageUploader,
                    options: {
                        upload: file => {
                            return new Promise((resolve, reject) => {
                                const formData = new FormData();
                                formData.append("images", file);

                                server.post('/api/v1/image/upload', formData, {
                                    headers: {
                                        'Content-Type': 'multipart/form-data'
                                    }
                                })
                                    .then(res => {
                                        resolve(res.data.result);
                                    })
                                    .catch(err => {
                                        reject("Upload failed");
                                        console.error("Error:", err)
                                    })
                            })
                        }
                    }
                },
                {
                    name: 'blotFormatter',
                    module: BlotFormatter,
                    options: {}
                }
            ]
        };
    },
    async mounted() {
        this.articleId = this.$route.params.articleId;
        try {
            const response = await server.get(`/api/v1/article/${this.articleId}`);
            if (response.data.status === 200) {
                this.title = response.data.result.title;
                this.content = response.data.result.content;
            }
        } catch (error) {
            console.error("API 호출 에러:", error);
        }
    },
    methods: {
        async updateArticle() {
            try {
                const response = await server.patch(`/api/v1/article/${this.articleId}`, {
                    title: this.title,
                    content: this.content
                });
                if (response.data.status === 200) {
                    this.$router.push(`/blog/${this.articleId}`)
                    alert('글이 성공적으로 수정되었습니다.');
                } else {
                    alert('글 수정에 실패하였습니다.');
                }
            } catch (error) {
                console.error("글 수정에 실패하였습니다: ", error);
                alert('글 수정에 실패하였습니다.');
            }
        },
        cancel() {
            this.$router.push(`/blog/${this.articleId}`);
        },
    },
}
</script>

<style scoped>
::v-deep .ql-editor {
    min-height: 500px;
}

::v-deep .ql-toolbar {
    background-color: rgb(188, 234, 238);
}
</style>
