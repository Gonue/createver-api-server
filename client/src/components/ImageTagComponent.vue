<template>
    <div>
        <div ref="reverseTagContainer" class="tags mb-2">
            <button v-for="tag in infiniteTags" :key="tag.tagId" class="tag-button" @click="sendTagToParent(tag.name)">
                {{ tag.name }}
            </button>
        </div>
        <div ref="tagContainer" class="tags mb-3">
            <button v-for="tag in infiniteTags" :key="tag.tagId" class="tag-button" @click="sendTagToParent(tag.name)">
                {{ tag.name }}
            </button>
        </div>
    </div>
</template>
  
<script>
import server from '@/axios-config';

export default {
    data() {
        return {
            tags: [],
            infiniteTags: [],
            scrollSpeed: 0.5,
        };
    },
    async mounted() {
        try {
            const response = await server.get('/api/v1/image/tags?size=40');
            if (response.status === 200 && response.data.result.content) {
                this.tags = response.data.result.content;
                this.infiniteTags = [...this.tags, ...this.tags];
                this.startScrolling();
                this.startReverseScrolling();
            }
        } catch (error) {
            console.error('API 호출 중 오류 발생:', error);
        }
    },
    methods: {
        startScrolling() {
            const container = this.$refs.tagContainer;
            setInterval(() => {
                if (container.scrollLeft >= container.scrollWidth / 2) {
                    container.scrollLeft = 0;
                } else {
                    container.scrollLeft += this.scrollSpeed;
                }
            }, 20);
        },
        startReverseScrolling() {
            const container = this.$refs.reverseTagContainer;
            setInterval(() => {
                if (container.scrollLeft <= 0) {
                    container.scrollLeft = container.scrollWidth / 2;
                } else {
                    container.scrollLeft -= this.scrollSpeed;
                }
            }, 20);
        },
        sendTagToParent(tagName) {
            this.$emit('tagSelected', tagName);
        }
    }
};
</script>
  
<style>
.tags {
    display: flex;
    flex-wrap: nowrap;
    overflow-x: hidden;
}

.tag-button {
    margin-right: 10px;
    font-size: 13px;
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
    background: rgba(105, 98, 144, 0.6);
}

.tag-button:hover {
    background-color: #b7c6cd;
}

</style>
