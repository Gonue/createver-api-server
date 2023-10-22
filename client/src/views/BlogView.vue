<template>
    <div class="container vh">

        <div class="row">

            <div class="col-1"></div>
            <div class="col-10 mb-5">
                <div class="d-flex justify-content-end mb-3" v-if="$store.state.roles.includes('ADMIN')">
                    <router-link to="/blog/write" class="btn btn-success text-white">Write</router-link>
                </div>
                <router-link :to="`/blog/${articles[0]?.articleId}`">
                    <div class="card mb-3 mx-auto" style="max-width: 1100px;">
                        <div class="row g-0">
                            <div class="col-md-4">
                                <img :src="getFirstImageSrc(articles[0]?.content)" class="img-fluid rounded-start"
                                    alt="...">
                            </div>
                            <div class="col-md-8">
                                <div class="card-body" style="padding: 35px;">
                                    <p class="card-text col-10" style="font-size: 15px;">
                                        <span class="date fw">{{ formatDate(articles[0]?.createdAt) }}</span>
                                        <span class="separator"></span>
                                        <span class="author fw">✏️ By {{ articles[0]?.member?.nickName }}</span>
                                    </p>
                                    <h5 class="card-title mt-4" style="font-size: 25px;">{{ articles[0]?.title }}</h5>
                                    <p class="card-text" style="font-size: 20px;" v-html="extractH2(articles[0]?.content)"></p>
                                    <button class="btn custom-btn mt-5">ReadMore</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </router-link>
            </div>
        </div>

        <div class="row">
            <div class="col-1"></div>
            <div class="col-10 mb-5">
                <div class="row row-cols-1 row-cols-md-3 g-4">
                    <div class="col" v-for="article in articles.slice(1)" :key="article.articleId">
                        <router-link :to="`/blog/${article.articleId}`">

                            <div class="card custom-card">
                                <img :src="getFirstImageSrc(article.content)" class="card-img-top" alt="...">
                                <div class="card-body">
                                    <p class="card-text">
                                        <span class="date fw">{{ formatDate(article.createdAt) }}</span>
                                        <span class="separator"></span>
                                        <span class="author fw">By {{ article.member.nickName }}</span>
                                    </p>
                                    <h5 class="card-title">{{ article.title }}</h5>
                                    <p class="card-text" v-html="extractH2(article.content)"></p>
                                    <button class="btn custom-btn">ReadMore</button>

                                </div>
                            </div>
                        </router-link>

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
            articles: []
        };
    },

    async mounted() {
        try {
            const response = await server.get('/api/v1/article?sort=createdAt,desc');
            if (response.data.status === 200) {
                this.articles = response.data.result.content;
            } else {
                console.error('데이터를 불러오는데 실패했습니다.');
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
        extractH2(htmlString) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(htmlString, 'text/html');
            const h2 = doc.querySelector('h2');
            return h2 ? h2.innerHTML : '';
        },
        getFirstImageSrc(content) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(content, 'text/html');
            const img = doc.querySelector('img');
            return img ? img.src : require('@/assets/art.jpeg'); // null 혹은 기본 이미지 URL을 반환할 수 있습니다.
        },
    }
}
</script>

<style scoped>
.vh {
    margin-top: 100px;
}

.card {
    background-color: #222839;
    border: solid 0.1px #41495e;
    color: white;
    border-radius: 20px;
    padding: 20px;
}

.card-body p.card-text {
    color: #8F94A3;
    display: flex;
    align-items: center;
}

img {
    border-radius: 10px;
}

.separator {
    flex-grow: 1;
    height: 1px;
    background-color: #5e6370;
    margin: 0 10px;
}

a {
    text-decoration: none;
    color: inherit;
}

.fw {
    font-weight: 500;
}

.custom-card {
    min-height: 500px;
}

.custom-btn {
    background-color: #475b70;
    color: white;
    border-radius: 12px;
    padding: 10px 20px;
    font-size: 16px;
    transition: all 0.3s ease;
}

.custom-btn:hover {
    background-color: #6599d1;
    transform: scale(1.05);
}
</style>