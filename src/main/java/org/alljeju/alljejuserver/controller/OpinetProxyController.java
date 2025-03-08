package org.alljeju.alljejuserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/opinet")
public class OpinetProxyController {
    
    @Value("${opinet.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    
    public OpinetProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @GetMapping("/api/aroundAll.do")
    public ResponseEntity<String> getAroundAll(
            @RequestParam String x,
            @RequestParam String y,
            @RequestParam String radius,
            @RequestParam(defaultValue = "B027") String prodcd,
            @RequestParam(defaultValue = "1") String sort
    ) {
        String url = UriComponentsBuilder
            .fromHttpUrl("https://www.opinet.co.kr/api/aroundAll.do")
            .queryParam("code", apiKey)
            .queryParam("x", x)
            .queryParam("y", y)
            .queryParam("radius", radius)
            .queryParam("prodcd", prodcd)
            .queryParam("sort", sort)
            .queryParam("out", "json")
            .build()
            .toUriString();
            
        return restTemplate.getForEntity(url, String.class);
    }

    @GetMapping("/api/lowTop10.do")
    public ResponseEntity<String> getLowTop10(
            @RequestParam(defaultValue = "B027") String prodcd,
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "20") String cnt,
            @RequestParam(defaultValue = "json") String out
    ) {
        try {
            String url = UriComponentsBuilder
                .fromHttpUrl("https://www.opinet.co.kr/api/lowTop10.do")
                .queryParam("code", apiKey)
                .queryParam("prodcd", prodcd)
                .queryParam("area", area)
                .queryParam("cnt", cnt)
                .queryParam("out", out)
                .build()
                .toUriString();
                
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(
                "{\"error\": \"주유소 정보를 가져오는데 실패했습니다.\"}",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    // 에러 응답을 위한 DTO
    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private String error;
    }
}