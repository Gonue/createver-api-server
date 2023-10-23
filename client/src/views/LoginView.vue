<template>
	<div class="container-fluid">
		<div class="d-flex flex-column align-items-center justify-content-center">
			<div class="d-flex flex-column align-items-center justify-content-center">
				<h1 class="fw-bold mt-5">Welcome back</h1>
			</div>
			<div class="d-flex flex-column mt-5">
				<div class="d-flex flex-column align-items-center justify-content-center">
					<button class="btn d-flex align-items-center justify-content-center"
						style="background-color: rgb(184, 196, 196);"
						onclick="window.location.href='https://createver.site/oauth2/authorization/google'">
						<img src="../assets/googleLogo.svg"
							alt="Google logo" height="24" width="24" class="me-3">
						<span class="fw-bold ho">Continue with Google</span>
					</button>
				</div>
				<div class="d-flex align-items-center justify-content-between mt-4">
					<hr class="flex-grow-1">
					<span class="me-3 ms-3">Or</span>
					<hr class="flex-grow-1">
				</div>
				<form @submit.prevent="onSubmit" id="login-form" class="d-flex flex-column align-items-center mt-5">
					<div class="d-flex flex-column mb-4">
						<input v-model="email" type="email" class="ar form-control" placeholder="Email address" required>
						<div class="d-flex flex-column align-items-end mt-3">
							<input v-model="password" type="password" class="ar form-control" placeholder="Password"
								required>
							<div class="d-flex align-items-center justify-content-between mt-2 w-100">
								<span v-if="loginFailed" class="text-danger">Invalid input</span>
								<span v-else class="text-transparent"></span>
								<a href="/accountRecovery">Forgot password</a>
							</div>
						</div>
					</div>
					<button type="submit" class="fw-bold text-white btn d-flex align-items-center justify-content-center"
						style="background-color: rgb(82, 79, 87)">
						Log in
					</button>
					<a href="/register" class="mt-4">
						Don't have an account?
						<span class="fw-bold text-white">Sign up for free</span>
					</a>
				</form>
			</div>
		</div>
	</div>
</template>


<script>

export default {
	data() {
		return {
			email: '',
			password: '',
			loginFailed: false
		};
	},
	methods: {
		async onSubmit() {
			try {
				await this.$store.dispatch('login', {
					email: this.email,
					password: this.password
				});
				// 로그인 성공 시 에러 메시지 초기화
				this.loginFailed = false;

			} catch (error) {
				console.error('Login failed:', error);
				// 로그인 실패 시 에러 메시지 설정
				this.loginFailed = true;
			}
		}
	}
}
</script>


<style scoped>
.container-fluid {
	background-color: #141827;
	color: white;
	min-height: 100vh;
	padding: 0;
}

.ar {
	width: 350px !important;
	height: 50px !important;
	box-sizing: border-box;
	color: white !important;
	background-color: #141827 !important;
	/* 배경색 */

}

.ar::placeholder {
	color: white !important;
	opacity: 1;
}

h1 {
	font-family: 'Courier New', Courier, monospace;
}

.btn {
	width: 350px;
	height: 50px;
}

.btn:hover {
	color: black !important;
}

.ho:hover {
	color: white !important;
}

a {
	color: rgb(111, 120, 121) !important;
	text-decoration: none !important;
}
</style>
