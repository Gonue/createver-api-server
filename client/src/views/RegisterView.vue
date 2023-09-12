<template>
	<div>

		<body>
			<div class="container-fluid">
				<div class="row main-content bg-success text-center">
					<div class="col-md-4 text-center company__info">
						<span class="company__logo">
							<h2><span class="fa fa-android"></span></h2>
						</span>
						<h4 class="company_title">Your Company Logo</h4>
					</div>
					<div class="col-md-8 col-xs-12 col-sm-12 login_form ">
						<div class="container-fluid">
							<div class="row">
								<h2>Register</h2>
							</div>
							<div class="row">
								<form @submit.prevent="onSubmit" class="form-group">
									<div class="row">
										<input v-model="nickName" type="text" name="nickName" id="nickName"
											class="form__input" placeholder="닉네임">
											<p v-if="!nickName" class="warning-text">닉네임을 입력해주세요.</p>
									</div>
									<div class="row">
										<input v-model="email" type="text" name="email" id="email" class="form__input"
											placeholder="이메일">
											<p v-if="!email" class="warning-text">이메일을 입력해주세요.</p>
											<p v-if="email && !isEmailValid" class="warning-text">유효한 이메일 형식이 아닙니다.</p>

									</div>
									<div class="row">
										<input v-model="password" type="password" name="password" id="password"
											class="form__input" placeholder="비밀번호">
											<p v-if="password.length < 4 || password.length > 12" class="warning-text">비밀번호는 4~12자 구성이어야 합니다.</p>

									</div>
									<div class="row">
										<input v-model="checkPassword" type="password" name="checkPassword"
											id="checkPassword" class="form__input" placeholder="비밀번호 확인">
											<p v-if="password !== checkPassword" class="warning-text">비밀번호가 일치하지 않습니다.</p>

									</div>
									<div class="col">
										<input type="button" value="Submit" @click="onSubmit" class="btn">
									</div>
								</form>
							</div>
							<div class="row">
								<p>이미 아이디가 있으신가요? <a href="/login"> 로그인 </a></p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</body>
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

.warning-text {
	color: #d11801;
}

.main-content {
	width: 50%;
	border-radius: 20px;
	box-shadow: 0 5px 5px rgba(0, 0, 0, .4);
	margin: 5em auto;
	display: flex;
}

.company__info {
	background-color: #008080;
	border-top-left-radius: 20px;
	border-bottom-left-radius: 20px;
	display: flex;
	flex-direction: column;
	justify-content: center;
	color: #fff;
}

.fa-android {
	font-size: 3em;
}

@media screen and (max-width: 640px) {
	.main-content {
		width: 80%;
	}

	.company__info {
		display: none;
	}

	.login_form {
		border-top-left-radius: 20px;
		border-bottom-left-radius: 20px;
	}
}

@media screen and (min-width: 642px) and (max-width:800px) {
	.main-content {
		width: 70%;
	}
}

.row>h2 {
	color: #008080;
}

.login_form {
	background-color: #fff;
	border-top-right-radius: 20px;
	border-bottom-right-radius: 20px;
	border-top: 1px solid #ccc;
	border-right: 1px solid #ccc;
}

form {
	padding: 0 2em;
}

.form__input {
	width: 100%;
	border: 0px solid transparent;
	border-radius: 0;
	border-bottom: 1px solid #aaa;
	padding: 1em .5em .5em;
	padding-left: 2em;
	outline: none;
	margin: 1.5em auto;
	transition: all .5s ease;
}

.form__input:focus {
	border-bottom-color: #008080;
	box-shadow: 0 0 5px rgba(0, 80, 80, .4);
	border-radius: 4px;
}

.btn {
	transition: all .5s ease;
	width: 70%;
	border-radius: 30px;
	color: #008080;
	font-weight: 600;
	background-color: #fff;
	border: 1px solid #008080;
	margin-top: 1.5em;
	margin-bottom: 1em;
}

.btn:hover,
.btn:focus {
	background-color: #008080;
	color: #fff;
}
</style>