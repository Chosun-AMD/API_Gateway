# Description
- API-Gateay 서버입니다.
- 현재는 Apache Tomcat 기본 내장서버를 사용하고 있지만, 추후 Nginx서버로 변경해 `Reverse Proxy`기능을 도입해보려고 합니다.
- API Gateway 설정은 추구 서버 설정에 맞게 변경해 나갈 예정입니다.
- 지금까지 설정한 정보는 다음과 같습니다.

1. authentication(인증서버) 로드벨런싱
2. RewritePath 지정
3. Cookie Header 제외
4. __특정 경로로 접근 시 Authenticaion Header 검증(Authentication Server 마저 구축 후 설정)__

# Information
회원가입 : http://localhost:8000/auth/signup
로그인 : http://localhost:8000/auth/login
사용자 정보 : http://localhost:8000/auth/{userId}
DB값 전체 parsing : http://localhost:8000/parse/getAllData