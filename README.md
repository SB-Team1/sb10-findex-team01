-----

# 📚 {1팀} - Findex

[팀 협업 문서](https://innovative-snap-cf9.notion.site/SB10-Findex-Team01-320cef5b940680339a92ff5cf8e38593?source=copy_link)

-----

## 👨‍👩‍👧‍👦 팀원 구성

김하은 (https://github.com/shong9124)

김진우 (https://github.com/zinuzanu)

신지연 (https://github.com/Nooroong)

이규빈 (https://github.com/plzslp)

이윤섭 (https://github.com/jonasInDark)

이하나 (https://github.com/hnlee199)

-----

## 📈 프로젝트 소개

- 금융 지수 데이터를 한눈에 제공하는 대시보드 서비스
- 프로젝트 기간: 2026.03.13 ~ 2026.03.20

-----

## 🛠 기술 스택

### **Backend**
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Scheduler

### **Library**
- Lombok
- Mapstruct
- QueryDSL
- Swagger/OpenAPI 3.0

### **Database**
- Postgresql

### **Infrastructure**
- Railway

### **Tools**
- Notion
- ERD Cloud
- Discord
- Github

-----

## 💻 팀원별 구현 기능 상세

### **김하은**

**대시보드 관리**
- 사용자별 즐겨찾기 지수 요약 정보 제공
  - 즐겨찾기된 지수의 전일 대비 성과 요약 및 실시간 현황 API 구현

- 대시보드 지수 시계열 차트 조회 기능 구현
  - 공공데이터 Open API 기반 종가 데이터를 활용한 월/분기/연 단위 시계열 데이터 제공
  - 날짜 범위 및 지수 ID별 동적 정렬 및 필터링 로직 구현
  - 최근 5일 및 20일 종가 데이터를 활용한 이동평균 산출 로직 구현
  - 차트 시각화에 적합한 데이터 구조(DTO) 변환 및 응답 처리

- 기간별 지수 성과(수익률) 분석 및 랭킹 구현
  - 전일, 전주, 전월 대비 종가 기반 수익률 계산 로직 구현
  - 계산된 성과율 기준 정렬 및 순위 산정 로직을 통한 랭킹 데이터 제공
  - 사용자가 대시보드에서 상승/하락 추세를 한눈에 파악할 수 있도록 기간 파라미터(DAILY/WEEKLY/MONTHLY) 동적 처리


### **김진우**

**자동 연동 배치**
- **Spring Scheduler 기반 지수 데이터 자동 연동 배치 구현**
    - 1일 주기 배치 실행 및 애플리케이션 설정(Cron)을 통한 실행 주기 외부 주입 관리
    - 자동 연동 설정이 활성화된 지수만 선별하여 데이터 수집 대상에 포함하는 필터링 로직 구현

- **증분 수집 기반 데이터 동기화 로직 구현**
    - 마지막 자동 연동 성공 시점을 기준으로 이후 데이터만 조회하는 증분 수집 방식 적용
    - 중복 데이터 방지 및 API 호출 최소화를 통한 효율적인 데이터 동기화 처리

- **대량 데이터 처리 성능 최적화**
    - API 응답 데이터를 Buffer에 적재 후 Chunk 단위로 분할 처리하는 구조 설계
    - JDBC Batch Insert를 활용하여 DB write 성능 개선 및 대량 데이터 처리 효율성 확보

### **신지연**

**연동 작업 관리**
- **Open API 연동 작업 이력 관리 및 수동 실행 API 구현**
  - 지수 정보 및 데이터에 대한 사용자 수동 연동 기능 개발
  - 연동 유형, 지수, 대상 날짜, 작업자(IP/System), 결과, 일시를 포함한 이력 구조 설계

- **연동 결과 기록 및 정합성 추적 로직 구현**
  - 지수 및 날짜 단위의 세부 연동 성공/실패 결과 저장 로직 구현
  - 데이터 API를 응용하여 지수 정보를 등록 및 수정하는 프로세스 처리

- **연동 이력 조회 및 페이지네이션 처리**
  - 유형, 지수, 날짜, 결과 등 복합 조건 검색 및 단일 정렬 기능 구현
  - Cursor Pagination 기술을 적용한 이력 목록 조회 API 개발

### **이규빈**

**지수 데이터 관리**
- **지수 데이터 무결성 검증 및 CRUD API 구현**
  - 시가, 종가, 고가, 저가, 대비, 등락률, 거래량 등 시세 정보 관리 기능 개발

- **지수 데이터 조건 검색 및 분석 지원 기능 구현**
  - 지수(완전 일치) 및 날짜(범위 조건) 기반의 복합 필터링 로직 개발
  - 소스 타입을 제외한 전 필드 대상의 단일 정렬 기능 구현
  - 데이터 목록 조회를 위한 Cursor Pagination 기반 목록 API 구현

- **지수 데이터 CSV Export 기능 구현**
  - 필터링 및 정렬 조건이 적용된 지수 시세 데이터의 CSV 파일 추출 기능 개발

### **이윤섭**

**Open API 연동 준비 및 지수 정보 관리**
- **Open API 연동 환경 구축**
  - 공공데이터 API 수집 모듈 개발
  - Spring WebClient 활용한 외부 API 연동 구조

- **지수 정보 관리**
  - 지수 정보 CRUD 구현
  - 데이터 무결성을 위한 제약 조건 설정

- **지수 정보 조회**
  - Cursor 기반 Pagination 구현
  - 조회 성능 향상을 위한 인덱스 전략 수립
  - Querydsl 기반 동적 쿼리 및 검색 로직 구현
  - 동적 쿼리 추상화 및 컴파일 시 타입 체크로 런타임 에러 방지

### **이하나**

**자동 연동 설정 관리**
- **지수별 자동 연동 활성화 상태(On/Off) 제어 기능 구현**
  - 지수 정보 등록 시 자동 연동 설정을 '비활성화' 상태로 초기 생성하는 로직 개발
  - 관리자용 활성화 여부 수정 API 및 상태 변경 로직 구현

- **자동 연동 설정 조회 및 모니터링 기능 구현**
  - 지수 ID 및 활성화 상태 기반의 복합 조건 검색 기능 개발
  - 지수명 및 활성화 여부 기준의 단일 정렬 로직 적용

- **자동 연동 설정 목록 조회**
  - 설정 목록의 빠른 로딩과 정확한 페이징을 위한 Cursor Pagination 기반 응답 처리

-----

## 📂 파일 구조

```text
src
 ┗ main
    ┣ java
    ┃ ┗ com
    ┃    ┗ sprint
    ┃       ┗ project
    ┃          ┗ findex
    ┃             ┣ config
    ┃             ┃ ┣ openapi
    ┃             ┃ ┃ ┗ OpenApiConfig.java
    ┃             ┃ ┣ FindexConfig.java
    ┃             ┃ ┗ QueryDslConfig.java
    ┃             ┣ controller
    ┃             ┃ ┣ advice
    ┃             ┃ ┃ ┗ GlobalExceptionHandler.java
    ┃             ┃ ┣ AutoSyncConfigController.java
    ┃             ┃ ┣ IndexDataController.java
    ┃             ┃ ┣ IndexInfoController.java
    ┃             ┃ ┗ SyncJobController.java
    ┃             ┣ dto
    ┃             ┃ ┣ autosyncconfig
    ┃             ┃ ┃ ┣ AutoSyncConfigDto.java
    ┃             ┃ ┃ ┣ AutoSyncConfigListRequest.java
    ┃             ┃ ┃ ┣ AutoSyncConfigListResponse.java
    ┃             ┃ ┃ ┗ AutoSyncConfigUpdateRequest.java
    ┃             ┃ ┣ dashboard
    ┃             ┃ ┃ ┣ DashboardQueryDto.java
    ┃             ┃ ┃ ┣ IndexChartDto.java
    ┃             ┃ ┃ ┣ IndexPerformanceDto.java
    ┃             ┃ ┃ ┣ Items.java
    ┃             ┃ ┃ ┣ RankedIndexPerformanceDto.java
    ┃             ┃ ┃ ┗ RankingRequest.java
    ┃             ┃ ┣ indexdata
    ┃             ┃ ┃ ┣ CursorPageIndexDataRequest.java
    ┃             ┃ ┃ ┣ CursorPageResponseIndexDataDto.java
    ┃             ┃ ┃ ┣ IndexDataCreateRequest.java
    ┃             ┃ ┃ ┣ IndexDataCsvExportRequest.java
    ┃             ┃ ┃ ┣ IndexDataCsvHeader.java
    ┃             ┃ ┃ ┣ IndexDataDto.java
    ┃             ┃ ┃ ┣ IndexDataSortField.java
    ┃             ┃ ┃ ┗ IndexDataUpdateRequest.java
    ┃             ┃ ┣ indexinfo
    ┃             ┃ ┃ ┣ CursorPageResponseIndexInfoDto.java
    ┃             ┃ ┃ ┣ IndexInfoCreateRequest.java
    ┃             ┃ ┃ ┣ IndexInfoCursorPageRequest.java
    ┃             ┃ ┃ ┣ IndexInfoDto.java
    ┃             ┃ ┃ ┣ IndexInfoSortField.java
    ┃             ┃ ┃ ┣ IndexInfoSummaryDto.java
    ┃             ┃ ┃ ┗ IndexInfoUpdateRequest.java
    ┃             ┃ ┣ openapi
    ┃             ┃ ┃ ┣ StockMarketIndexRequest.java
    ┃             ┃ ┃ ┗ StockMarketIndexResponse.java
    ┃             ┃ ┣ syncjob
    ┃             ┃ ┃ ┣ CursorPageResponseSyncJobDto.java
    ┃             ┃ ┃ ┣ IndexDataSyncRequest.java
    ┃             ┃ ┃ ┣ SyncJobDto.java
    ┃             ┃ ┃ ┗ SyncJobRequestQuery.java
    ┃             ┃ ┣ ErrorResponse.java
    ┃             ┃ ┗ SortDirection.java
    ┃             ┣ entity
    ┃             ┃ ┣ base
    ┃             ┃ ┃ ┗ BaseEntity.java
    ┃             ┃ ┣ AutoSyncConfig.java
    ┃             ┃ ┣ IndexData.java
    ┃             ┃ ┣ IndexInfo.java
    ┃             ┃ ┣ SourceType.java
    ┃             ┃ ┗ SyncJob.java
    ┃             ┣ global
    ┃             ┃ ┣ entity
    ┃             ┃ ┃ ┣ DeletedStatus.java
    ┃             ┃ ┃ ┣ JobType.java
    ┃             ┃ ┃ ┣ ResultType.java
    ┃             ┃ ┃ ┗ SourceType.java
    ┃             ┃ ┣ exception
    ┃             ┃ ┃ ┣ dto
    ┃             ┃ ┃ ┃ ┗ ApiErrorResponse.java
    ┃             ┃ ┃ ┣ ApiException.java
    ┃             ┃ ┃ ┗ ErrorCode.java
    ┃             ┣ mapper
    ┃             ┃ ┣ config
    ┃             ┃ ┃ ┗ GlobalMapperConfig.java
    ┃             ┃ ┣ AutoSyncConfigMapper.java
    ┃             ┃ ┣ BaseMapper.java
    ┃             ┃ ┣ DashboardMapper.java
    ┃             ┃ ┣ IndexDataMapper.java
    ┃             ┃ ┣ IndexInfoMapper.java
    ┃             ┃ ┗ SyncJobMapper.java
    ┃             ┣ repository
    ┃             ┃ ┣ autosyncconfig
    ┃             ┃ ┃ ┣ AutoSyncConfigRepository.java
    ┃             ┃ ┃ ┣ AutoSyncConfigRepositoryCustom.java
    ┃             ┃ ┃ ┗ AutoSyncConfigRepositoryImpl.java
    ┃             ┃ ┣ dashboard
    ┃             ┃ ┃ ┣ projection
    ┃             ┃ ┃ ┃ ┗ DashboardRankingProjection.java
    ┃             ┃ ┃ ┗ DashboardRepository.java
    ┃             ┃ ┣ indexdata
    ┃             ┃ ┃ ┣ querydsl
    ┃             ┃ ┃ ┃ ┣ IndexDataQDSLRepository.java
    ┃             ┃ ┃ ┃ ┗ IndexDataQDSLRepositoryImpl.java
    ┃             ┃ ┃ ┗ IndexDataRepository.java
    ┃             ┃ ┣ indexinfo
    ┃             ┃ ┃ ┣ querydsl
    ┃             ┃ ┃ ┃ ┣ IndexInfoQDSLRepository.java
    ┃             ┃ ┃ ┃ ┗ IndexInfoQDSLRepositoryImpl.java
    ┃             ┃ ┃ ┗ IndexInfoRepository.java
    ┃             ┃ ┣ syncjob
    ┃             ┃ ┃ ┣ querydsl
    ┃             ┃ ┃ ┃ ┣ SyncJobQDSLRepository.java
    ┃             ┃ ┃ ┃ ┗ SyncJobQDSLRepositoryImpl.java
    ┃             ┃ ┃ ┗ SyncJobRepository.java
    ┃             ┣ scheduler
    ┃             ┃ ┗ AutoSyncScheduler.java
    ┃             ┣ service
    ┃             ┃ ┣ openapi
    ┃             ┃ ┃ ┣ internal
    ┃             ┃ ┃ ┃ ┗ PersistentWorker.java
    ┃             ┃ ┃ ┗ OpenApiService.java
    ┃             ┃ ┣ AutoSyncConfigService.java
    ┃             ┃ ┣ AutoSyncService.java
    ┃             ┃ ┣ CsvExportService.java
    ┃             ┃ ┣ DashboardService.java
    ┃             ┃ ┣ IndexDataService.java
    ┃             ┃ ┣ IndexInfoService.java
    ┃             ┃ ┣ IndexSyncService.java
    ┃             ┃ ┗ SyncJobService.java
    ┃             ┗ Findex.java
    ┗ resources
       ┣ db
       ┃ ┗ schema.sql
       ┣ static
       ┃ ┣ assets
       ┃ ┃ ┗ (CSS, JS, Fonts 파일들...)
       ┃ ┣ favico.ico
       ┃ ┗ index.html
       ┣ application.yml
       ┣ application-local.yml
       ┗ application-prod.yml
```

-----

## 🔗 구현 홈페이지

https://sb10-findex-team01-production.up.railway.app/

-----
