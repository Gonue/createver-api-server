<template>
    <div>
        <body>
            <main>
                <header class="p-3 text-bg">
                    <div class="container">
                        <div class="row">
                            <div
                                class="col d-flex flex-wrap align-items-center justify-content-center justify-content-start">
                                <a href="/" id="logo" class="col-3 d-flex align-items-center justify-content-center mb-lg-0 link-body-emphasis text-decoration-none text-white fw-bold">
                                    <img src="https://github.com/Gonue/mine/assets/109960034/d75f1bc8-cf6b-4aee-8b7a-262d2b22e21f" width="32" height="32">Createver
                                </a>
                                <ul id="con" class="nav col-12 col-lg-auto ms-lg-auto justify-content-lg-end mb-2 mb-md-0">
                                    <li><a href="/" class="nav-link text-white">Home</a></li>
                                    <li><a href="/image" class="nav-link text-white">Image</a></li>
                                    <li><a href="/community" class="nav-link text-white">Community</a></li>
                                    <li><a href="/community" class="nav-link text-white">Community</a></li>
                                    <li v-if="!token">
                                        <a class="custom-btn nav-link text-white fw-bold" href="/login">Log-In</a>
                                    </li>
                                </ul>
                                    <div v-if="token" class="fw-bold mx-3 text-white">{{ nickName }}</div>
                                    <div v-if="token" class="dropdown text-end">
                                        <a href="#" class="d-block link-body-emphasis text-decoration-none" data-bs-toggle="dropdown" aria-expanded="false">
                                            <img :src="profileImage" alt="profile" width="32" height="32"
                                                class="rounded-circle">
                                        </a>
                                        <ul class="dropdown-menu dropdown-menu-dark text-small">
                                            <li><a class="dropdown-item" href="/mypage">My Page</a></li>
                                            <li>
                                                <hr class="dropdown-divider">
                                            </li>
                                            <li><a class="dropdown-item" @click="logout" href="#">Sign out</a></li>
                                        </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>
            </main>
        </body>
    </div>
</template>

<script>
export default {
    name: "headerView",

    methods: {
        logout(){
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
    font-family:'Courier New', Courier, monospace;
}
.text-bg{
    background-color: #141827;
    border-bottom: 0.1px solid #424242;  /* 2px 높이의 흰색 밑줄 */

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
    margin-right: 10px;
}
</style>