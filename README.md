
### 특징
- **DDD 기반 설계**:
  - 도메인을 중심으로 한 Aggregate 설계.
  - 명확한 경계 컨텍스트(Bounded Context) 정의.
- **Spring Boot 기본 설정**:
  - JPA와 Hibernate를 사용한 데이터베이스 연동.
  - WebClient를 통한 외부 API 호출.
  - 환경별 데이터베이스 설정(Local, QA, Test).
  - 공통 응답 구조와 페이징 처리 구현.

### **기술 스택**
- **Backend Framework**: Spring Boot 3.x
- **Database**: MySQL (Local, QA), H2 (Test)
- **ORM**: JPA with Hibernate
- **Connection Pool**: HikariCP
- **API 호출**: WebClient
- **Build Tool**: Gradle
