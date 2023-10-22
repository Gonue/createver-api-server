<template>
    <div class="container mt-5 text-white">
        <form @submit.prevent="submit">
            <div class="mb-3">
                <label for="title" class="form-label">제목</label>
                <input type="text" id="title" v-model="title" class="form-control" />
            </div>
            <div class="mb-3">
                <label for="content" class="form-label">내용</label>
                <QuillEditor v-model:content="content" :modules="modules" contentType="html" theme="snow" toolbar="full"
                    class="text-dark" />
            </div>
            <div class="mb-3">
                <button type="submit" class="btn btn-primary">저장</button>
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

    methods: {
        async submit() {
            console.log("Submit button was clicked.")
            console.log("title:", this.title)
            console.log("content:", this.content)

            try {
                const response = await server.post('/api/v1/article', {
                    title: this.title,
                    content: this.content
                })
                if (response.data.status === 201) {
                    this.$router.push('/blog')
                    alert('글이 성공적으로 등록되었습니다.')
                } else {
                    alert('글 등록에 실패하였습니다.')
                }
            } catch (error) {
                console.error("글 등록에 실패하였습니다: ", error)
                alert('글 등록에 실패하였습니다.')
            }
        },
        cancel() {
            // 취소 로직
        },


    },


}
</script>

<style scoped>
::v-deep .ql-editor {
    min-height: 500px;
    background-color: white;
}

::v-deep .ql-toolbar {
    background-color: rgb(188, 234, 238);
}
</style>
