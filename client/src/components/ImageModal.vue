<template>
    <div v-if="selectedImageInfo" class="modal" @click.self="closeModal">
        <div class="modal-content">
            <h1>Selected Image</h1>
            <img :src="selectedImageInfo.s3Url" />
            <div>
                <p><strong>Prompt:</strong> {{ selectedImageInfo }}</p>
                <p><strong>Prompt:</strong> {{ selectedImageInfo.prompt }}</p>
                <p><strong>Option:</strong> {{ selectedImageInfo.option }}</p>
                <p><strong>Created At:</strong> {{ selectedImageInfo.createdAt }}</p>
            </div>
            <button @click="closeModal">닫기</button>
        </div>
    </div>
</template>
  
<script>
export default {
    props: ['selectedImageInfo'],
    mounted() {
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

        }
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
    border-radius: 8px;
    width: 80%;
    max-width: 500px;
}
</style>
  