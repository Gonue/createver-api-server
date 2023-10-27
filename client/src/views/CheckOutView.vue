<template>
    <div class="container text-white">
        <h2 class="text-center mt-5">주문/결제</h2>

        <div class="row justify-content-center">

            <div class="col-6">
                <div class="form-group text-center">

                    <ImageMixer :originalImage="originalImage" />
                    <div class="mt-3">
                        <button type="button" class="btn btn-secondary" @click="decrementQuantity"
                            :disabled="quantity <= 1">-</button>
                        <span class="mx-3">수량: {{ quantity }}</span>
                        <button type="button" class="btn btn-secondary" @click="incrementQuantity">+</button>
                    </div>
                </div>
            </div>
            <div class="col-6">

                <div class="card-body">
                    <form @submit.prevent="submitForm">

                        <!-- 구매자 정보 -->
                        <div class="mt-5">
                            <h3 class="mt-3">구매자 정보</h3>
                            <div class="form-group">
                                <label for="buyerName">이름</label>
                                <input type="text" class="form-control" id="buyerName" v-model="buyer.name" placeholder="이름"
                                    required>
                            </div>
                            <div class="form-group">
                                <label for="buyerEmail">Email</label>
                                <input type="email" class="form-control" id="buyerEmail" v-model="email" readonly>
                            </div>
                            <div class="form-group">
                                <label for="buyerPhone">휴대폰번호</label>
                                <input type="tel" class="form-control" id="buyerPhone" v-model="buyer.phone"
                                    placeholder="010-xxxx-xxxx" required>
                            </div>
                        </div>
                        <!-- 받는사람 정보 -->

                        <div class="mt-5">
                            <h3 class="my-3">받는사람 정보</h3>
                            <div class="form-group">
                                <label for="recipientName">이름</label>
                                <input type="text" class="form-control" id="recipientName" v-model="recipient.name"
                                    placeholder="이름" required>
                            </div>
                            <div class="form-group">
                                <label for="recipientAddress" id="postcode">우편 번호</label>
                                <div class="input-group">
                                    <input type="text" class="form-control" v-model="recipient.postcode" placeholder="우편번호"
                                        id="postcode" readonly required>
                                    <input type="button" class="btn btn-warning" @click="execDaumPostcode()"
                                        value="우편번호 찾기">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="recipientAddress">배송주소</label>
                                <input type="text" class="form-control" v-model="recipient.address" id="recipientAddress"
                                    placeholder="배송주소" required readonly>
                            </div>

                            <div class="form-group">
                                <label for="recipientAddress1">상세 주소</label>
                                <input type="text" class="form-control" id="recipientAddress1" placeholder="상세주소" required>
                            </div>

                            <div class="form-group">
                                <label for="recipientPhone">휴대폰번호</label>
                                <input type="tel" class="form-control" id="recipientPhone" v-model="recipient.phone"
                                    placeholder="010-xxxx-xxxx" required>
                            </div>
                            <div class="form-group">
                                <label for="buyerInfo">요청 사항</label>
                                <input type="tel" class="form-control" v-model="buyer.info" id="buyerInfo"
                                    placeholder="요청 사항">
                            </div>
                        </div>

                        <!-- 결제 정보 -->
                        <div class="mt-5">
                            <h3 class="my-3">결제 정보</h3>
                            <div class="form-group">
                                <label for="totalPrice">총 상품 가격</label>

                                <div class="input-group">
                                    <input type="number" class="form-control" id="totalPrice" :value="totalPrice" readonly>
                                    <span class="btn btn-warning">원</span>
                                </div>

                            </div>
                            <!-- 결제 버튼 -->
                            <div class="mt-5 mb-5">
                                <h3 class="my-3">결제</h3>
                                <div class="text-align-center">
                                    <button class="custom-button">
                                        <img src="../assets/payment.png" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { mapState } from 'vuex';
import ImageMixer from '../components/ImageMixer.vue';

export default {
    components: {
        ImageMixer,
    },
    data() {
        return {
            buyer: {
                name: '',
                phone: ''
            },
            recipient: {
                name: '',
                postcode: '',
                address: '',
                extraAddress: '',
                info: '',
            },
            payment: {
                totalPrice: 0,
            },
            quantity: 1
        };
    },
    computed: {
        ...mapState({
            email: state => state.email,
            originalImage: state => state.image.originalImage
        }),
        totalPrice() {
            return this.quantity * 22000;  // 수량 * 단가
        },
    },
    methods: {
        incrementQuantity() {
            this.quantity++;
        },
        decrementQuantity() {
            if (this.quantity > 1) {
                this.quantity--;
            }
        },
        execDaumPostcode() {
            new window.daum.Postcode({
                oncomplete: (data) => {
                    if (this.recipient.extraAddress !== "") {
                        this.recipient.extraAddress = "";
                    }
                    if (data.userSelectedType === "R") {
                        // 사용자가 도로명 주소를 선택했을 경우
                        this.recipient.address = data.roadAddress;
                    } else {
                        // 사용자가 지번 주소를 선택했을 경우(J)
                        this.recipient.address = data.jibunAddress;
                    }

                    // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                    if (data.userSelectedType === "R") {
                        // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                        // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                        if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
                            this.recipient.extraAddress += data.bname;
                        }
                        // 건물명이 있고, 공동주택일 경우 추가한다.
                        if (data.buildingName !== "" && data.apartment === "Y") {
                            this.recipient.extraAddress +=
                                this.recipient.extraAddress !== ""
                                    ? `, ${data.buildingName}`
                                    : data.buildingName;
                        }
                        // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                        if (this.recipient.extraAddress !== "") {
                            this.recipient.extraAddress = `(${this.recipient.extraAddress})`;
                        }
                    } else {
                        this.recipient.extraAddress = "";
                    }
                    // 우편번호를 입력한다.
                    this.recipient.postcode = data.zonecode;
                },
            }).open();
        },
        submitForm() {
        }
    }
};
</script>

<style scoped>
.custom-button {
    border: none;
    background: none;
}

.custom-button img {
    width: 100px;
    height: auto;
}

.text-align-center {
    text-align: center;
}

.form-group {
    margin-top: 20px;
}
</style>
