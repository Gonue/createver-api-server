<template>
    <div v-if="selectedImageInfo" class="modal" @click.self="closeModal">
        <div class="modal-content">
            <button class="close-button" @click="closeModal">X</button>

            <div class="image-wrapper mt-5">
                <img :src="localSelectedImageInfo.storageUrl" />
            </div>

            <div class="d-flex justify-content-end mt-4">
                <!-- <button class="btn btn-success mx-1" type="button">Download</button> -->
                <button class="btn btn-success mx-1 mb-3" type="button" @click="downloadImage">Download</button>
            </div>

            <div>
                <ImageSearchComponentVue :localSelectedImageInfo="localSelectedImageInfo"
                    @image-selected="updateMainImage" />
            </div>

            <div class="info-section">
                <h2 class="text-color">Prompt</h2>
                <div class="prompt-box text-bold">
                    {{ localSelectedImageInfo.prompt }}
                </div>
                <h2 class="text-color">Detail</h2>

                <table class="detail-table">
                    <thead>
                        <tr>
                            <th>Image Size</th>
                            <th>Resolution</th>
                            <th>Model</th>
                            <th>Aspect Ratio</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td> - </td>
                            <td>Upscaled</td>
                            <td>{{ optionText }}</td>
                            <td> - </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div>
                <ImageMixer :originalImage="localSelectedImageInfo.storageUrl" />
            </div>
        </div>
    </div>
</template>
  
<script>
import axios from 'axios';
import ImageMixer from './ImageMixer.vue';
import ImageSearchComponentVue from './ImageSearchComponent.vue';

const server = axios.create({
    baseURL: process.env.SERVER_URL,
})

export default {
    data() {
        return {
            localSelectedImageInfo: null, // 로컬 상태 변수
        };
    },

    components: {
        ImageMixer,
        ImageSearchComponentVue
    },
    props: ['selectedImageInfo'],
    mounted() {
        this.localSelectedImageInfo = { ...this.selectedImageInfo };
        document.addEventListener('keydown', this.handleKeydown);

    },
    beforeUnmount() {
        document.removeEventListener('keydown', this.handleKeydown);
    },

    methods: {
        closeModal() {
            this.$emit('close');
        },
        handleKeydown(event) {
            if (event.key === 'Escape') {
                this.closeModal();
            }
        },
        async downloadImage() {
            try {
                const response = await server.get(`/api/v1/image/download/${this.localSelectedImageInfo.galleryId}`, {
                    responseType: 'blob'
                });

                const blob = new Blob([response.data], { type: response.headers['content-type'] });
                const link = document.createElement('a');
                link.href = URL.createObjectURL(blob);

                // 파일명은 Content-Disposition 헤더에서 추출하거나 클라이언트에서 지정한 형식을 사용
                const contentDisposition = response.headers['content-disposition'];
                let filename = `${this.selectedImageInfo.prompt}.png`;  // 기본 파일명

                if (contentDisposition) {
                    const match = contentDisposition.match(/filename="(.+)"/);
                    if (match && match[1]) {
                        filename = match[1];
                    }
                }

                link.setAttribute('download', filename);
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);

            } catch (error) {
                console.error("이미지 다운로드 중 오류 발생:", error);
            }
        },

        updateMainImage(newImage) {
            this.localSelectedImageInfo = { ...newImage };
        }
    },

    watch: {
        // props가 변경되면 로컬 상태 변수도 업데이트
        selectedImageInfo: {
            immediate: true,
            handler(newVal) {
                this.localSelectedImageInfo = { ...newVal };

            }
        }
    },

    computed: {
        optionText() {
            switch (this.localSelectedImageInfo.option) {
                case 1: return "Art";
                case 2: return "Drawing";
                case 3: return "Photo";
                case 4: return "None";
                case 5: return "SD-Pro"
                default: return "None";
            }
        },

    }
};
</script>
  
<style scoped>
.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.modal-content {
    background-color: #141827;
    padding: 20px;
    border-radius: 10px;
    border: 2px solid white;
    width: 90%;
    max-width: 600px;


    overflow-y: auto;
    /* 내용이 넘칠 경우 스크롤 */
    max-height: 95vh;
    position: relative;

}

.image-wrapper {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
}

.image-wrapper img {
    object-fit: cover;
    width: 400px;
    height: auto;
    max-width: 100%;
    max-height: 100%;
    border-radius: 10px;
    display: block;
}

.close-button {
    position: absolute;
    top: 10px;
    right: 10px;
    background: #50566c;
    border: none;
    border-radius: 5px;
    color: #d5cfcf;

    width: 40px;
    height: 40px;
    font-size: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.image-wrapper img {
    max-width: 100%;
    max-height: 100%;
    border-radius: 10px;
    display: block;
}

.info-section {
    margin-top: 20px;
}

.prompt-box {
    word-wrap: break-word;
    text-align: left;
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 10px;
    background-color: #222839;
}


.detail-table {
    background-color: #222839;
    border-radius: 10px;
    border-collapse: separate;
    width: 100%;
    text-align: center;
}

.text-color {
    font-size: 18px;
    color: #8f94a3;
    text-align: left;
    margin-left: 10px;
}

th {
    color: #8f94a3;
    font-size: 13px;
}

.prompt-box {
    color: white;
}

tr {
    color: white;
}

@media (max-width: 768px) {
    .image-wrapper {
        height: auto;
    }

    .image-wrapper img {
        max-width: 100%;
        height: auto;
    }
}
</style>