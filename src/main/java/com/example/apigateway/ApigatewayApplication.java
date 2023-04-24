package com.example.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableEurekaClient
public class ApigatewayApplication {
    private final Environment env;

    @Autowired
    public ApigatewayApplication(Environment env){
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayApplication.class, args);
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in User Service on PORT "
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration time=" + env.getProperty("token.expiration_time"));

    }

    @Bean
    public HttpTraceRepository httpTraceRepository(){
        return new InMemoryHttpTraceRepository();
    }

}
