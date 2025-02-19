## Gradle
![image](https://github.com/user-attachments/assets/27efb423-7093-4bde-957c-4a2ccd50b6a7)
출처 : https://docs.gradle.org/current/userguide/dependency_constraints_conflicts.html

> Gradle은 종속성 그래프에 나타나는 모든 요청된 버전을 고려하며,<br>
> 기본적으로 이러한 버전 중 **가장 높은 버전을 선택한다.**

### 해결방안
--- 종속성 분류 ---
- Direct dependencies(직접적 종속성) : 구성 요소의 빌드나 메타데이터 내에 명시적으로 지정된 종속성
- Transitive dependencies(전이적 종속성) : 직접적 종속성의 종속

<br>

**1. Direct dependencies에 제약조건 추가**
```groovy
  dependencies {
    constraints {
        // Platform declares some versions of libraries used in subprojects
        api 'commons-httpclient:commons-httpclient:3.1'
        runtime 'org.postgresql:postgresql:42.2.5'
    }
}
```
  - `constraints` 블럭을 사용하여 라이브러리의 버전을 강제로 명시함.
  -  다른 모듈이 다른 모듈을 가져오는 경우 명시한 버전보다 이 이상 버전만 전이할 수 있도록 범위 지정
  - dependency configuration에 의거하여 실행 시, 지정한 버전으로 해결하도록 적용하기 때문에 충돌 나지 않음
  - 이 제약 조건은 라이브러리가 구성을 확장하는 모든 구성에서 최소한 버전이 유지되도록 보장함

**2. Transitive dependencies에 제약조건 추가**
```groovy
  dependencies {
    implementation 'org.apache.httpcomponents:httpclient'
    constraints {
        implementation('org.apache.httpcomponents:httpclient:4.5.3') {
            because 'previous versions have a bug impacting this application'
        }
        implementation('commons-codec:commons-codec:1.11') {
            because 'version 1.9 pulled from httpclient has bugs affecting this application'
        }
    }
}
```
  - 직접적으로 버전을 명시하지 않음
  - 때문에 프로젝트에서 전이적 종속성으로 가져올 때에만 적용
  - 전이적으로 가져오지 않으면 제약 조건이 적용되지 않음

## Maven
![image](https://github.com/user-attachments/assets/1300611a-c0a6-4d48-98b2-f9b7a847bea4)
출처 : https://maven.apache.org/pom.html#Dependency_Management


> pom.xml에 여러 개의 같은 dependency를 설정하고 서로 다른 버전을 참조하고 있을 때<br>
> maven의 경우도 마찬가지로 두 의존성의 충돌을 해결할 때 **가장 높은 버전을 선택한다.**
>  - 모든 의존성에서 요구하는 최소 버전을 만족할 수 있는 방식이기 때문


### 예시
spring-boot-starter-web와 같은 스타터는 내부적으로 slf4j-api와 logback-classic을 포함하고 있기 때문에, 별도로 slf4j-api의 버전을 명시적으로 추가할 경우 충돌이 발생할 수 있음
```xml
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
      </dependency>
      <dependency>
          <groupId>org.slf4j</groupId>
          <artifactId>slf4j-api</artifactId>
          <version>2.0.0</version>
      </dependency>
```

![image](https://github.com/user-attachments/assets/916964ef-e42a-4dd3-b6bc-415ea7bafcfa)

![image](https://github.com/user-attachments/assets/ef106252-ce30-4200-ab8b-28a0fb0742c2)
+) `mvn dependency:tree` -> pom.xml에 선언된 의존성들을 트리 구조로 확인 가능

Spring Boot는 기본적으로 slf4j 1.7.x 버전과 함께 logback-classic을 포함<br>
slf4j 2.0.0 버전을 직접 의존성으로 추가하면, slf4j의 두 다른 버전(2.0.16 / 2.0.0)이 프로젝트에 존재하게 되어 충돌 발생<br>
여기서 maven은 가장 높은 버전의 slf4j를 사용하기 때문에 2.0.16으로 설정함

### 해결방안
maven에서 제공하는 plugin 사용방법도 있지만 아래와 같이 조치함.


먼저 충돌이 발생하는 라이브러리를 추출한 후 어떤 버전을 사용할지 결정한다.<br>

그 후 slf4j-api 버전 2.0.0을 명시적으로 사용하고 싶다면 자동으로 참조되는 라이브러리에서 `exclusion` 태그로 제외시킨다.<br>
충돌이 발생하는 라이브러리에는 주석 추가
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- ==================================================== -->
        <!-- dependency management -->
        <!-- ==================================================== -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.0</version>
        </dependency>
```
