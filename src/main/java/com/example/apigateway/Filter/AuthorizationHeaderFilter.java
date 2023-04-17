package com.example.apigateway.Filter;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import com.google.common.net.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;
    public AuthorizationHeaderFilter(Environment env){
        super(Config.class);
        this.env = env;
    }
    public static class Config {

    }

    /**
     * 로그인 시 사용되는 Filter입니다.
     * JWT토큰의 Header를 파싱해 AUTHORIZATION에 토큰 값이 있는지 가져와 검증함.
     * @param config
     * @return
     */
    // login -> token -> users(with token) -> header(if include token)
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Authentication Header가 있는지 파악
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            // jwt 토큰을 확인했을 때 Bearer형식으로 Token이 발급되므로 Bearer 이후로부터 accessToken으로 인식
            String jwt = authorizationHeader.replace("Bearer", "");

            // Create a cookie object
//            ServerHttpResponse response = exchange.getResponse();
//            ResponseCookie c1 = ResponseCookie.from("my_token", "test1234").maxAge(60 * 60 * 24).build();
//            response.addCookie(c1);

            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    /**
     * JWT 토큰이 유효한지 판별하는 메소드입니다.
     * @param jwt
     * @return boolean
     * @author : 황시준
     * @since : 1.0
     */
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;
        System.out.println(new Date(System.currentTimeMillis()));

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception ex) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

    /**
     *
     * @param exchange
     * @param err
     * @param httpStatus
     * @return Mono Type
     * @author : 황시준
     * @since : 1.0
     */
    // Spring Cloud Gateway는 Spring MVC로 구성하지 않는다.
    // Spring Functional API 방식으로 처리하는데 WebFlux 단위로 Mono라는 단일값을 사용한다.
    // Mono, Flux -> Spring WebFlux
    // WebFlux는 클라이언트, 서버에서 reactive 스타일의 어플리케이션 개발을 도와주는 모듈이며,
    // Mono는 0 ~ 1 개의 데이터를 전달한다.
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        // log.error(err);
        return response.setComplete();
    }
}
