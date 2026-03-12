# Findex

SB10-Findex-team01

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
