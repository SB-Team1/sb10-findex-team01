# com.sprint.project.findex.Findex

SB10-com.sprint.project.findex.Findex-team01

### 기본 설정

- 아래 명령어를 실행해주세요.
- intellij 에서 commit message 작성 시 template 이 나옵니다.

```shell
git config commit.template .gitmessage.txt
```

<details>
<summary>commit message convention</summary>

```text
<타입>: <제목> (최대 50자)
예: feat: 구글 자바 스타일 가이드 적용

--- 본문 (선택 사항, 상세 설명이 필요한 경우) ---

--- 꼬리말 (선택 사항, 이슈 번호 연결) ---
fix: #이슈번호

--- 타입 종류 ---
feat     : 새로운 기능 추가
fix      : 버그 수정
docs     : 문서 수정
style    : 코드 포맷팅, 세미콜론 누락 (로직 변경 없음)
refactor : 코드 리팩토링
test     : 테스트 코드 추가/수정
chore    : 빌드 업무, 패키지 매니저 설정 등 (로직 변경 없음)
```

</details>
-----

# 📚 {1팀} - Findex

[팀 협업 문서](https://innovative-snap-cf9.notion.site/SB10-Findex-Team01-320cef5b940680339a92ff5cf8e38593?source=copy_link)

-----

## 👨‍👩‍👧‍👦 팀원 구성

김하은 (개인 Github 링크)

김진우 (개인 Github 링크)

신지연 (개인 Github 링크)

이규빈 (개인 Github 링크)

이윤섭 (개인 Github 링크)

이하나 (개인 Github 링크)

-----

## 프로젝트 소개

- 금융 지수 데이터를 한눈에 제공하는 대시보드 서비스
- 프로젝트 기간: 2026.03.13 ~ 2026.03.03

-----

## 🛠 기술 스택

### **Backend**
- Spring Boot
- Spring Data JPA
- Lombok
- Mapstruct

### **Database**
- Postgresql

### **Tools**
- Notion
- ERD Cloud
-----

## 💻 팀원별 구현 기능 상세

### **김하은**

- **대시보드 관리**: 설명
- 사용자별 즐겨찾기 지수 요약 정보 제공
  - 즐겨찾기된 지수의 전일 대비 성과 요약 및 실시간 현황 API 구현

- 대시보드 지수 시계열 차트 조회 기능 구현
  - 공공데이터 Open API 기반 종가 데이터를 활용한 월/분기/연 단위 시계열 데이터 제공
  - 날짜 범위 및 지수 ID별 동적 정렬 및 필터링 로직 구현

- 지수 이동평균선 데이터 계산 및 API 구현
  - 최근 5일 및 20일 종가 데이터를 활용한 이동평균 산출 로직 구현
  - 차트 시각화에 적합한 데이터 구조(DTO) 변환 및 응답 처리

- 기간별 지수 성과(수익률) 분석 및 랭킹 구현
  - 전일, 전주, 전월 대비 종가(Close Price) 기반 수익률 계산 로직 구현
  - 계산된 성과율 기준 정렬 및 순위 산정 로직을 통한 랭킹 데이터 제공
  - 사용자가 대시보드에서 상승/하락 추세를 한눈에 파악할 수 있도록 기간 파라미터(Day/Week/Month) 동적 처리


### **김진우**

- **자동 연동 배치**: 설명

### **신지연**

- **연동 작업 관리**: 설명

### **이규빈**

- **지수 데이터 관리**: 설명

### **이윤섭**

- **Open API 연동 준비**: 설명
- **지수 정보 관리**: 설명

### **이하나**

- **자동 연동 설정 관리**: 설명

-----

## 📂 파일 구조

```text
src
 ┣ main
 ┃ ┣ java
 ┃ ┃ ┗ com.sprint.project.findex
 ┃ ┃ ┃ ┣ config
 ┃ ┃ ┃ ┣ controller ()
 ┃ ┃ ┃ ┣ dto      ()
 ┃ ┃ ┃ ┣ entity ()
 ┃ ┃ ┃ ┣ mapper    ()
 ┃ ┃ ┃ ┣ repository   ()
 ┃ ┃ ┃ ┣ scheduler        ()
 ┃ ┃ ┃ ┣ service  ()
 ┃ ┃ ┃ ┗ Findex
 ┃ ┣ resources
 ┃ ┃ ┣ db
 ┃ ┃ ┣ application.yml
 ┃ ┃ ┣ application-local.yml
 ┃ ┃ ┗ application-prod.yml
 ┃ ┗
 ┣
 ┣ pom.xml
 ┣ Application.java
 ┗ README.md
```

-----

## 📝 프로젝트 회고록

- **발표 자료 및 상세 회고:** [회고록 바로가기(첨부파일 링크)]
- **핵심 성과:**

-----
