package org.alljeju.alljejuserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebClientConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://grap.co.kr")
            .allowedOrigins("http://localhost:3000")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "POST", "OPTIONS", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 16MB 버퍼 크기 설정
                    // XML 디코더 등록 부분 수정
                })
                .build();

        return webClientBuilder
                .exchangeStrategies(strategies)
                .defaultHeader("Accept", MediaType.APPLICATION_XML_VALUE)
                .build();
    }
}
