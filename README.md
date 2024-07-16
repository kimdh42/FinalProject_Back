# [Final-Project] Synergy-Hub
Spring Framework + React.js SPA 기반의 그룹웨어 개발

<br/>

## 📄 프로젝트 소개
<img src="https://github.com/SynergyHub-groupware/synergyhub-back/assets/157452524/06f7dacb-6e24-49b3-aa06-29a12f2817dd" width="200px" height="150px" title="Alt Text">
<br/>

- **SynergyHub** : 소통과 협업을 강화하다.
- 팀원과 원활한 소통을 기반으로 시너지 효과를 극대화하는 그룹웨어를 제공합니다.

## 📅 개발 기간
- 2024.05.08 ~ 2024.07.16 (2달)

### 🏃 담당자 소개

|담당자|담당 기능|
|---|---|
|[김대한](https://github.com/kimdh42)|인사 (권한별 정보 조회, 조직도, 프로필), <br/> 발령|
|[김정원](https://github.com/won1999won)|게시글(생성/수정/임시저장), 게시판(생성/수정/권한),<br/>첨부파일(저장/다운로드)|
|[박은비](https://github.com/SILVER-BE)|근태/휴가(개인별 기록/관리, 권한별 관리), 메일링,<br/>엑셀 다운로드, SSE 통신 기반 피드|
|[박진영](https://github.com/jinyoung23456)|일정, 업무, 캘린더|
|[이다정](https://github.com/LXXDJ)|결재상신, 회수 및 수정, 임시저장, 결재승인&반려,<br/>서명관리, 개인보관함관리, 결재양식관리|
|[이재현](https://github.com/JH5256)|사용자 로그인, 쪽지 (발송/조회/임시저장/삭제),<br/>회원 차단, 주소록|

### ✏ 기술 스택 (플랫폼 & 언어)
![js](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![js](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![js](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white)

![js](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![js](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![js](https://img.shields.io/badge/CSS-239120?&style=for-the-badge&logo=css3&logoColor=white)
![js](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)

### 🙌 협업 관리
![js](https://img.shields.io/badge/Notion-20232A?style=for-the-badge&logo=Notion&logoColor=#00000)
![js](https://img.shields.io/badge/Discord-20232A?style=for-the-badge&logo=Discord&logoColor=#5865F2)
![js](https://img.shields.io/badge/GitHub-20232A?style=for-the-badge&logo=GitHub&logoColor=#181717)
![js](https://img.shields.io/badge/Figma-20232A?style=for-the-badge&logo=Figma&logoColor=#F24E1E)

<br/>

## 업무 분석 & 모델링

### [업무 흐름도 URL](https://whimsical.com/thunderbolts-full-flow-chart-BGoMHJesjfduSsLoan1mQt)

### [이벤트 스토밍 URL](https://miro.com/app/board/uXjVKBGvnGA=/)

### [Figma URL](https://www.figma.com/design/cvZT81QuXtLELMrXJPtmhW/%EA%B7%B8%EB%A3%B9%EC%9B%A8%EC%96%B4?node-id=0-1&t=dQYS44L1KbI9wgR2-0)

### DA#5 Modeling
<img src="https://github.com/SynergyHub-groupware/synergyhub-back/assets/157452524/e0176d9c-42bf-4e26-977d-f454186997f5" width="600px" height="400px" title="Alt Text">

### 이슈 관리
<img src="https://github.com/SynergyHub-groupware/synergyhub-back/assets/157452524/12c6b057-3f46-4886-96c7-a4e1c2b354fa" width="600px" height="400px" title="Alt Text">

<br/>

## 💻 주요 기능

### 로그인
> JWT(JSON Web Tokens)를 이용한 유저 인증, 권한 관리 및 유저 식별

### 게시판, 게시글
> 게시판 : 권한 비교 및 등록
> 
> 게시글 : 관련 데이터 추가 및 수정, 삭제 기능 

### 피드
> SSE통신을 이용한 알림 기능

### 근태 / 휴가
> Scheduled를 이용한 근태일지 자동 생성 기능
>
> 메일링 서비스를 통한 연차 촉진 기능
>
> Excel 파일 다운로드 기능

### 일정 / 업무
> 개인 이벤트, 업무 생성, 수정, 삭제
> 
> 모달을 통한 데이터 관리 기능

### 캘린더
> 이벤트 데이터를 받아 일정 표기
>
> CRUD 모달 팝업 기능

### 쪽지
> 쪽지 발송, 조회, 임시저장 및 삭제 기능
> 
> 주소록 조회 및 추가 기능

### 회원 차단
> 차단회원 쪽지 발송 및 조회 제한 기능

### 전자결재
> 결재라인 자동 생성 및 상신, 회수, 수정, 임시저장
>
> 서명, 개인보관함, 결재양식 관리

