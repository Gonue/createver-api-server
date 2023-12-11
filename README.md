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
    - [검색](#검색)
  - [음악 생성 및 관리](#음악-생성-및-관리)
  - [블로그](#블로그)
  - [캐시 전략 및 최적화](#캐시-전략-및-최적화)
    - [정적 리소스 캐싱](#정적-리소스-캐싱)
    - [동적 데이터 캐싱](#동적-데이터-캐싱)
  - [페이징 및 무한 스크롤](#페이징-및-무한-스크롤)
  - [CI/CD 파이프라인](#cicd-파이프라인)
  - [로그 추적 및 관리](#로그-추적-및-관리)
    - [로그 추적기](#로그-추적기)
    - [로그 관리](#로그-관리)
  - [소셜 로그인](#소셜-로그인)
  - [반응형 웹](#반응형-웹)
  - [SEO 개선 및 전략](#seo-개선-및-전략)
  - [모니터링 및 알림](#모니터링-및-알림)
  - [문서화](#문서화)

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
- Spring Rest Docs
<!-- - Swagger -->

### Database

- Mysql
- Redis

### Infra

- AWS - EC2, ALB, RDS, ElasticCache, S3, CloudFront, Route53, SageMaker, Lambda, EventBridge, CloudWatch
- Docker
- GitHub Actions
- GitHub ContainerRegistry
- Uptime Kuma

### Front
- Vue.js 3.x

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

이 두 모델 모두 사용자의 입력에 기반하여 이미지나 음악을 생성하는 Prompt 기반의 생성형 AI 모델이며 
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
- 태그 생성 및 관리 : 생성된 이미지의 태그를 자동으로 생성하여 이미지를 쉽게 분류하고 검색 가능하게 합니다.
- 갤러리 게시: 사용자에 의해 생성된 이미지는 자동으로 공용 갤러리에 게시되어 모든 사용자가 접근하고 감상할 수 있습니다.
- 연관 이미지 : 이미지와 연관도가 높은 이미지를 갤러리에서 같이 표시합니다.
- 좋아요 : 사용자별 좋아요 카운트가 가능합니다.
- 댓글 : 공용 갤러리에 게시되는 모든 이미지는 댓글이 가능합니다.
- 신고 & 블라인드 : 부적절한 이미지에 대해 신고가 가능하며 일정 신고수치가 되면 자동 블라인드 처리 됩니다.
- 이미지 저장 및 관리: 
  - 저장 : AWS S3를 활용하여 생성된 이미지를 안전하게 저장하고 관리합니다.
  - 관리 : 라이프 사이클을 지정하여 자주 사용하지 않는 이미지의 경우 좀 더 비용 효율적인 스토리지 계층(S3 Glacier)으로 자동 이동 됩니다.
- 성능 최적화: 서버의 부하를 고려한 요청 제한을 두어 안정적인 시스템 운영 유지시키고, Querydsl을 활용하여 데이터베이스 쿼리를 최적화하고, 효율적인 데이터 접근 방식을 구현했습니다.

#### 검색

<img src="https://github.com/Gonue/mine/assets/109960034/fff5d49a-9410-4d77-9368-915b93a828ac">

사용자는 특정 옵션과 프롬프트(키워드)를 기반으로 이미지를 검색할 수 있습니다.

검색 로직
- 조건부 검색: 동적으로 검색 조건을 구성하며 사용자가 제공한 옵션과 프롬프트에 따라 검색 조건이 변경됩니다.
- 옵션 기반 검색: 사용자가 선택한 옵션(예: 이미지 스타일)에 따라 해당하는 이미지를 필터링합니다.
- 프롬프트 기반 검색: 사용자가 입력한 프롬프트가 포함된 이미지를 검색합니다.
- 성능 최적화: 이미지 리스트와 같이 데이터베이스 쿼리 최적화 및 효율적 데이터접근 방식을 구현했습니다.

<img src="https://github.com/Gonue/mine/assets/109960034/c088bed5-e18a-462e-b014-765e03055d72">
[이미지 생성시 전체 동작 과정]

### 음악 생성 및 관리

<p align="center"> 

<img src="https://github.com/Gonue/mine/assets/109960034/5d00345c-81df-43c4-87d6-fc6450d73739" width="400px" height="400px">
<img src="https://github.com/Gonue/mine/assets/109960034/0eacd78f-7ad4-4034-bf8e-1d1e35c6de86" width="400px" height="400px">

</p>

주요 기능 및 특징

- 음악 생성: 사용자의 입력(프롬프트)을 기반으로 개인화된 음악 트랙을 생성합니다. 이는 프롬프트 기반으로 지정한 장르, 스타일, 무드 등의 요구사항에 따라 다양한 형태로 제공됩니다.
- 사용자 친화적 인터페이스: 사용자가 쉽게 음악 생성, 결과를 듣고 관리할 수 있는 인터페이스를 제공합니다.

음악 공유 및 관리

- 음악 앨범 생성: 생성된 음악으로 음악 앨범 생성이 가능하며, 다른 사용자들이 감상하고 공유할 수 있습니다.
- 음악 저장 및 관리: 
  - 저장 : AWS S3를 활용하여 생성된 음악을 안전하게 저장하고 관리합니다.
  - 관리 : 라이프 사이클을 지정하여 자주 사용하지 않는 파일의 경우 좀 더 비용 효율적인 스토리지 계층(S3 Glacier)으로 자동 이동 됩니다.

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

  <img src="https://github.com/Gonue/mine/assets/109960034/61f3779e-2bfd-4f5b-bb13-48af4c6bf754" width="400"  height="400px"/>
  <img src="https://github.com/Gonue/mine/assets/109960034/e5f6492b-2737-4bd9-9346-336db675301b" width="400" height="400px"/> 

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

### CI/CD 파이프라인

본 프로젝트의 CI/CD 파이프라인은 비용 효율성 및 워크플로우의 간소화를 위해 GitHub Actions를 통해 전적으로 관리됩니다. 이를 통해 코드 통합, 테스트, 빌드 및 배포 과정이 자동화되어, 안정적이고 지속적인 소프트웨어 개발이 가능하였습니다.

<img src="https://github.com/Gonue/createver-api-server/assets/109960034/a03aa60a-8f08-4f2e-8000-052a8e9b1812">

CI (Continuous Integration)
  - 구성요소
    - 빌드 및 테스트: PR이 'main' 브랜치에 생성될 때마다, 소스 코드가 자동으로 빌드되고 테스트됩니다.
      - Gradle을 사용한 빌드 및 테스트 실행
      - 테스트 결과 및 실패한 코드 라인에 대한 PR 코멘트 등록
      - 아티팩트 업로드: 빌드된 JAR 파일이 아티팩트로 업로드되어, 후속 Docker 빌드 과정에서 사용됩니다.

    - Docker 빌드 및 GCR 푸시
      - 빌드된 아티팩트를 사용하여 Docker 이미지를 생성하고 Github Container Registry(GCR)로 푸시합니다.

    - 알림
      - 위의 작업 결과에 따른 알림을 Discord 채널에 전송합니다.

CD (Continuous Deployment)
- 구성요소
  - 배포: PR이 'main' 브랜치에 병합되고 닫히면, Docker 이미지가 EC2 인스턴스에 자동으로 배포됩니다.
    - Docker 이미지 풀 및 EC2 인스턴스에 배포
  - 알림
    - CD 작업이 완료된 후, Discord 채널에 작업 상태 알림이 전송됩니다.

---

### 로그 추적 및 관리

#### 로그 추적기
- Spring AOP를 활용한 로그 추적기 구현.
- 포인트컷 지정: Controller, Service, Repository의 모든 메소드에 대한 로그를 찍습니다. 이는 시스템의 요청과 응답을 효과적으로 추적하는 데 도움이 됩니다.
- 로그 추적:
  - 각 요청은 고유한 난수(UUID)를 기반으로 추적되며, 요청의 깊이와 응답 시간을 로그에 명시합니다.
  - 싱글톤 패턴과 쓰레드 로컬을 사용하여 한번에 여러 요청이 와도, 요청별로 고유한 UUID를 유지할 수 있게 합니다.
- 예외 처리: 예외 발생 시, 로그 추적기는 해당 오류를 로그에 기록하고 추적 ID를 이용해 오류 발생 위치를 쉽게 파악할 수 있도록 합니다.

<img src="https://github.com/Gonue/createver-api-server/assets/109960034/53cb5b1a-0893-42ed-a5ff-2995c08f99cf">

위와 같이 구현하여 로그의 실시간 모니터링과 분석을 가능하게 하며, 장애 발생시 빠른 로그 확인이 가능하도록 하였습니다.

#### 로그 관리
- AWS CloudWatch: 서버 로그는 AWS CloudWatch와 Logs Insights 통해 관리 및 식별합니다.
- 로그는 기본적으로 INFO 레벨로 관리되며 에러발생시 해당 로그는 따로 캐치하여 기록 및 관리 되도록 하였습니다.
- S3 이동 자동화 아키텍쳐: 한달이 경과한 로그 데이터는 비용 효율성을 고려하여 EventBridge 및 Lamda를 사용하여 자동으로 Amazon S3로 이동되어 장기 보관됩니다.

<img src="https://github.com/Gonue/createver-api-server/assets/109960034/265b6d1c-bac5-4a43-b4c6-3e79d04cee6a">

- 1. Amazon EventBridge에 정의한 Rule에 의해 Lambda를 호출합니다.
- 2. 이후 Lambda가 지정한 CloudWatch의 Logs에 적재된 데이터를 찾습니다.
- 3. 이관될 대상의 로그들이 S3로 이관됩니다.
- 4. 이후 S3에서 설정한 생명주기에 따라 S3 Glacier 이동합니다.

<img src="https://github.com/Gonue/createver-api-server/assets/109960034/5169901d-f9a6-4d24-974d-856ea0036ae3">

---


### 소셜 로그인

과정
- 소셜 로그인 과정은 사용자가 OAuth2 제공자를 통해 인증을 수행하고, 시스템이 이를 처리하여 JWT 토큰을 발급하는 순서로 진행됩니다.

단계별 흐름
- 사용자 인증 요청: 사용자가 소셜 로그인 제공자를 통해 인증을 요청합니다.
- OAuth2 인증 처리: 스프링 시큐리티는 OAuth2 표준을 따라 사용자의 인증 요청을 처리합니다.
- 사용자 정보 추출: 인증 성공 후, 사용자의 정보(이메일, 닉네임 등)가 추출됩니다.
- 권한 할당 및 사용자 정보 저장: 사용자에게 권한이 할당되고, 사용자 정보가 시스템에 저장됩니다.
- JWT 토큰 발급: 사용자에 대한 Access Token과 Refresh Token이 생성됩니다.
- 리디렉션 URI 생성: 생성된 토큰이 포함된 리디렉션 URI가 생성됩니다.
- 사용자 리디렉션: 사용자는 토큰이 포함된 URI로 리디렉션되어 애플리케이션에 접근합니다.

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
  - Google PageSpeed Insights 기반 보고서 기반 지속적 사이트 최적화
  - 메타 태그 최적화
  - Pre-rendering 적용 

그러나, CSR 방식의 구조상 검색 엔진 최적화에 한계가 존재하여,
사이트 특성상 높은 유입률이 필요하기 때문에, 향후 SSR 및 SSG 지원으로 SEO 성능을 개선할 수 있는 Next.js 또는 Nuxt.js로의 마이그레이션을 계획하고 있습니다.

### 모니터링 및 알림

<img src="https://github.com/Gonue/mine/assets/109960034/162dcafe-1066-4451-a918-c5bd617ee4bf">


- 서비스 가용성: 최근 30일 동안 서버 가용율 99%의 이상의 서비스 가용성을 유지 하고 있습니다.
- 모니터링: Aws CloudWatch, Uptime Kuma 등으로 서버상태를 모니터링 하며 특정 상태(다운타임, 성능 저하 등) 변화에 따라 알림을 전송합니다.

---

### 문서화

Spring REST Docs를 사용하여 API 문서화 진행.

<img src="https://github.com/Gonue/createver-api-server/assets/109960034/c3bfa19f-b87b-4fbb-81e2-c639c0376cfc">

문서 주소 : https://api.createver.site/docs/index.html
* 일부 API는 제외되어 있습니다.
