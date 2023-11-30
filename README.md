<p align="center">
    <img src="https://github.com/Gonue/mine/assets/109960034/c91aeb1b-5553-4d34-8805-a8e6c77df9ad" alt="" width="200px" height="200px">
</p>

<h3 align="center">Createver</h3>

<div align="center">

<p align="center">

<a href="https://createver.site">View Site</a>

</p>

</div>

## 목차
- [목차](#목차)
- [한줄 설명](#한줄-설명)
- [개발 환경](#개발-환경)
- [사용 기술](#사용-기술)
  - [Back](#back)
  - [Database](#database)
  - [Infra](#infra)
  - [Front](#front)
- [시스템 아키텍쳐](#시스템-아키텍쳐)
- [E-R 다이어그램](#e-r-다이어그램)
- [설명](#설명)
  - [사용 모델](#사용-모델)
  - [이미지 생성 및 관리](#이미지-생성-및-관리)
    - [댓글](#댓글)
    - [좋아요](#좋아요)
    - [신고 \& 블라인드](#신고--블라인드)
  - [음악 생성 및 관리](#음악-생성-및-관리)
  - [블로그](#블로그)
  - [캐시 전략 및 최적화](#캐시-전략-및-최적화)
    - [정적 리소스 캐싱](#정적-리소스-캐싱)
    - [동적 데이터 캐싱](#동적-데이터-캐싱)
  - [페이징 및 무한 스크롤](#페이징-및-무한-스크롤)
  - [검색](#검색)
  - [소셜 로그인](#소셜-로그인)
  - [반응형 웹](#반응형-웹)
  - [SEO 개선 및 전략](#seo-개선-및-전략)
  - [서버 성능 및 가용성 관리](#서버-성능-및-가용성-관리)
  - [보안 권장 사항](#보안-권장-사항)
  - [문서화](#문서화)
  - [S3전략](#s3전략)

---

## 한줄 설명

<p align="center">

<img src="https://github.com/Gonue/mine/assets/109960034/e4f5bcb0-56d4-4b2d-a745-6599731bff59" width="300px" height="200px"> 
<img src="https://github.com/Gonue/mine/assets/109960034/8cf094cc-3644-4455-8a46-199ebede90a6" width="300px" height="200px">

</p>


- Createver는 간단하게 사용할 수 있는 AI 기반 생성 사이트입니다. 사용자는 몇 번의 클릭으로 다양한 창작물을 생성할 수 있습니다.

## 개발 환경

- Intellij IDEA Ultimate
- Postman
- Github
- GitKraken

---

## 사용 기술

### Back

- Java 17
- Springboot 3.1.5
- Spring Data JPA
- QueryDSL
- Spring Security
- Swagger

### Database

- Mysql
- Redis

### Infra

- AWS - EC2, ALB, RDS, ElasticCache, S3, CloudFront, Route53, SageMaker, CloudWatch
- Docker
- GitHub Actions
- GitHub ContainerRegistry
- Uptime Kuma

### Front
- Vue.js 3.x
s
## 시스템 아키텍쳐

<img src="https://github.com/Gonue/mine/assets/109960034/a50ccbbd-ea05-4e8e-a04d-75415f857e51">

---

## E-R 다이어그램

<img src="https://github.com/Gonue/mine/assets/109960034/111b587b-4ee0-49f5-a5de-c2dc98bdad0e">

---

## 설명

### 사용 모델

배포환경

- 모든 모델은 AWS SageMaker를 통해 배포되어 운영 중입니다.

이미지 생성 모델

- 모델: Stable Diffusion web UI (AUTOMATIC1111 버전)
- 기능: 사용자 입력 기반 고해상도 커스텀 이미지 생성
  - 고정된 CheckPoint, Lora, Embedding 사용
  - 인물 중심 이미지에 특화된 고정 설정사용
 
음악 생성 모델

- 모델: META Music Gen
- 기능: 사용자 입력 기반 다양한 스타일의 음악 생성

이 두 모델은 모두 사용자의 입력에 기반하여 이미지나 음악을 생성하는 Prompt 기반의 생성형 AI 모델이며 
고정된 설정값을 사용하여 사용자가 별도로 설정을 조정할 필요 없이 일관된 출력 품질로 간편한 사용을 목표로 하고 있습니다.

---





### 이미지 생성 및 관리

<p align="center">

<img src="https://github.com/Gonue/mine/assets/109960034/3d368adf-7f1a-4118-9872-4c2c7918be04" width="400px" height="400px">

<img src="https://github.com/Gonue/mine/assets/109960034/ea47659d-7c19-41e6-8d74-1516efa256a0" width="400px" height="400px">

</p>
주요 기능 및 특징

- 맞춤형 이미지 생성: 사용자가 입력하는 텍스트 프롬프트를 기반으로 개인화된 이미지를 생성합니다.
- 다양한 언어 지원: 한국어와 영어를 포함한 다양한 언어의 프롬프트 처리 및 번역 기능을 제공합니다.
- 고품질 이미지 생성: OpenAI와 AWS SageMaker에 배포된 Stable-Diffusion 모델을 활용하여 고품질의 이미지를 생성합니다.
- 사용자 친화적 옵션 선택: 사용자가 간편하게 이미지 스타일과 형식을 선택할 수 있는 몇가지 옵션을 제공합니다.

이미지 공유 및 관리
- 태그 생성 및 관리 : 생성된 이미지와 관련된 태그를 자동으로 생성하여 이미지를 쉽게 분류하고 검색 가능하게 합니다.
- 갤러리 게시: 사용자에 의해 생성된 이미지는 자동으로 공용 갤러리에 게시되어 모든 사용자가 접근하고 감상할 수 있습니다.
- 안전한 이미지 저장: AWS S3를 활용하여 생성된 이미지를 안전하게 저장하고 관리합니다.
- 성능 최적화: 서버의 부하를 고려한 요청 제한두어 안정적인 시스템 운영 유지합니다.

<img src="https://github.com/Gonue/mine/assets/109960034/c088bed5-e18a-462e-b014-765e03055d72">
이미지 생성시 전체 동작 과정

#### 댓글

<img src="https://github.com/Gonue/mine/assets/109960034/16fea7fd-5415-43d6-84ad-cadded697968" height="400px">

#### 좋아요

#### 신고 & 블라인드



### 음악 생성 및 관리
주요 기능 및 특징


---

### 블로그

기본 CRUD 기능
- 관리자 권한 제한: 작성, 수정, 삭제 기능은 Spring Security를 사용하여 관리자(Role: Admin)만 접근할 수 있도록 인가 처리를 구현했습니다.
- 관리자 인터페이스: 클라이언트에서는 관리자 계정에만 해당 관리 기능이 보이도록 구현하여, 권한에 따른 사용자 경험을 분리했습니다.

클라이언트 구현
- Quill 에디터 사용: 글 작성 및 편집을 위해 Quill 에디터를 활용했습니다.
- 이미지 업로드 및 편집: ImageUploader 및 BlotFormatter 모듈을 통해 사용자가 이미지를 쉽게 업로드하고 편집할 수 있도록 했습니다.

<img src="https://github.com/Gonue/mine/assets/109960034/a5da9c1c-3088-42ed-8b9e-115ffffc9369">

이미지 업로드 및 관리
- 로컬 이미지 업로드: 사용자는 로컬에서 이미지를 선택하여 서버로 업로드할 수 있습니다.
- AWS S3 저장: 업로드된 이미지는 AWS S3에 저장되며, 저장된 이미지의 URL을 반환받아 블로그 게시물에 사용합니다.

---

### 캐시 전략 및 최적화

#### 정적 리소스 캐싱
- 도구: Amazon CloudFront
- 목적: 웹사이트의 정적 리소스(이미지, CSS, JavaScript 파일 등)에 대한 빠른 로딩 시간과 효율적인 콘텐츠 전달
  - CDN을 활용하여 사용자에게 더 빠른 접근성 제공
  - 각 리소스 유형별로 별도의 원본 및 동작 설정 적용
  - 데이터의 특성(이미지, 음악 등)에 맞는 캐시 정책 설정

#### 동적 데이터 캐싱
- 도구: Amazon ElasticCache(Redis)
- 목적: 데이터베이스 쿼리 결과와 같은 동적 데이터의 빠른 응답 시간 및 처리 효율성 향상
- 고성능 인-메모리 데이터 스토어를 활용하여 자주 요청되는 데이터를 캐싱, 데이터베이스 부하 감소 및 응답 시간 단축
  - 데이터 유형별(블로그 글, 사용자 정보 등) 캐시 생명주기 설정 및 자동 초기화
  - 데이터 정합성에 영향을 미치는 메서드 사용 시 캐시 초기화

<p align="center">

  <img src="https://github.com/Gonue/mine/assets/109960034/61f3779e-2bfd-4f5b-bb13-48af4c6bf754" width="600" />
  <img src="https://github.com/Gonue/mine/assets/109960034/e5f6492b-2737-4bd9-9346-336db675301b" width="600" /> 

</p>


---


### 페이징 및 무한 스크롤

오프셋 페이징 및 클라이언트 구현
- 게시물 및 이미지 검색: 오프셋 페이징 쿼리를 사용하여 게시물과 이미지 검색 시 페이징 처리를 구현했습니다.
- 무한 스크롤 기능: 클라이언트에서 스크롤 이벤트를 감지해, 사용자가 화면 하단에 도달할 때 추가 데이터를 로드하는 방식으로 무한 스크롤을 구현했습니다.
- 효율적 데이터 로딩: 사용자의 페이지 요청에 따라 적절한 양의 데이터를 로드하여 서버의 부하를 최소화하고 시스템의 성능을 안정화합니다.

백엔드 성능 최적화
- 페이지 파라미터 활용: 사용자의 페이지 요청에 따라 데이터를 조회하고, 이를 사용자에게 제공합니다.
- 정렬 및 필터링: 다양한 정렬 기준과 필터링 옵션을 제공하여 사용자가 원하는 데이터를 쉽게 찾을 수 있도록 합니다.
- 최적화된 쿼리: JPA 및 Querydsl을 사용하여 데이터베이스 쿼리를 최적화하고, 그룹화 및 집계를 통해 필요한 정보만 효과적으로 추출합니다.
- 동적 정렬 적용: Pageable의 Sort 객체를 활용하여 사용자 요청에 따라 동적으로 데이터를 정렬합니다.
- 효율적인 카운트 쿼리: 전체 페이지 수를 계산하기 위해 별도의 카운트 쿼리를 사용하여, 전체 데이터셋을 로드하지 않고도 페이징 정보를 제공합니다.
- 서버 리소스 효율적 사용: 적절한 페이지 크기와 쿼리 최적화를 통해 서버 부하를 줄이고 빠른 응답 시간을 보장합니다.

---

### 검색

---

### 소셜 로그인

---

### 반응형 웹

다양한 기기에서 일관된 사용자 경험 제공

<img src="https://github.com/Gonue/mine/assets/109960034/1fb01d99-091b-48e8-83c5-4f6c14a83c05">

[Google Search Console 기반]

---

### SEO 개선 및 전략

현 상태 
- 구현 방식 : Vue.js를 사용한 클라이언트 사이드 렌더링 (CSR)
- 지표 : Google PageSpeed Insights 기반 결과

<img src="https://github.com/Gonue/mine/assets/109960034/e8964f4d-c0cd-4d91-8239-811556bfb38d">

- 최적화 전략 : 
  - Google PageSpeed Insights 기반 보고서에 기반 지속적 사이트 최적화
  - 메타 태그 최적화
  - Pre-rendering 적용 

그러나, CSR 방식의 구조상 검색 엔진 최적화에 한계가 존재하여,
사이트 특성상 높은 유입률이 필요하기 때문에, 향후 SSR 및 SSG 지원으로 SEO 성능을 개선할 수 있는 Next.js 또는 Nuxt.js로의 마이그레이션을 계획하고 있습니다.

### 서버 성능 및 가용성 관리

<img src="https://github.com/Gonue/mine/assets/109960034/162dcafe-1066-4451-a918-c5bd617ee4bf">


최근 30일 동안 서버 가용율 99.72%, 높은 서비스 가용성 및 안정성 보장

---

### 보안 권장 사항

---

### 문서화

---

### S3전략