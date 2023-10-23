<template>
    <main>
        <div class="container text-white">
            <h1 class="text-center pricing-table-subtitle t1 text-bold">Choose your plan</h1>
            <h6 class="text-center pricing-table-title t2">Get the best of Createver, for personal and commercial use.</h6>
            <div class="row justify-content-center mb-5">


                <div class="col-md-4" v-for="(plan, index) in plans" :key="index">
                    <div :class="['card', 'pricing-card', plan.cardClass]">
                        <div class="card-image">
                            <img v-for="(image, i) in plan.images" :key="i" :src="image" alt="Plan Image">
                        </div>

                        <div class="card-body">
                            <h2 class="pricing-plan-title t1">{{ plan.title }}</h2>
                            <h3 class="pricing-plan-cost ml-auto text-dark">{{ plan.cost }}</h3>
                            <ul class="pricing-plan-features">
                                <li v-for="(feature, i) in plan.features" :key="i">
                                    {{ feature }}
                                </li>
                            </ul>
                            <a @click="goToLink(plan)" class="btn pricing-plan-purchase-btn">{{ plan.buttonText }}</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</template>
  
<script>
export default {
    data() {
        return {
            plans: [
                {
                    title: 'Basic',
                    cost: 'FREE',
                    cardClass: 'pricing-plan-basic',
                    iconClass: 'mdi-cube-outline',
                    features: ['- Unlimited images', '- Default image Models', '- No private images'],
                    buttonText: 'Start',
                    images: [require('@/assets/art.jpeg'), require('@/assets/photo.jpeg'), require('@/assets/draw.jpeg'), require('@/assets/none.jpeg')],
                    link: '/'
                },
                {
                    title: 'Pro',
                    cost: '$11',
                    cardClass: 'pricing-card-highlighted pricing-plan-pro',
                    iconClass: 'mdi-trophy',
                    features: ['- Unlimited images', '- Stable-Diffusion based image model', '- Checkpoint, Lora, VAE, Embedding Full Access', '- Private images'],
                    buttonText: 'Purchase',
                    images: [require('@/assets/main.png'), require('@/assets/001.jpeg'), require('@/assets/002.jpeg'), require('@/assets/003.png')],
                },
            ]
        }
    },
    methods: {
        goToLink(plan) {
            const isLoggedIn = this.$store.state.token !== '';
            if (isLoggedIn) {
                window.location.href = plan.link;
            } else {
                window.location.href = '/login';
            }
        }
    },
}
</script>
  
<style scoped>
/* 기본 스타일 */
.container {
    padding-top: 50px;
    text-align: center;
}

.pricing-table-subtitle,
.pricing-table-title {
    margin-bottom: 30px;
}

.card {
    border: none;
    border-radius: 10px;
    box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
    transform: translateY(-10px);
    box-shadow: 0px 12px 18px rgba(0, 0, 0, 0.2);
}

.pricing-card-highlighted {
    background: linear-gradient(135deg, #3bc366 0%, #7262fd 100%);
    color: white;
}


.card:hover .pricing-plan-icon {
    transform: scale(1.1);
}

.pricing-plan-purchase-btn {
    background-color: #5e7c7a;
    color: white;
    border-radius: 5px;
    padding: 10px 20px;
    transition: background-color 0.3s ease, transform 0.3s ease;
}

.pricing-plan-purchase-btn:hover {
    background-color: #6bab3b;
    transform: scale(1.1);
}

.pricing-plan-basic {
    background: linear-gradient(135deg, #489187 0%, #6c5833 100%);
    color: white;
}

.card-image img {
    padding: 10px;
    border-radius: 20px;
    width: 50%;
    height: 200px
}

.pricing-plan-features {
    list-style-type: none;
    padding-left: 0;
}

.pricing-plan-features li {
    margin: 5px 0;
    margin-left: 40px;
    padding: 5px;
    font-weight: bold;
    text-align: left;
}

main {
    min-height: 130vh;
}

.t1 {
    font-family: 'Courier New', Courier, monospace;
    font-weight: bold
}

.t2 {
    color: #8f94a3
}
</style>
  
  