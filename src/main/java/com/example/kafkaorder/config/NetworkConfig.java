package com.example.kafkaorder.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NetworkConfig {

    /**
     * Tomcat 서버 설정을 커스터마이징하여 외부 IP 접근을 최적화
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            // 모든 네트워크 인터페이스에서 연결 허용
            factory.setAddress(null); // null로 설정하면 모든 인터페이스에서 접근 가능
            
            // 커넥션 풀 설정
            factory.addConnectorCustomizers(connector -> {
                // 최대 커넥션 수 설정
                connector.setProperty("maxConnections", "8192");
                // 최대 스레드 수 설정
                connector.setProperty("maxThreads", "200");
                // 최소 스레드 수 설정
                connector.setProperty("minSpareThreads", "10");
                // 연결 타임아웃 설정
                connector.setProperty("connectionTimeout", "20000");
                // Keep-Alive 타임아웃 설정
                connector.setProperty("keepAliveTimeout", "15000");
                // 최대 Keep-Alive 요청 수
                connector.setProperty("maxKeepAliveRequests", "100");
            });
        };
    }
} 