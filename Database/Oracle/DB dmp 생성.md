## 1. dmp file 생성
1. (만약 로컬이 아닌 다른 곳에서 실행 중일 경우) oracle bash 진입
2. 아래 exp(내보내기) 명령어 입력
- *full 옵션 : 전체 데이터베이스를 내보내는지 여부*
```shell
$  exp userid={사용자-ID-지정}/{사용자-pw-지정} file={dmp 생성할 파일-path/파일명} full=y

적용 예시
$  exp userid=KPS/iampassword file=/qc_dump.dmp full=y
```
