### 생성
1. Project ID 확인
```shell
$  service smd3100 status
...
Main PID : 9043(java)
...
```

2. heapdump 파일 생성
```shell
$  sudo jmap -dump:format=b,file=smd3100_240524.hprof 9043
[sudo] password for itman:***************
Dumping heap to /home/itman/smd3100_240524.hprof ...
Heap dump file created
```

3. 최신순으로 파일 조회
```shell
$  ls -alth
total 582M
-rw-------  1 root  root   20M  5월 24 15:52 smd3100_240524.hprof
...

# 아마 이렇게 UID, GID가 root로 생성되었을 것임
```

4.  GroupID & UserID 변경 (root -> itman)
```shell
#  sudo chown USER[:GROUP] FILE
$  sudo chown itman:itman smd3100_240524.hprof

# 수정 후 반영되었는지 조회
$  ls -alth smd3100_240524.hprof
```

5. 생성된 파일 MemoryAnalyzer 같은 프로그램으로 열어서 분석
