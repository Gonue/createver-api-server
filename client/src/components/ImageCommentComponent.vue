<template>
    <div class="comment-section">
        <h2 class="text-color">Comment - {{ comments.length }}</h2>
        <div class="comment-box text-bold">


            <div class="no-login" v-if="!$store.state.token">You must <a class="no-a" href="/login">sign in</a> to add a comment</div>

            <div v-if="$store.state.token" class="comment-input-section">
                <textarea class="comment-input" v-model="newComment" placeholder="Add a comment..."
                @keydown="checkForEnter"></textarea>
            </div>

            <div class="mt-3 mb-3" v-for="comment in comments" :key="comment.imageCommentId">
                <div class="comment-item">
                    <img :src="comment.member.profileImage" alt="프로필 이미지" class="profile-image" />
                    <span class="nickname"> {{ comment.member.nickName }}</span>
                    <span class="time"> - {{ formatDate(comment.createdAt) }}</span>

                    <span v-if="comment.member.email === $store.state.email" class="comment-actions">
                        <a @click="editComment(comment)" class="edit-button text-success me-2"><i
                                class="bi bi-pencil-square"></i></a>
                        <a @click="deleteComment(comment.imageCommentId)" class="delete-button text-danger"> <i
                                class="bi bi-trash"></i></a>
                    </span>

                </div>
                <div v-if="editingCommentId === comment.imageCommentId">
                    <textarea class="update-input" v-model="editedComment"></textarea>
                    <button class="btn btn-sm btn-success me-2" @click="saveEditedComment">Save</button>
                    <button class="btn btn-sm btn-danger" @click="cancelEditing">Cancel</button>
                </div>
                <div v-else class="comment-content">{{ comment.content }}</div>
            </div>
        </div>
    </div>
</template>

<script>
import server from '@/axios-config';


export default {
    props: ['localSelectedImageInfo'],
    data() {
        return {
            comments: [],
            newComment: '',
            editingCommentId: null,
            editedComment: ''
        };
    },
    async mounted() {
        await this.fetchComments();
    },
    watch: {
        'localSelectedImageInfo.galleryId': {
            immediate: true,
            handler: 'fetchComments',
        }
    },
    methods: {
        checkForEnter(event) {
            if (event.key === 'Enter' && !event.isComposing) {
                event.preventDefault();
                this.submitComment();
            }
        },
        async fetchComments() {
            const galleryId = this.localSelectedImageInfo.galleryId;
            try {
                const response = await server.get(`/api/v1/image/${galleryId}/comments?sort=createdAt,desc`);
                if (response.data.status === 200) {
                    this.comments = response.data.result.content;
                } else {
                    console.error('API 호출 실패:', response.data.message);
                }
            } catch (error) {
                console.error('API 호출 중 에러 발생:', error);
            }
        },


        async submitComment() {
            if (this.newComment.trim() === '') {
                return;
            }

            const galleryId = this.localSelectedImageInfo.galleryId;

            try {
                const response = await server.post(`/api/v1/image/${galleryId}/comment`, {
                    content: this.newComment
                });

                if (response.data.status === 200) {
                    // 댓글 등록 성공, 댓글 목록을 다시 불러옴
                    await this.fetchComments();
                    this.newComment = ''; // 입력 필드 초기화
                } else {
                    console.error('댓글 등록 실패:', response.data.message);
                }
            } catch (error) {
                console.error('댓글 등록 중 에러 발생:', error);
            }
        },

        editComment(comment) {
            this.editingCommentId = comment.imageCommentId;
            this.editedComment = comment.content;
        },
        async saveEditedComment() {
            try {
                const response = await server.patch(`/api/v1/image/comment/${this.editingCommentId}`, {
                    content: this.editedComment
                });
                if (response.data.status === 200) {
                    await this.fetchComments();
                    this.editingCommentId = null;
                } else {
                    console.error('댓글 수정 실패:', response.data.message);
                }
            } catch (error) {
                console.error('댓글 수정 중 에러 발생:', error);
            }
        },
        cancelEditing() {
            this.editingCommentId = null;
        },

        async deleteComment(imageCommentId) {
            try {
                const response = await server.delete(`/api/v1/image/comment/${imageCommentId}`);
                if (response.data.status === 200) {
                    await this.fetchComments();
                } else {
                    console.error('댓글 삭제 실패:', response.data.message);
                }
            } catch (error) {
                console.error('댓글 삭제 중 에러 발생:', error);
            }
        },
        formatDate(dateString) {
            const date = new Date(dateString);
            const year = String(date.getFullYear()).slice(-2);
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hour = String(date.getHours()).padStart(2, '0');
            const minute = String(date.getMinutes()).padStart(2, '0');
            return `${year}.${month}.${day} - ${hour}:${minute}`;
        },
    }
}
</script>

<style scoped>
.comment-section {
    margin-top: 30px;
}

.text-color {
    font-size: 18px;
    color: #8f94a3;
    text-align: left;
    margin-left: 10px;
}

.comment-box {
    word-wrap: break-word;
    text-align: left;
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 10px;
    background-color: #222839;
    color: white;
}

.comment-item {
    display: flex;
    align-items: center;
    flex-wrap: no-wrap;
}

.profile-image {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    margin-right: 10px;
}

.nickname,
.time {
    margin-right: 10px;
    white-space: nowrap;
    font-size: 13px;
}

.time {
    color: #8f94a3;
}

.comment-content {
    margin: 10px;
    font-size: 14px;
}

.nickname {
    font-weight: bold;
}

.no-login {
    text-align: center;
    padding: 12px 16px;
    border: 1px solid transparent;
    background-color: rgba(11, 96, 176, 0.639);
    color: rgb(232, 238, 244);
    margin-top: 10px;
    margin-bottom: 10px;
}

.no-a {
  text-decoration: none;
  color: rgb(10, 201, 147);
}
.comment-input-section {
    display: flex;
    flex-direction: column;
}

.comment-input {
    width: 100%;
    height: 50px;
    padding: 10px;
    border-radius: 8px;
    background-color: #141720;
    color: white;
    resize: none;
    margin-bottom: 10px;
}

.update-input {
    width: 100%;
    height: 50px;
    padding: 10px;
    border-radius: 8px;
    background-color: #141720;
    color: white;
    resize: none;
    margin-top: 10px;
    margin-bottom: 10px;
}
</style>
