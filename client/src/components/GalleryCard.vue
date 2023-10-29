<template>
    <div class="p">
        <div class="gallery-card">
            <div class="image-box" @click="handleClick">
                <div class="time-box">
                    <button class="custom-btn-top" v-if="showTime"> {{ formattedDate(image.createdAt)}} </button>  <!-- 조건부 렌더링 -->
                    <button class="custom-btn-top"> {{ optionText }}</button>

                </div>
                <img :src="image.storageUrl" :alt="'Image ' + image.galleryId" @load="$emit('image-loaded')" />
                <div class="text-box">
                    <h3>{{ image.prompt }}</h3>

                    <div class="icon-box">
                        <button class="custom-btn-bottom"> <i class="bi bi-hand-thumbs-up me-1"></i> --</button>
                        <button class="custom-btn-bottom"> <i class="bi bi-chat me-1"></i> --</button>
                        <button class="custom-btn-bottom"> <i class="bi bi-download me-1"></i> --</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</template>

<script>


export default {
    props: {
        image: {
            type: Object,
            required: true
        },
        showTime: {
            type: Boolean,
            default: false,
        },
    },
    methods: {
        formattedDate(dateStr) {
            const now = new Date();
            const date = new Date(dateStr);
            const diffInSeconds = (now - date) / 1000;
            const diffInMinutes = diffInSeconds / 60;
            const diffInHours = diffInMinutes / 60;
            const diffInDays = diffInHours / 24;
            const diffInMonths = diffInDays / 30;
            const diffInYears = diffInDays / 365;

            if (diffInYears >= 1) {
                return `${Math.floor(diffInYears)} years`;
            } else if (diffInMonths >= 1) {
                return `${Math.floor(diffInMonths)} month`;
            } else if (diffInDays >= 1) {
                return `${Math.floor(diffInDays)} days`;
            } else if (diffInHours >= 1) {
                return `${Math.floor(diffInHours)} hours`;
            } else if (diffInMinutes >= 1) {
                return `${Math.floor(diffInMinutes)} mins`;
            } else {
                return 'Just now';
            }
        },

        handleClick() {
            this.$emit('image-selected', this.image);
        },
    },


    computed: {
        optionText() {
            switch (this.image.option) {
                case 1: return "Art";
                case 2: return "Drawing";
                case 3: return "Photo";
                case 4: return "None";
                case 5: return "SD-Pro"
                default: return "None";
            }
        },

    }
}
</script>

<style scoped>
.gallery-card {
    cursor: pointer;
    border: 0.5px solid #ccc;
    border-radius: 5px;
    max-height: auto;
    min-width: 320px;
    max-width: 300px;
    margin: 10px;
    overflow: hidden;
    position: relative;
}


.image-box img {
    width: 100%;
    height: auto;
    display: block;
}

.text-box {
    position: absolute;
    bottom: 0;
    background: linear-gradient(45deg, rgba(57, 59, 67, 0.8) 0%, rgba(53, 61, 99, 0) 100%);
    width: 100%;
    padding: 10px;
}

.text-box h3 {
    margin: 0 0 10px;
    font-size: 15px;
}

.text-box p {
    margin: 0;
    font-size: 10px;
    color: white;
}

.icon-box {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    position: relative;
}

.custom-btn-bottom {
    margin-right: 10px;
    font-size: 10px;
    border-radius: 4px;
    font-weight: 700;
    border: 1px solid transparent;
    background: rgba(210, 215, 210, 0.2);
    color: white;
}

.custom-btn-top {
    margin-right: 10px;
    font-size: 10px;
    padding: 0px 8px;
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    text-transform: uppercase;
    border-radius: 4px;
    font-weight: 700;
    letter-spacing: 0.25px;
    border: 1px solid transparent;
    color: white;
    box-shadow: rgba(37, 38, 43, 0.2) 1px 2px 3px -1px;
    background: rgba(1, 152, 169, 0.6);
}

.time-box {
    position: absolute;
    margin-left: 5px;
    top: 3px;
    border-radius: 5px;
}
</style>
