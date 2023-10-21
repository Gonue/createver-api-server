<template>
    <div class="container my-5">
        <div class="row">
            <div class="col-md-3">
                <div class="card">
                    <img :src="profileImage" class="card-img-top" alt="Profile Image">
                    <div class="card-body text-end">
                        <h5 class="card-title text-center"> {{ nickName }}</h5>
                        <p class="card-text text-center"> {{ email }}</p>
                        <button @click="deleteAccount" class="btn btn-danger mt-2">회원 탈퇴</button>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mt-3 text-end">
                <h4 class="text-center">프로필 수정</h4>
                <form @submit.prevent="updateProfileUI">
                    <div class="mb-3  text-start">
                        <label for="newNickName" class="form-label">새 닉네임</label>
                        <input type="text" class="form-control" id="newNickName" v-model="newNickName">
                    </div>
                    <div class="mb-3 text-start">
                        <label for="newProfileImageFile" class="form-label">새 프로필 이미지</label>
                        <input type="file" class="form-control" id="newProfileImageFile" ref="profileImageFile">
                    </div>
                    <button type="submit" class="btn btn-success">수정하기</button>
                </form>
            </div>
        </div>
        <MyPageGallery />
    </div>
</template>
  
<script>
import MyPageGallery from '@/components/MyPageGallery.vue';
import { mapState, mapActions } from 'vuex';
import server from '../axios-config';

export default {
    components: {
        MyPageGallery
    },

    computed: {
        ...mapState([
            'email',
            'nickName',
            'profileImage'
        ])
    },
    data() {
        return {
            newNickName: '',
            newProfileImage: '',
        };
    },

    methods: {
        ...mapActions([
            'updateProfile',
        ]),
        async updateProfileUI() {

            await this.uploadImage();

            const payload = {
                newNickName: this.newNickName,
                newProfileImage: this.newProfileImage,
            };

            await this.updateProfile(payload);

        },
        async deleteAccount() {
            // 회원 탈퇴 로직
        },

        async uploadImage() {
            const fileInput = this.$refs.profileImageFile;
            if (fileInput.files.length > 0) {
                const file = fileInput.files[0];
                const formData = new FormData();
                formData.append("images", file);

                const response = await server.post("/api/v1/image/upload", formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                if (response.data.status === 200) {
                    this.newProfileImage = response.data.result;
                }
            }
        }

    }
};
</script>
  

<style scoped>
.container {
    color: white;

}

.card {
    background-color: #24293d;
    border: none;
    color: white;
}

.card-img-top {
    border-radius: 200px !important;
}
</style>