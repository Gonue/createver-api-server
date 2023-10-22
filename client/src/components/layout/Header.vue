<template>
    <div>
        <header class="p-3 text-bg">
            <div class="container">
                <div class="row">
                    <div class="col d-flex flex-wrap align-items-center justify-content-between">
                        <!-- 햄버거 메뉴 (모바일에서만 보임) -->
                        <button class="btn d-lg-none" type="button" data-bs-toggle="offcanvas"
                            data-bs-target="#offcanvasExample" aria-controls="offcanvasExample">
                            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor"
                                class="bi bi-list text-white" viewBox="0 0 16 16">
                                <path fill-rule="evenodd"
                                    d="M2.5 12a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5z" />
                            </svg>
                        </button>

                        <!-- Offcanvas 컴포넌트 -->
                        <div class="offcanvas offcanvas-start bg-dark" tabindex="-2" id="offcanvasExample"
                            aria-labelledby="offcanvasExampleLabel">
                            <div class="offcanvas-header">
                                <h5 id="offcanvasExampleLabel text-white"></h5>
                                <button type="button" class="btn-close text-reset bg-white" data-bs-dismiss="offcanvas"
                                    aria-label="Close"></button>
                            </div>
                            <div class="offcanvas-body">
                                <!-- 컨텐츠 -->
                                <a href="/" class="nav-link text-white under-line">
                                    <h2>Home</h2>
                                </a>
                                <a class="nav-link text-white under-line">
                                    <h3>Image</h3>
                                    <router-link to="/image/search" class="custom-link">search</router-link>
                                </a>
                                <a href="/community" class="nav-link text-white under-line">
                                    <h2>Community</h2>
                                </a>
                            </div>
                        </div>

                        <!-- 로고 -->
                        <div class="logo-container d-flex justify-content-center align-items-center">
                            <a href="/" id="logo"
                                class=" d-flex align-items-center justify-content-center mb-lg-0 link-body-emphasis text-decoration-none text-white fw-bold">
                                <img src="../../assets/mainlogo2.png" width="130" height="40">
                            </a>
                        </div>


                        <!-- 데스크톱 메뉴 -->
                        <ul id="con" class="nav d-none d-lg-flex ms-lg-auto mb-2 mb-md-0">
                            <li><a href="/blog" class="nav-link text-white">Blog</a></li>
                            <li><a href="/image/search" class="nav-link text-white">Search</a></li>
                            <li><a href="/" class="nav-link text-white">Music</a></li>
                            <li><a href="/" class="nav-link text-white">FAQ</a></li>
                            <li><a href="/community" class="nav-link text-white">Community</a></li>
                        </ul>

                        <div class="d-flex align-items-center ml-lg-auto">
                            <div v-if="!token" class="d-lg-block">
                                <a class="custom-btn nav-link text-white fw-bold" href="/login">Log-In</a>
                            </div>
                            <div v-if="token" class="fw-bold mx-3 text-white">{{ nickName }}</div>
                            <div v-if="token" class="dropdown text-end">
                                <a href="#" class="d-block link-body-emphasis text-decoration-none"
                                    data-bs-toggle="dropdown" aria-expanded="false">
                                    <img :src="profileImage" alt="profile" width="32" height="32" class="rounded-circle">
                                </a>
                                <ul class="dropdown-menu dropdown-menu-dark text-small">
                                    <li><a class="dropdown-item custom-dropdown-item" href="/mypage">My Page</a></li>
                                    <li>
                                        <hr class="dropdown-divider">
                                    </li>
                                    <li><a class="dropdown-item custom-dropdown-item" @click="logout" href="#">Sign out</a>
                                    </li>
                                </ul>
                            </div>
                        </div>


                        <!-- 모바일 메뉴 -->
                        <div class="collapse navbar-collapse d-lg-none" id="navbarNav">
                            <ul class="navbar-nav ml-auto">
                                <li class="nav-item"><a class="nav-link" href="/image">Image</a></li>
                                <li class="nav-item"><a class="nav-link" href="/community">Community</a></li>

                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    </div>
</template>

<script>
export default {
    name: "headerView",

    methods: {
        logout() {
            this.$store.dispatch('logout');
        }
    },

    computed: {
        nickName() {
            return this.$store.state.nickName;
        },
        profileImage() {
            return this.$store.state.profileImage;
        },
        token() {
            return this.$store.state.token;
        }
    }
}
</script>

<style scoped>
body {
    font-family: 'Courier New', Courier, monospace;
}

.text-bg {
    background-color: #141827;
    border-bottom: 0.1px solid #424242;
    color: white;
    /* 2px 높이의 흰색 밑줄 */

}

.custom-btn {
    background-color: #46774fb3;
    color: white;
    padding: 10px;
    border-radius: 7px;
    text-decoration: none;
    transition: background-color 0.3s ease;
}

.custom-btn:hover {
    background-color: #515b65;
}

#logo img {
    margin-left: 20px;
}

.offcanvas {
    width: 250px !important;
}

.under-line {
    border-bottom: 1px solid #515b65;
    padding-bottom: 10px;
    margin-bottom: 10px;
}

.offcanvas-body a:last-child {
    border-bottom: none;
}

.nav-item.dropdown .dropdown-toggle::after {
    display: none;
}

.custom-dropdown-item:focus,
.custom-dropdown-item:hover {
    background-color: #515b65;
    color: white;
}

.custom-link {
    color: white;
    /* 글자색을 하얀색으로 */
    text-decoration: none;
    /* 밑줄 제거 */
}

.custom-link:hover,
.custom-link:focus {
    color: white;
    /* 마우스 호버나 포커스 시에도 하얀색 유지 */
}


@media (max-width: 320px) {
    .logo-container {
        position: absolute;
        left: 45%;
        transform: translateX(-50%);
    }
}</style>