package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.dto.BusVehicleApiResponse;
import org.alljeju.alljejuserver.mapper.BusVehicleMapper;
import org.alljeju.alljejuserver.model.BusVehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusVehicleService {

    private final BusVehicleMapper busVehicleMapper;
    
    @Value("${api.bus.location.key}")
    private String apiKey;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void fetchAndSaveBusVehicles() {
        try {
            log.info("버스 차량 정보 조회 API 호출 시작");
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/BusVehicle";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=1&numOfRows=100";
            
            log.info("API 요청 URL: {}", fullUrl);
            
            // RestTemplate 사용하여 XML 응답 직접 받기
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            log.info("API 응답 상태 코드: {}", response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Jackson XML 매퍼를 사용하여 수동으로 XML 문자열을 객체로 변환
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    BusVehicleApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusVehicleApiResponse.class);
                    
                    if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                        List<BusVehicle> busVehicles = convertToBusVehicles(apiResponse);
                        
                        if (!busVehicles.isEmpty()) {
                            busVehicleMapper.batchInsert(busVehicles);
                            log.info("버스 차량 정보 저장 완료: {} 건", busVehicles.size());
                        } else {
                            log.warn("조회된 버스 차량 정보가 없습니다.");
                        }
                        
                        // 데이터가 많을 경우 페이징 처리
                        int totalPages = (apiResponse.getBody().getTotalCount() + apiResponse.getBody().getNumOfRows() - 1) / apiResponse.getBody().getNumOfRows();
                        
                        if (totalPages > 1) {
                            for (int page = 2; page <= totalPages; page++) {
                                fetchAndSaveBusVehiclePage(page, apiResponse.getBody().getNumOfRows());
                            }
                        }
                    } else {
                        log.error("버스 차량 정보 조회 API 에러: {}", 
                            apiResponse != null ? apiResponse.getHeader().getResultMsg() : "응답 처리 실패");
                    }
                } catch (Exception e) {
                    log.error("XML 파싱 중 오류 발생", e);
                    throw new RuntimeException("XML 파싱 중 오류 발생", e);
                }
            } else {
                log.error("API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("버스 차량 정보 조회 중 오류 발생", e);
            throw new RuntimeException("버스 차량 정보 조회 중 오류 발생", e);
        }
    }
    
    private void fetchAndSaveBusVehiclePage(int pageNo, int numOfRows) {
        try {
            log.info("버스 차량 정보 페이지 {} 조회", pageNo);
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/BusVehicle";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                XmlMapper xmlMapper = new XmlMapper();
                BusVehicleApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusVehicleApiResponse.class);
                
                if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                    List<BusVehicle> busVehicles = convertToBusVehicles(apiResponse);
                    if (!busVehicles.isEmpty()) {
                        busVehicleMapper.batchInsert(busVehicles);
                        log.info("버스 차량 정보 페이지 {} 저장 완료: {} 건", pageNo, busVehicles.size());
                    }
                }
            }
        } catch (Exception e) {
            log.error("버스 차량 정보 페이지 {} 조회 중 오류 발생", pageNo, e);
        }
    }
    
    private List<BusVehicle> convertToBusVehicles(BusVehicleApiResponse response) {
        if (response.getBody() == null || response.getBody().getItems() == null || 
            response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        
        return response.getBody().getItems().getItemList().stream()
                .map(item -> {
                    BusVehicle vehicle = new BusVehicle();
                    vehicle.setVhId(item.getVhId());
                    vehicle.setPlateNo(item.getPlateNo());
                    vehicle.setLowPlateTp(item.getLowPlateTp());
                    vehicle.setUseYn(item.getUseYn());
                    
                    try {
                        if (item.getUpd() != null && !item.getUpd().isEmpty()) {
                            vehicle.setUpd(LocalDateTime.parse(item.getUpd(), DATE_FORMATTER));
                        }
                    } catch (Exception e) {
                        log.warn("날짜 변환 중 오류: {}", e.getMessage());
                    }
                    
                    return vehicle;
                })
                .collect(Collectors.toList());
    }
    
    public List<BusVehicle> getLatestBusVehicles(int limit) {
        return busVehicleMapper.findLatest(limit);
    }
    
    public BusVehicle getBusVehicleByVehicleId(String vhId) {
        return busVehicleMapper.findByVehicleId(vhId);
    }
    
    public List<BusVehicle> searchBusVehiclesByPlateNo(String keyword, int limit) {
        return busVehicleMapper.findByPlateNo(keyword, limit);
    }
    
    public List<BusVehicle> getActiveBusVehicles() {
        return busVehicleMapper.findActiveVehicles();
    }
}
