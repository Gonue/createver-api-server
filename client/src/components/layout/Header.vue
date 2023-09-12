<template>
    <div>

        <body>
            <main>
                <header class="p-3 text-bg-dark">
                    <div class="container">
                        <div class="row">
                            <div
                                class="col d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
                                <a href="/" id="logo"
                                    class="col-3 d-flex align-items-center mb-2 mb-lg-0 link-body-emphasis text-decoration-none text-white">
                                    LOGO
                                </a>
                                <ul class="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
                                    <li><a href="#" class="nav-link px-2 text-white">Home</a></li>
                                    <li><a href="#" class="nav-link px-5 text-white">Test</a></li>
                                </ul>
                                    <div v-if="token" class="mx-3">{{ nickName }}님</div>
                                    <div v-if="token" class="dropdown text-end">
                                        <a href="#" class="d-block link-body-emphasis text-decoration-none dropdown-toggle"
                                            data-bs-toggle="dropdown" aria-expanded="false">
                                            <img :src="profileImage" alt="profile" width="32" height="32"
                                                class="rounded-circle">
                                        </a>
                                        <ul class="dropdown-menu text-small">
                                            <li><a class="dropdown-item" href="#">마이페이지</a></li>
                                            <li>
                                                <hr class="dropdown-divider">
                                            </li>
                                            <li><a class="dropdown-item" @click="logout" href="#">Sign out</a></li>
                                        </ul>
                                </div>
                                <div v-else>
                                    <a class="text-white" href="/login">로그인</a>
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
    name: "header",

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

<style scoped></style>