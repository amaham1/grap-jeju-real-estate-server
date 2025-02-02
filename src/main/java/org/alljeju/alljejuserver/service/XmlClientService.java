package org.alljeju.alljejuserver.service;

import org.alljeju.alljejuserver.model.RealEstate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class XmlClientService {
    
    private final RestTemplate restTemplate;
    
    public XmlClientService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
            .messageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8))
            .build();
    }
    
    public ResponseEntity<String> getXmlData(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        
        System.out.println("Calling API URL: " + url);
        
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            String encodedUrl = builder.build(false).toUriString();
            
            ResponseEntity<String> response = restTemplate.exchange(
                encodedUrl,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            return response;
        } catch (RestClientException e) {
            System.out.println("API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}