<template>
    <div class="container-fluid">
        <!-- Main Content -->

        <div class="row justify-content-center align-items-center">
            <img src="../assets/main.jpeg" class="mainImg mt-5">
        </div>

        <div class="row justify-content-center align-items-center vh">
            <div class="col-10 col-md-8 col-lg-6 text-center">

                <div class="input-group mb-3">
                    <textarea class="form-control ar me-2" placeholder="What do you want to generate?" style="background-color: #3a3d47; color: #fff;"></textarea>
                    <button class="btn btn-lg fw-bold" style="background-color: #4266db;">
                        <span><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-brush" viewBox="0 0 16 16">
                                <path
                                    d="M15.825.12a.5.5 0 0 1 .132.584c-1.53 3.43-4.743 8.17-7.095 10.64a6.067 6.067 0 0 1-2.373 1.534c-.018.227-.06.538-.16.868-.201.659-.667 1.479-1.708 1.74a8.118 8.118 0 0 1-3.078.132 3.659 3.659 0 0 1-.562-.135 1.382 1.382 0 0 1-.466-.247.714.714 0 0 1-.204-.288.622.622 0 0 1 .004-.443c.095-.245.316-.38.461-.452.394-.197.625-.453.867-.826.095-.144.184-.297.287-.472l.117-.198c.151-.255.326-.54.546-.848.528-.739 1.201-.925 1.746-.896.126.007.243.025.348.048.062-.172.142-.38.238-.608.261-.619.658-1.419 1.187-2.069 2.176-2.67 6.18-6.206 9.117-8.104a.5.5 0 0 1 .596.04zM4.705 11.912a1.23 1.23 0 0 0-.419-.1c-.246-.013-.573.05-.879.479-.197.275-.355.532-.5.777l-.105.177c-.106.181-.213.362-.32.528a3.39 3.39 0 0 1-.76.861c.69.112 1.736.111 2.657-.12.559-.139.843-.569.993-1.06a3.122 3.122 0 0 0 .126-.75l-.793-.792zm1.44.026c.12-.04.277-.1.458-.183a5.068 5.068 0 0 0 1.535-1.1c1.9-1.996 4.412-5.57 6.052-8.631-2.59 1.927-5.566 4.66-7.302 6.792-.442.543-.795 1.243-1.042 1.826-.121.288-.214.54-.275.72v.001l.575.575zm-4.973 3.04.007-.005a.031.031 0 0 1-.007.004zm3.582-3.043.002.001h-.002z" />
                            </svg> Draw</span>
                    </button>
                </div>

                <div class="button-container justify-content-center">

                    <button type="button" class="custom-btn btn-space" :class="{ 'selected-btn': selectedButton === 1 }"
                        @click="selectButton(1)">
                        <img src="../assets/art.jpeg" alt="Style example" class="rounded-md custom-img">
                        <a class="custom-text">Art</a>
                    </button>
                    <button type="button" class="custom-btn btn-space" :class="{ 'selected-btn': selectedButton === 2 }"
                        @click="selectButton(2)">
                        <img src="../assets/draw.jpeg" alt="Style example" class="rounded-md custom-img">
                        <a class="custom-text">Drawing</a>
                    </button> <button type="button" class="custom-btn btn-space"
                        :class="{ 'selected-btn': selectedButton === 3 }" @click="selectButton(3)">
                        <img src="../assets/photo.jpeg" alt="Style example" class="rounded-md custom-img">
                        <a class="custom-text">Photo</a>
                    </button> <button type="button" class="custom-btn btn-space"
                        :class="{ 'selected-btn': selectedButton === 4 }" @click="selectButton(4)">
                        <img src="../assets/none.jpeg" alt="Style example" class="rounded-md custom-img">
                        <a class="custom-text">None</a>
                    </button>
                </div>


                <div>
                    <div class="mt-4">
                        <img id="generatedImage" src="" alt="Generated Image" class="img-fluid">
                    </div>
                    <button type="button" class="btn btn-primary btn-lg btn-space">Large button</button>
                    <button type="button" class="btn btn-secondary btn-lg btn-space">Large button</button>
                </div>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    data() {
        return {
            selectedButton: 1, // 선택된 버튼의 ID를 저장, 초기 값은 1로 설정
        };
    },
    methods: {
        selectButton(id) {
            this.selectedButton = id; // 선택된 버튼의 ID를 저장
        },
    },
};
</script>



<style scoped>

textarea, a, span{
	font-family: 'Courier New', Courier, monospace;
}

.vh {
    min-height: 45vh;
}

.btn-space {
    margin-right: 30px;
}

.btn-lg {
    width: 100%;
}

textarea.form-control {
    resize: none;
}

@media (max-width: 768px) {
    .btn-lg {
        width: auto;
    }

    .custom-btn {
        flex-direction: column;
        align-items: center;
    }

    .custom-text {
        margin-left: 0;
        margin-right: 0;
    }

    .btn-space {
        margin-right: 15px;
        margin-bottom: 15px;
    }

    .button-container {
        flex-wrap: wrap;
        justify-content: space-around;
    }
}

.btn-lg {
    width: auto;
}

.container-fluid {
    background-color: #141827;
    color: white;
    min-height: 100vh;
    padding: 0;
}

.mainImg {
    width: 350px;
    height: 350px;
    border-radius: 4em;
}

.custom-btn {
    display: flex;
    align-items: center;
    background-color: rgb(34 40 57);
    border: 10px #313437;
    border-radius: 10px;
    position: relative;
    border: 3px solid transparent;

}

.custom-img {
    height: 55px;
    width: 65px;
    border-radius: 15px;

}

.custom-text {
    color: rgb(95, 120, 212);
    margin-left: 20px;
    margin-right: 20px;
    text-decoration: none;
}

.button-container {
    display: flex;
    align-items: center;
}

.selected-btn {
    border: 3px solid #3358bd;
}

.ar::placeholder {
	color: white !important;
	opacity: 1;
}

.input-group > textarea.form-control.ar {
    border-top-right-radius: 5px !important;
    border-bottom-right-radius: 5px !important;
}

.input-group > .btn {
    border-top-left-radius: 5px !important;
    border-bottom-left-radius: 5px !important;
}

</style>