<template>
	<div class="container-fluid">
		<div class="d-flex flex-column align-items-center justify-content-center">
			<div class="d-flex flex-column align-items-center justify-content-center">
				<h1 class="fw-bold mt-5">Create your account</h1>
			</div>
			<div class="d-flex flex-column mt-5">
				<div class="d-flex flex-column align-items-center justify-content-center">
					<button class="btn d-flex align-items-center justify-content-center" style="background-color: rgb(184, 196, 196);">
						<img src="../assets/googleLogo.svg" alt="Google logo"
							height="24" width="24" class="me-3">
						<span class="fw-bold ho">Continue with Googledd</span>
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
						<p v-if="!email" class="warning-text">* Required value</p>
						<p v-if="email && !isEmailValid" class="warning-text">This is not a valid email format.</p>

						<input v-model="nickName" type="nickName" class="ar form-control mt-3" placeholder="NickName" required>
						<p v-if="!nickName" class="warning-text">* Required value</p>
						
						<input v-model="password" type="password" class="ar form-control mt-3" placeholder="Password" required>
						<p v-if="password.length < 4 || password.length > 12" class="warning-text">* Must consist of 4 to 12 characters</p>
						
						<input v-model="checkPassword" type="password" class="ar form-control mt-3" placeholder="Check Password" required>
						<p v-if="password !== checkPassword" class="warning-text">* Passwords do not match</p>
						
					</div>
					<button type="submit" class="fw-bold  text-white btn d-flex align-items-center justify-content-center" style="background-color:rgb(82, 79, 87)">
						Continue
					</button>
					<a href="/login" class="mt-4">
						Already have an account?
						<span class="fw-bold text-white ms-2">Log in</span>
					</a>
				</form>
			</div>
		</div>
	</div>
</template>

<script>
import axios from 'axios';

const server = axios.create({
  baseURL: process.env.BASE_URL,
});
export default {
	data() {
		return {
			email: '',
			password: '',
			checkPassword: '',
			nickName: '',
		};
	},

	computed: {
		isEmailValid() {
			const re = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
			return re.test(this.email);
		}
	},

	methods: {
		async onSubmit() {
			if (this.password !== this.checkPassword) {
				alert('비밀번호가 일치하지 않습니다.');
				return;
			}

			try {
				const response = await server.post('/api/v1/member/join', {
					email: this.email,
					password: this.password,
					checkPassword: this.checkPassword,
					nickname: this.nickName
				});

				if(response.status === 201) {
					this.$router.push('/login');
				}
			} catch (error) {
				console.error('회원가입 실패:', error);
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
	/* Chrome, Firefox, Opera, Safari 10.1+ */
	color: white !important;
	opacity: 1;
	/* Firefox */
}

h1{
	font-family: 'Courier New', Courier, monospace;
}

.btn {
	width: 350px;
	height: 50px;
}
.btn:hover {
    color: black !important;
}

.ho:hover{
	color: white !important;
}

a {
	color: rgb(111, 120, 121) !important;
	text-decoration: none !important;
}

.warning-text {
	color: #827776;
}
</style>
